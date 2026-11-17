package com.example.spatialalbum.p20261115.platform
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import ai.onnxruntime.*
import java.io.File
import java.nio.FloatBuffer
import java.util.concurrent.Executors
import kotlin.math.*
object DepthGenerator {
 private val queue=Executors.newSingleThreadExecutor()
 fun enqueue(context: Context, file: File) {queue.execute {
  val pending=File(file.path+".pending");pending.writeText("生成中")
  try { generate(context,file);File(file.path+".error").delete() }
  catch(e:Exception) {File(file.path+".error").writeText(e.message ?: "生成失败")}
  finally {pending.delete()}
 }}
 @Synchronized fun generate(context: Context,file: File) {
  val model=File(context.filesDir,"depth.onnx")
  if(!model.exists()) {val tmp=File(context.filesDir,"depth.tmp");context.assets.open("models/depth.onnx").use { i->tmp.outputStream().use {i.copyTo(it)} };check(tmp.renameTo(model))}
  val options=BitmapFactory.Options().apply {inJustDecodeBounds=true};BitmapFactory.decodeFile(file.path,options)
  var sample=1;while(max(options.outWidth,options.outHeight)/sample>1600) sample*=2
  var image=BitmapFactory.decodeFile(file.path,BitmapFactory.Options().apply {inSampleSize=sample}) ?: error("无法解码照片")
  val exif=ExifInterface(file.path);val orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)
  val matrix=Matrix().apply {when(orientation){2->setScale(-1f,1f);3->setRotate(180f);4->setScale(1f,-1f);5->{setRotate(90f);postScale(-1f,1f)};6->setRotate(90f);7->{setRotate(270f);postScale(-1f,1f)};8->setRotate(270f)}}
  image=Bitmap.createBitmap(image,0,0,image.width,image.height,matrix,true)
  val ratio=min(1f,960f/max(image.width,image.height));image=Bitmap.createScaledBitmap(image,(image.width*ratio).roundToInt(),(image.height*ratio).roundToInt(),true)
  val w=image.width;val h=image.height;val sw=518f/w;val sh=518f/h;val scale=if(abs(1-sw)<abs(1-sh))sw else sh
  val iw=(w*scale/14).roundToInt()*14;val ih=(h*scale/14).roundToInt()*14
  val pixels=IntArray(w*h);image.getPixels(pixels,0,w,0,0,w,h)
  val floats=FloatArray(3*iw*ih);val means=floatArrayOf(.485f,.456f,.406f);val std=floatArrayOf(.229f,.224f,.225f)
  fun cubic(v:Float):Float {val x=abs(v);return if(x<=1)1.5f*x*x*x-2.5f*x*x+1 else if(x<2)-.5f*x*x*x+2.5f*x*x-4*x+2 else 0f}
  for(y in 0 until ih)for(x in 0 until iw){val sx=(x+.5f)*w/iw-.5f;val sy=(y+.5f)*h/ih-.5f;val bx=floor(sx).toInt();val by=floor(sy).toInt()
   for(c in 0..2){var value=0f;var weight=0f
    for(dy in -1..2)for(dx in -1..2){val q=cubic(sx-bx-dx)*cubic(sy-by-dy);val color=pixels[(by+dy).coerceIn(0,h-1)*w+(bx+dx).coerceIn(0,w-1)];value+=((color shr (16-8*c)) and 255)*q;weight+=q}
    floats[c*iw*ih+y*iw+x]=(value.div(weight).coerceIn(0f,255f)/255-means[c])/std[c]
   }
  }
  val env=OrtEnvironment.getEnvironment()
  OrtSession.SessionOptions().use { settings ->settings.setIntraOpNumThreads(2)
   env.createSession(model.path,settings).use {session ->
    OnnxTensor.createTensor(env,FloatBuffer.wrap(floats),longArrayOf(1,3,ih.toLong(),iw.toLong())).use {input ->session.run(mapOf("pixel_values" to input)).use {result ->
     val tensor=result[0] as OnnxTensor;val shape=tensor.info.shape;val dh=shape[shape.size-2].toInt();val dw=shape.last().toInt();val depth=FloatArray(dw*dh);tensor.floatBuffer.get(depth)
     require(depth.all {it.isFinite()});val low=depth.min();val range=depth.max()-low;require(range>1e-5f){"未检测到有效景深"}
     val out=IntArray(w*2*h)
     for(y in 0 until h)for(x in 0 until w){val d=(depth[(y.toLong()*dh/h).toInt()*dw+(x.toLong()*dw/w).toInt()]-low)/range;val shift=((d-.5f)*w*.012f).toInt();out[y*w*2+x]=pixels[y*w+(x-shift).coerceIn(0,w-1)];out[y*w*2+w+x]=pixels[y*w+(x+shift).coerceIn(0,w-1)]}
     val stereo=Bitmap.createBitmap(out,w*2,h,Bitmap.Config.ARGB_8888);val tmp=File(file.path+".sbs.tmp")
     tmp.outputStream().use {check(stereo.compress(Bitmap.CompressFormat.JPEG,92,it))};check(tmp.renameTo(File(file.path+".sbs.jpg")));stereo.recycle()
    }}
   }
  };image.recycle()
 }
}
