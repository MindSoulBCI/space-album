package com.example.spatialalbum.p20261115.platform
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.File
class ImportActivity: Activity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); if(savedInstanceState==null) startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type="*/*";putExtra(Intent.EXTRA_MIME_TYPES,arrayOf("image/*","video/*")); addCategory(Intent.CATEGORY_OPENABLE) },10) }
 override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
  super.onActivityResult(requestCode,resultCode,data)
  val uri=data?.data
  if(resultCode!=RESULT_OK || uri==null) { finish();return }
  Thread {
   val directory=File(filesDir,"photos").apply {mkdirs()}
   val tmp=File.createTempFile("import-",".tmp",directory)
   try {
    val mime=contentResolver.getType(uri).orEmpty()
    contentResolver.openInputStream(uri)!!.use { input -> tmp.outputStream().use { input.copyTo(it) } }
    if(mime.startsWith("video/")) {
     val retriever=MediaMetadataRetriever()
     try {
      retriever.setDataSource(tmp.path)
      val w=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloatOrNull()?:0f
      val h=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloatOrNull()?:0f
      require(w>0&&h>0)
      val extension=when(mime){"video/webm"->"webm";"video/quicktime"->"mov";"video/x-matroska"->"mkv";else->"mp4"}
      val target=File(directory,"视频-${System.currentTimeMillis()}.$extension")
      val thumb=retriever.getFrameAtTime(0)
      check(tmp.renameTo(target))
      File(target.path+".meta").writeText("MONO\n${w/h}")
      thumb?.let { b->File(target.path+".thumb.jpg").outputStream().use { b.compress(android.graphics.Bitmap.CompressFormat.JPEG,80,it) };b.recycle() }
     } finally {retriever.release()}
    } else {
     val opts=BitmapFactory.Options().apply { inJustDecodeBounds=true }; BitmapFactory.decodeFile(tmp.path,opts);require(opts.outWidth>0)
     val target=File(directory,"照片-${System.currentTimeMillis()}.jpg");check(tmp.renameTo(target));DepthGenerator.enqueue(applicationContext,target)
    }
   } catch(e: Exception) { runOnUiThread { android.widget.Toast.makeText(this,"导入失败：格式不支持或本机容量不足",android.widget.Toast.LENGTH_LONG).show() } }
   finally {tmp.delete()}
   runOnUiThread { finish() }
  }.start()
 }
}
