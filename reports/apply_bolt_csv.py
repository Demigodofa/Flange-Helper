import json, re
from pathlib import Path
import pandas as pd

src_csv = Path(r'C:\Users\KevinPenfield\Downloads\Bolt_B1_clean_noMaxTempCols.csv')
root_path = Path('app/src/main/assets/flange_reference.json')

if not src_csv.exists():
    raise SystemExit(f"Missing CSV: {src_csv}")
if not root_path.exists():
    raise SystemExit(f"Missing JSON: {root_path}")

# Load
with root_path.open('r', encoding='utf-8') as f:
    data = json.load(f)
root = data['flange_helper_reference_data']

fasteners = root['fasteners']
bolt_grades = fasteners['boltGrades']

# Load CSV
_df = pd.read_csv(src_csv)

# Normalize column names
cols = list(_df.columns)

temp_cols = [c for c in cols if re.match(r'^S_\d+F_ksi$', c)]

temp_values = sorted([int(re.findall(r'\d+', c)[0]) for c in temp_cols])

# Map grade to internal key

def grade_key(row):
    spec_raw = str(row.get('spec','')).strip()
    grade = str(row.get('grade','')).strip()
    cls = str(row.get('class','')).strip()
    if cls == 'nan' or cls.lower() == 'none':
        cls = ''

    if spec_raw == 'SA-193':
        if grade == 'B7':
            return 'A193_B7'
        if grade == 'B7M':
            return 'A193_B7M'
        if grade == 'B16':
            return 'A193_B16'
        if grade == 'B8' and cls == 'Class 1':
            return 'A193_B8_Class1_304'
        if grade == 'B8M' and cls == 'Class 1':
            return 'A193_B8M_Class1_316'
    if spec_raw == 'SA-320':
        if grade == 'L7':
            return 'A320_L7'
        if grade == 'L7M':
            return 'A320_L7M'
    if spec_raw == 'SA-453':
        if grade == '660' and cls == 'Class A':
            return 'A453_660_ClassA'
        if grade == '660' and cls == 'Class B':
            return 'A453_660_ClassB'
        if grade == '660' and cls == 'Class C':
            return 'A453_660_ClassC'
        if grade == '660' and cls == 'Class D':
            return 'A453_660_ClassD'
    return None

# No inference: only use explicit S_###F_ksi cells
def explicit_temp_map(row):
    out = {}
    for t in temp_values:
        col = f'S_{t}F_ksi'
        v = row.get(col)
        if pd.isna(v):
            continue
        out[t] = float(v)
    return out

# Build allowable ranges by contiguous equal S

def to_temp_ranges(temp_map):
    if not temp_map:
        return []
    ranges = []
    temps = sorted(temp_map.keys())
    start = temps[0]
    prev = temps[0]
    current_s = temp_map[start]
    step = 50
    for t in temps[1:]:
        s = temp_map[t]
        if t == prev + step and s == current_s:
            prev = t
            continue
        ranges.append({'tMin': start, 'tMax': prev, 'S': current_s})
        start = t
        prev = t
        current_s = s
    ranges.append({'tMin': start, 'tMax': prev, 'S': current_s})
    return ranges

# Existing lookup for fallback
existing_strength = bolt_grades.get('strength_Sy_Su_min_ksi', {})
existing_allowable = bolt_grades.get('allowableStress_S_ksi_atTemp', {})

# Group rows by grade key
_df['grade_key'] = _df.apply(grade_key, axis=1)
_df = _df[_df['grade_key'].notna()].copy()

# Update strength and allowable for mapped grades
new_strength = {k: [] for k in existing_strength.keys()}
new_allowable = {k: [] for k in existing_allowable.keys()}

# helper to find existing range for fallback

def find_existing_strength(key, dia_min, dia_max):
    ranges = existing_strength.get(key, [])
    for r in ranges:
        if dia_min >= r.get('diaMin_in', 0.0) and dia_max <= r.get('diaMax_in', 1e9):
            return r
    return None

for key, group in _df.groupby('grade_key'):
    for _, row in group.iterrows():
        dia_min = row.get('dia_min_in')
        dia_max = row.get('dia_max_in')
        dia_min = 0.0 if pd.isna(dia_min) else float(dia_min)
        dia_max = 1e9 if pd.isna(dia_max) else float(dia_max)
        sy = row.get('yield_min_ksi')
        su = row.get('tensile_min_ksi')
        sy = None if (pd.isna(sy)) else float(sy)
        su = None if (pd.isna(su)) else float(su)
        if sy is None or su is None:
            existing = find_existing_strength(key, dia_min, dia_max)
            if existing:
                if sy is None:
                    sy = existing.get('Sy')
                if su is None:
                    su = existing.get('Su')
        # add strength
        if key in new_strength:
            new_strength[key].append({
                'diaMin_in': dia_min,
                'diaMax_in': dia_max,
                'Sy': sy,
                'Su': su
            })

        # allowable temps (explicit only)
        temp_map = explicit_temp_map(row)
        temp_ranges = [{'tMin': t, 'tMax': t, 'S': s} for t, s in sorted(temp_map.items())]
        if temp_ranges:
            new_allowable.setdefault(key, [])
            new_allowable[key].append({
                'diaMin_in': dia_min,
                'diaMax_in': dia_max,
                'temps': temp_ranges
            })

# Merge with existing: if no new data for a key, keep existing
for key, ranges in existing_strength.items():
    if key not in new_strength or not new_strength[key]:
        new_strength[key] = ranges

for key, ranges in existing_allowable.items():
    if key not in new_allowable or not new_allowable[key]:
        new_allowable[key] = ranges

bolt_grades['strength_Sy_Su_min_ksi'] = new_strength
bolt_grades['allowableStress_S_ksi_atTemp'] = new_allowable

with root_path.open('w', encoding='utf-8') as f:
    json.dump(data, f, indent=2)

print('Updated flange_reference.json from CSV')
