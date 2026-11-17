package com.example.spatialalbum.p20261115.ui.album
import android.content.Context
import com.example.spatialalbum.p20261115.data.repository.LocalPhotoRepository
import com.example.spatialalbum.p20261115.domain.usecase.AlbumUseCase
import java.io.File
object AlbumViewModelFactory {
 fun create(context: Context): AlbumViewModel {
  val dir=File(context.filesDir,"photos").apply { mkdirs() }
  val marker=File(context.filesDir,"seeded-photography-v2")
  if(!marker.exists()) {
   context.assets.list("photography").orEmpty().forEach { name -> context.assets.open("photography/$name").use { input -> File(dir,name).outputStream().use { input.copyTo(it) } } }
   context.assets.list("stereo").orEmpty().filter {it.endsWith(".sbs.jpg")}.forEach { name -> context.assets.open("stereo/$name").use {input -> File(dir,name.removeSuffix(".sbs.jpg")+".jpg.sbs.jpg").outputStream().use {input.copyTo(it)} } }
   (1..4).forEach { File(dir,"sample-$it.png").delete() }
   marker.writeText("1")
  }
  val videoMarker=File(context.filesDir,"seeded-video-v1")
  if(!videoMarker.exists()) {
   val video=File(dir,"00-洞穴空间视频示例.mp4")
   context.assets.open("videos/cave-sbs.mp4").use {input->video.outputStream().use {input.copyTo(it)}}
   File(video.path+".meta").writeText("SBS\n2.0")
   val retriever=android.media.MediaMetadataRetriever()
   try {
    retriever.setDataSource(video.path)
    retriever.getFrameAtTime(0)?.let {b->
     val eye=android.graphics.Bitmap.createBitmap(b,0,0,b.width/2,b.height)
     File(video.path+".thumb.jpg").outputStream().use {eye.compress(android.graphics.Bitmap.CompressFormat.JPEG,80,it)}
     eye.recycle();b.recycle()
    }
   } finally {retriever.release()}
   videoMarker.writeText("1")
  }
  return AlbumViewModel(AlbumUseCase(LocalPhotoRepository(dir)))
 }
}
