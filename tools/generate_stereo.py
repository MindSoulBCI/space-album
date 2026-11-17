from pathlib import Path
import numpy as np, onnxruntime as ort, json, hashlib
from PIL import Image,ImageOps
root=Path(__file__).resolve().parents[1]; assets=root/'app/src/main/assets';model=assets/'models/depth.onnx'
opt=ort.SessionOptions();opt.intra_op_num_threads=4
session=ort.InferenceSession(str(model),opt)
report=[]
for path in sorted((assets/'photography').glob('*.jpg')):
 image=ImageOps.exif_transpose(Image.open(path)).convert('RGB');image.thumbnail((960,960));w,h=image.size
 sw,sh=518/w,518/h; scale=sw if abs(1-sw)<abs(1-sh) else sh
 iw,ih=round(w*scale/14)*14,round(h*scale/14)*14
 inp=np.asarray(image.resize((iw,ih),Image.Resampling.BICUBIC),dtype=np.float32)/255
 inp=((inp-np.array([.485,.456,.406],np.float32))/np.array([.229,.224,.225],np.float32)).transpose(2,0,1)[None]
 depth=session.run(None,{'pixel_values':inp})[0].squeeze();lo,hi=float(depth.min()),float(depth.max());assert hi-lo>1e-5
 d=np.asarray(Image.fromarray((depth-lo)/(hi-lo)).resize((w,h),Image.Resampling.BICUBIC));d=np.clip(d,0,1)
 rgb=np.asarray(image);yy,xx=np.mgrid[:h,:w];shift=((d-.5)*w*.012).astype(int)
 left=rgb[yy,np.clip(xx-shift,0,w-1)];right=rgb[yy,np.clip(xx+shift,0,w-1)]
 out=assets/'stereo';out.mkdir(exist_ok=True)
 Image.fromarray(np.concatenate([left,right],axis=1)).save(out/(path.stem+'.sbs.jpg'),quality=92)
 Image.fromarray((d*255).astype(np.uint8)).save(out/(path.stem+'.depth.png'))
 report.append({'id':path.stem,'depthMin':lo,'depthMax':hi,'eyeDifferenceMean':float(np.abs(left.astype(float)-right).mean()),'size':[w,h]}); print(path.stem,flush=True)
(assets/'models/model-manifest.json').write_text(json.dumps({'model':'onnx-community/depth-anything-v2-small','sha256':hashlib.sha256(model.read_bytes()).hexdigest(),'input':'pixel_values NCHW RGB float32','resize':'preserve aspect, minimal scaling to 518 then round to 14','mean':[.485,.456,.406],'std':[.229,.224,.225],'stereoLayout':'SideBySide','strength':.012,'outputs':report},indent=2))
