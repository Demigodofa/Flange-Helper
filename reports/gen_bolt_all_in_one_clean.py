import json, csv
from pathlib import Path

root = Path('app/src/main/assets/flange_reference.json')
with root.open('r', encoding='utf-8') as f:
    data = json.load(f)['flange_helper_reference_data']

fasteners = data['fasteners']
bolt_grades = fasteners['boltGrades']
strength = bolt_grades.get('strength_Sy_Su_min_ksi', {})
allowable = bolt_grades.get('allowableStress_S_ksi_atTemp', {})
options = bolt_grades.get('options', [])

# Map grade key to spec/grade/class text
GRADE_MAP = {
    'A193_B7': ('A/SA-193', 'B7', ''),
    'A193_B16': ('A/SA-193', 'B16', ''),
    'A193_B8_Class1_304': ('A/SA-193', 'B8', 'Class 1 (304)'),
    'A193_B8M_Class1_316': ('A/SA-193', 'B8M', 'Class 1 (316)'),
    'A320_L7': ('A/SA-320', 'L7', ''),
    'A193_B7M': ('A/SA-193', 'B7M', ''),
    'A320_L7M': ('A/SA-320', 'L7M', ''),
    'A453_660_ClassA': ('A/SA-453', '660', 'Class A'),
    'A453_660_ClassB': ('A/SA-453', '660', 'Class B'),
    'A453_660_ClassC': ('A/SA-453', '660', 'Class C'),
    'A453_660_ClassD': ('A/SA-453', '660', 'Class D'),
}

# build temperature columns (50 F increments)
all_temps = set()
for grade, ranges in allowable.items():
    for r in ranges:
        for t in r.get('temps', []):
            tmin = int(t.get('tMin'))
            tmax = int(t.get('tMax'))
            for temp in range(tmin, tmax + 1, 50):
                all_temps.add(temp)

if not all_temps:
    all_temps = set(range(100, 1001, 50))

sorted_temps = sorted(all_temps)

# helper to union ranges

def range_key(r):
    return (float(r.get('diaMin_in', 0.0)), float(r.get('diaMax_in', 0.0)))

def union_ranges(grade):
    ranges = set()
    for r in strength.get(grade, []):
        ranges.add(range_key(r))
    for r in allowable.get(grade, []):
        ranges.add(range_key(r))
    return sorted(ranges)


def find_range(ranges, rmin, rmax):
    for r in ranges:
        mn = float(r.get('diaMin_in', 0.0))
        mx = float(r.get('diaMax_in', 0.0))
        if rmin >= mn and rmax <= mx:
            return r
    return None


def build_temp_map(temps):
    out = {}
    max_temp = None
    for t in temps:
        tmin = int(t.get('tMin'))
        tmax = int(t.get('tMax'))
        sval = t.get('S')
        if max_temp is None or tmax > max_temp:
            max_temp = tmax
        for temp in range(tmin, tmax + 1, 50):
            out[temp] = sval
    return out, max_temp

rows = []
for grade in options:
    spec, gname, gclass = GRADE_MAP.get(grade, ('', grade, ''))
    s_ranges = strength.get(grade, [])
    a_ranges = allowable.get(grade, [])
    ranges = union_ranges(grade)
    if not ranges:
        rows.append({
            'spec': spec,
            'grade': gname,
            'class': gclass,
            'grade_key': grade,
            'dia_min_in': '',
            'dia_max_in': '',
            'Sy_ksi': '',
            'Su_ksi': '',
            'max_temp_F': '',
            'missing_flags': 'NO_STRENGTH_DATA;NO_ALLOWABLE_DATA'
        })
        continue

    for rmin, rmax in ranges:
        s_match = find_range(s_ranges, rmin, rmax) or (s_ranges[0] if s_ranges else None)
        a_match = find_range(a_ranges, rmin, rmax)
        sy = s_match.get('Sy') if s_match else None
        su = s_match.get('Su') if s_match else None

        temp_map = {}
        max_temp = None
        missing = []
        if not s_ranges:
            missing.append('NO_STRENGTH_DATA')
        else:
            if isinstance(sy, str) and 'PLACEHOLDER' in sy:
                missing.append('Sy_placeholder')
            if isinstance(su, str) and 'PLACEHOLDER' in su:
                missing.append('Su_placeholder')

        if a_match:
            temp_map, max_temp = build_temp_map(a_match.get('temps', []))
        else:
            missing.append('NO_ALLOWABLE_DATA')

        row = {
            'spec': spec,
            'grade': gname,
            'class': gclass,
            'grade_key': grade,
            'dia_min_in': rmin,
            'dia_max_in': rmax,
            'Sy_ksi': sy,
            'Su_ksi': su,
            'max_temp_F': max_temp,
            'missing_flags': ';'.join(missing) if missing else ''
        }
        for temp in sorted_temps:
            if max_temp is not None and temp > max_temp:
                row[f'T{temp}F'] = ''
            else:
                row[f'T{temp}F'] = temp_map.get(temp, 'NOT_AVAILABLE')
        rows.append(row)

columns = ['spec','grade','class','grade_key','dia_min_in','dia_max_in','Sy_ksi','Su_ksi','max_temp_F','missing_flags'] + [f'T{t}F' for t in sorted_temps]

out_dir = Path('reports')
out_dir.mkdir(exist_ok=True)

csv_path = out_dir / 'Bolt_AllInOne_Clean.csv'
with csv_path.open('w', newline='', encoding='utf-8') as f:
    writer = csv.DictWriter(f, fieldnames=columns)
    writer.writeheader()
    for r in rows:
        writer.writerow(r)

# Plain text table (first 40 columns to keep readable)
text_path = out_dir / 'Bolt_AllInOne_Clean.txt'
show_cols = columns[:10] + columns[10:20]  # spec columns + first 10 temps
col_widths = {c: len(c) for c in show_cols}
for r in rows:
    for c in show_cols:
        col_widths[c] = max(col_widths[c], len(str(r.get(c,''))))

lines = []
lines.append('NOTE: Temps are in 50F steps. For inputs that are not a 50F increment, round UP to the next 50F.')
lines.append('')
header = ' | '.join(c.ljust(col_widths[c]) for c in show_cols)
lines.append(header)
lines.append('-+-'.join('-' * col_widths[c] for c in show_cols))
for r in rows:
    lines.append(' | '.join(str(r.get(c,'')).ljust(col_widths[c]) for c in show_cols))

text_path.write_text('\n'.join(lines), encoding='utf-8')

print('Wrote:', csv_path, text_path)
