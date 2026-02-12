import json, csv
from pathlib import Path

root = Path('app/src/main/assets/flange_reference.json')
with root.open('r', encoding='utf-8') as f:
    data = json.load(f)['flange_helper_reference_data']

nuts = data['fasteners']['nutGrades']['options']

out_dir = Path('reports')
out_dir.mkdir(exist_ok=True)

csv_path = out_dir / 'Nut_Grades_List.csv'
with csv_path.open('w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['nut_grade'])
    for n in nuts:
        writer.writerow([n])

print('Wrote:', csv_path)
