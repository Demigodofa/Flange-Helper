import json, csv
from pathlib import Path

root = Path('app/src/main/assets/flange_reference.json')
with root.open('r', encoding='utf-8') as f:
    data = json.load(f)['flange_helper_reference_data']

bolt_grades = data['fasteners']['boltGrades']
strength = bolt_grades.get('strength_Sy_Su_min_ksi', {})
allowable = bolt_grades.get('allowableStress_S_ksi_atTemp', {})
options = bolt_grades.get('options', [])

out_dir = Path('reports')
out_dir.mkdir(exist_ok=True)

strength_csv = out_dir / 'Bolt_Strength_RoomTemp.csv'
with strength_csv.open('w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['grade_key','dia_min_in','dia_max_in','Sy_ksi','Su_ksi','missing'])
    for grade in options:
        ranges = strength.get(grade, [])
        if not ranges:
            writer.writerow([grade,'','','','','NO_STRENGTH_DATA'])
            continue
        for r in ranges:
            sy = r.get('Sy')
            su = r.get('Su')
            missing = ''
            if isinstance(sy, str) and 'PLACEHOLDER' in sy:
                missing = 'Sy_placeholder'
            if isinstance(su, str) and 'PLACEHOLDER' in su:
                missing = (missing + ';' if missing else '') + 'Su_placeholder'
            writer.writerow([grade, r.get('diaMin_in',''), r.get('diaMax_in',''), sy, su, missing])

allowable_csv = out_dir / 'Bolt_AllowableStress_Temp.csv'
with allowable_csv.open('w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['grade_key','dia_min_in','dia_max_in','temp_min_F','temp_max_F','S_ksi','missing'])
    for grade in options:
        ranges = allowable.get(grade, [])
        if not ranges:
            writer.writerow([grade,'','','','','','NO_ALLOWABLE_DATA'])
            continue
        for r in ranges:
            for t in r.get('temps', []):
                s = t.get('S')
                missing = ''
                if s is None or (isinstance(s, str) and 'PLACEHOLDER' in s):
                    missing = 'S_placeholder'
                writer.writerow([grade, r.get('diaMin_in',''), r.get('diaMax_in',''), t.get('tMin',''), t.get('tMax',''), s, missing])

missing_txt = out_dir / 'Bolt_Missing_Data.txt'
missing_allowable = [g for g in options if not allowable.get(g)]
missing_strength = []
placeholder_strength = []
for g in options:
    ranges = strength.get(g, [])
    if not ranges:
        missing_strength.append(g)
    for r in ranges:
        for key in ('Sy','Su'):
            val = r.get(key)
            if isinstance(val, str) and 'PLACEHOLDER' in val:
                placeholder_strength.append(g)
                break

with missing_txt.open('w', encoding='utf-8') as f:
    f.write('Missing Allowable Stress at Temperature (no entries):\n')
    f.write(', '.join(missing_allowable) + ('\n\n' if missing_allowable else 'None\n\n'))
    f.write('Missing Strength Ranges (no entries):\n')
    f.write(', '.join(missing_strength) + ('\n\n' if missing_strength else 'None\n\n'))
    f.write('Strength Placeholders Present:\n')
    f.write(', '.join(sorted(set(placeholder_strength))) + ('\n' if placeholder_strength else 'None\n'))

print('Wrote:', strength_csv, allowable_csv, missing_txt)
