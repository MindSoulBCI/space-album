package com.example.spatialalbum.p20261115.data.repository
import com.example.spatialalbum.p20261115.domain.model.Photo
import java.io.File
interface PhotoRepository { fun load(): List<Photo>; fun delete(id: String) }
class LocalPhotoRepository(private val directory: File): PhotoRepository {
 override fun load() = directory.listFiles()?.filter { it.extension.lowercase() in setOf("jpg","png","mp4","webm","mov","mkv") && !it.name.endsWith(".sbs.jpg") && !it.name.endsWith(".thumb.jpg") }?.sortedBy { it.name }?.map {
  val video=it.extension.lowercase() in setOf("mp4","webm","mov","mkv")
  val stereo=File(it.path+".sbs.jpg").takeIf { f->f.exists() }
  val meta=File(it.path+".meta").takeIf { f->f.exists() }?.readLines().orEmpty()
  val layout=meta.firstOrNull()?:"MONO"
  Photo(it.name,it.nameWithoutExtension,it.absolutePath,it.length()+(stereo?.length()?:0)+File(it.path+".thumb.jpg").length(),stereo?.path,
   if(video) {if(layout=="SBS") "空间视频 · SBS" else "视频 · 2D"} else if(stereo!=null) "空间照片" else if(File(it.path+".pending").exists()) "生成中" else if(File(it.path+".error").exists()) "生成失败，可重试" else "待生成",video,layout,meta.getOrNull(1)?.toFloatOrNull()?:1.5f)
 }.orEmpty()
 override fun delete(id: String) { require(id == File(id).name); listOf("",".sbs.jpg",".error",".thumb.jpg",".meta").forEach {File(directory,id+it).delete()} }
}
