import json,concurrent.futures,urllib.request,pathlib
root=pathlib.Path(__file__).resolve().parents[1]
rows=json.loads((root/'photo-sources.json').read_text()); dest=root/'app/src/main/assets/photography';dest.mkdir(exist_ok=True)
def fetch(pair):
 i,row=pair; f=dest/f'photo-{i+1:02}.jpg'
 if not f.exists():
  with urllib.request.urlopen(row['image'],timeout=45) as r: f.write_bytes(r.read())
 from PIL import Image
 with Image.open(f) as im: im.verify()
 print(f.name,flush=True)
with concurrent.futures.ThreadPoolExecutor(max_workers=5) as pool:list(pool.map(fetch,enumerate(rows)))
