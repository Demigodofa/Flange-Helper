import json, csv
from pathlib import Path

root = Path('app/src/main/assets/flange_reference.json')
with root.open('r', encoding='utf-8') as f:
    data = json.load(f)['flange_helper_reference_data']

fasteners = data['fasteners']
tpi_lookup = fasteners['tpi_lookup']
as_lookup = fasteners['tensileStressArea_As_in2_lookup']
strength_lookup = fasteners['boltGrades']['strength_Sy_Su_min_ksi']
allowable_lookup = fasteners['boltGrades'].get('allowableStress_S_ksi_atTemp', {})

scenarios = [
    {"grade":"A193_B7", "dia":"1", "series":"8UN", "temp":600, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A193_B7", "dia":"1", "series":"8UN", "temp":650, "pct":0.45, "lube":"Moly paste", "k":0.11},
    {"grade":"A193_B7", "dia":"1-1/2", "series":"8UN", "temp":700, "pct":0.50, "lube":"Never-Seez Regular", "k":0.13},
    {"grade":"A193_B7", "dia":"2", "series":"8UN", "temp":750, "pct":0.40, "lube":"Copper/Nickel anti-seize", "k":0.15},
    {"grade":"A193_B7", "dia":"1/2", "series":"UNC", "temp":550, "pct":0.35, "lube":"Dry", "k":0.27},

    {"grade":"A193_B16", "dia":"2-1/2", "series":"8UN", "temp":900, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A193_B16", "dia":"3", "series":"8UN", "temp":650, "pct":0.55, "lube":"High-temp blends", "k":0.17},
    {"grade":"A193_B16", "dia":"4", "series":"8UN", "temp":500, "pct":0.45, "lube":"Moly paste", "k":0.11},

    {"grade":"A193_B7M", "dia":"1", "series":"8UN", "temp":650, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A193_B7M", "dia":"2", "series":"8UN", "temp":700, "pct":0.45, "lube":"Never-Seez Regular", "k":0.13},

    {"grade":"A320_L7", "dia":"1", "series":"8UN", "temp":650, "pct":0.50, "lube":"Copper/Nickel anti-seize", "k":0.15},
    {"grade":"A320_L7", "dia":"2", "series":"8UN", "temp":550, "pct":0.40, "lube":"Dry", "k":0.27},

    {"grade":"A320_L7M", "dia":"1", "series":"8UN", "temp":650, "pct":0.50, "lube":"High-temp blends", "k":0.17},
    {"grade":"A320_L7M", "dia":"2", "series":"8UN", "temp":700, "pct":0.45, "lube":"Moly paste", "k":0.11},

    {"grade":"A193_B8_Class1_304", "dia":"1", "series":"8UN", "temp":300, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A193_B8M_Class1_316", "dia":"1", "series":"8UN", "temp":650, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A193_B8M_Class1_316", "dia":"2", "series":"8UN", "temp":1000, "pct":0.45, "lube":"Copper/Nickel anti-seize", "k":0.15},

    {"grade":"A453_660_ClassA", "dia":"1", "series":"8UN", "temp":950, "pct":0.50, "lube":"Dry", "k":0.27},
    {"grade":"A453_660_ClassB", "dia":"1", "series":"8UN", "temp":1000, "pct":0.50, "lube":"Moly paste", "k":0.11},
    {"grade":"A453_660_ClassC", "dia":"1", "series":"8UN", "temp":1000, "pct":0.50, "lube":"High-temp blends", "k":0.17},
]


def parse_diameter(value):
    if '-' in value and '/' in value:
        whole, frac = value.split('-')
        num, den = frac.split('/')
        return float(whole) + float(num) / float(den)
    if '/' in value:
        num, den = value.split('/')
        return float(num) / float(den)
    return float(value)


def lookup_sy(grade, dia):
    ranges = strength_lookup.get(grade, [])
    for r in ranges:
        if dia >= r['diaMin_in'] and dia <= r['diaMax_in']:
            sy = r.get('Sy')
            return sy if isinstance(sy, (int, float)) else None
    return None


def lookup_allowable(grade, dia, temp):
    ranges = allowable_lookup.get(grade, [])
    if not ranges:
        return None, None
    for r in ranges:
        if dia >= r['diaMin_in'] and dia <= r['diaMax_in']:
            temps = r.get('temps', [])
            # direct match
            for t in temps:
                if temp >= t['tMin'] and temp <= t['tMax']:
                    return t['S'], t['tMax']
            # round up to next highest
            candidates = [t for t in temps if t['tMax'] >= temp]
            if candidates:
                chosen = sorted(candidates, key=lambda x: x['tMax'])[0]
                return chosen['S'], chosen['tMax']
    return None, None


rows = []
for idx, sc in enumerate(scenarios, start=1):
    dia_key = sc['dia']
    d_in = parse_diameter(dia_key)
    series = sc['series']
    tpi = tpi_lookup.get(series, {}).get(dia_key)
    as_in2 = as_lookup.get(series, {}).get(dia_key)
    if as_in2 is None and tpi is not None:
        term = d_in - (0.9743 / tpi)
        as_in2 = 0.7854 * term * term

    sy = lookup_sy(sc['grade'], d_in)
    allow_s, used_temp = lookup_allowable(sc['grade'], d_in, sc['temp'])
    strength_used = allow_s if allow_s is not None else sy

    pct = sc['pct']
    f = as_in2 * (strength_used * 1000.0) * pct if (as_in2 and strength_used) else None
    torque = (sc['k'] * d_in * f) / 12.0 if f is not None else None

    rows.append({
        'scenario': idx,
        'grade': sc['grade'],
        'diameter_in': dia_key,
        'thread_series': series,
        'tpi': tpi,
        'As_in2': as_in2,
        'working_temp_F': sc['temp'],
        'used_temp_F': used_temp,
        'Sy_or_allowable_ksi': strength_used,
        'pct_yield': pct,
        'bolt_load_F_lbf': f,
        'lube': sc['lube'],
        'K': sc['k'],
        'target_torque_ftlb': torque,
        'pass1_30pct_ftlb': (torque * 0.30) if torque is not None else None,
        'pass2_60pct_ftlb': (torque * 0.60) if torque is not None else None,
        'pass3_100pct_ftlb': torque,
    })

out_dir = Path('reports')
out_dir.mkdir(exist_ok=True)

csv_path = out_dir / 'Torque_Matrix_20.csv'
with csv_path.open('w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(list(rows[0].keys()))
    for r in rows:
        writer.writerow([r[k] for k in r.keys()])

print('Wrote:', csv_path)
