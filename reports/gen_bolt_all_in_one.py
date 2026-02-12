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


def range_key(r):
    return (float(r.get('diaMin_in', 0.0)), float(r.get('diaMax_in', 0.0)))

def find_range(ranges, rmin, rmax):
    for r in ranges:
        mn = float(r.get('diaMin_in', 0.0))
        mx = float(r.get('diaMax_in', 0.0))
        if rmin >= mn and rmax <= mx:
            return r
    return None

def temps_to_str(temps):
    parts = []
    missing = False
    for t in temps:
        tmin = t.get('tMin')
        tmax = t.get('tMax')
        sval = t.get('S')
        if sval is None or (isinstance(sval, str) and 'PLACEHOLDER' in sval):
            missing = True
        if tmin == tmax:
            parts.append(f"{tmin}: {sval}")
        else:
            parts.append(f"{tmin}-{tmax}: {sval}")
    return '; '.join(parts), missing

rows = []
for grade in options:
    s_ranges = strength.get(grade, [])
    a_ranges = allowable.get(grade, [])

    # Union of dia ranges
    ranges = set()
    for r in s_ranges:
        ranges.add(range_key(r))
    for r in a_ranges:
        ranges.add(range_key(r))

    if not ranges:
        rows.append({
            'grade': grade,
            'dia_min_in': '',
            'dia_max_in': '',
            'Sy_ksi': '',
            'Su_ksi': '',
            'allowable_S_ksi_by_tempF': 'NO_ALLOWABLE_DATA',
            'missing_flags': 'NO_STRENGTH_DATA;NO_ALLOWABLE_DATA'
        })
        continue

    for rmin, rmax in sorted(ranges):
        s_match = find_range(s_ranges, rmin, rmax) or next(iter(s_ranges), None)
        a_match = find_range(a_ranges, rmin, rmax)

        sy = s_match.get('Sy') if s_match else None
        su = s_match.get('Su') if s_match else None
        missing = []
        if not s_ranges:
            missing.append('NO_STRENGTH_DATA')
        else:
            if isinstance(sy, str) and 'PLACEHOLDER' in sy:
                missing.append('Sy_placeholder')
            if isinstance(su, str) and 'PLACEHOLDER' in su:
                missing.append('Su_placeholder')

        if a_match and a_match.get('temps'):
            temp_str, temp_missing = temps_to_str(a_match.get('temps'))
            if temp_missing:
                missing.append('Allowable_placeholder')
        else:
            temp_str = 'NO_ALLOWABLE_DATA'
            missing.append('NO_ALLOWABLE_DATA')

        rows.append({
            'grade': grade,
            'dia_min_in': rmin,
            'dia_max_in': rmax,
            'Sy_ksi': sy,
            'Su_ksi': su,
            'allowable_S_ksi_by_tempF': temp_str,
            'missing_flags': ';'.join(missing) if missing else ''
        })

csv_path = out_dir / 'Bolt_AllInOne.csv'
with csv_path.open('w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['grade','dia_min_in','dia_max_in','Sy_ksi','Su_ksi','allowable_S_ksi_by_tempF','missing_flags'])
    for r in rows:
        writer.writerow([
            r['grade'], r['dia_min_in'], r['dia_max_in'], r['Sy_ksi'], r['Su_ksi'],
            r['allowable_S_ksi_by_tempF'], r['missing_flags']
        ])

# Pretty text table
col_names = ['grade','dia_min_in','dia_max_in','Sy_ksi','Su_ksi','allowable_S_ksi_by_tempF','missing_flags']
col_widths = {c: len(c) for c in col_names}
for r in rows:
    for c in col_names:
        col_widths[c] = max(col_widths[c], len(str(r[c])))

lines = []
header = ' | '.join(c.ljust(col_widths[c]) for c in col_names)
lines.append(header)
lines.append('-+-'.join('-' * col_widths[c] for c in col_names))
for r in rows:
    lines.append(' | '.join(str(r[c]).ljust(col_widths[c]) for c in col_names))

text_path = out_dir / 'Bolt_AllInOne.txt'
text_path.write_text('\n'.join(lines), encoding='utf-8')

print('Wrote:', csv_path, text_path)
