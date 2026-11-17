package com.example.spatialalbum.p20261115.ui.album.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.design.windows.AlertDialog
import com.pico.spatial.ui.foundation.hover.spatialHoverEffect
import com.example.spatialalbum.p20261115.R
import com.example.spatialalbum.p20261115.ui.album.*
import com.example.spatialalbum.p20261115.domain.usecase.AlbumUseCase
@Composable fun AlbumContent(s: AlbumUiState,onEvent: (AlbumEvent)->Unit,onImport: ()->Unit,onImmersive: ()->Unit,onGenerate: ()->Unit,modifier: Modifier=Modifier,availableBytes:Long=0,totalBytes:Long=1,onAccount:()->Unit={},onVideoLayout:()->Unit={},onExitGuest:()->Unit={}) {
 if(s.dialog=="thought") {ThoughtGenerationPanel(modifier){onEvent(AlbumEvent.Dialog(null))};return}
 val pageSize=AlbumUseCase.PAGE_SIZE
 val grid=rememberLazyGridState()
 LaunchedEffect(s.page) {grid.scrollToItem(0)}
 Column(modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=Alignment.CenterVertically) {
   Image(painterResource(R.drawable.album_logo_foreground),"空间相册 APP 图标",Modifier.size(52.dp))
   Column(Modifier.weight(1f)) {
    Text("空间相册",style=PicoTheme.typography.headlineMedium,color=PicoTheme.colorScheme.labelPrimary)
    Text("让你的空间记忆都在这里...",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
   }
   Column(Modifier.width(260.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
    val photoBytes=s.photos.filterNot {it.isVideo}.sumOf {it.bytes};val videoBytes=s.photos.filter {it.isVideo}.sumOf {it.bytes}
    Text("本机相册 · ${storageSize(photoBytes+videoBytes)}",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelPrimary)
    LinearProgressIndicator(progress={((photoBytes+videoBytes).toDouble()/totalBytes.coerceAtLeast(1)).toFloat().coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
    Text("照片 ${storageSize(photoBytes)} · 视频 ${storageSize(videoBytes)}",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    Text("可用 ${storageSize(availableBytes)} / ${storageSize(totalBytes)}",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
   }
   Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
    Button(onClick=onAccount) {Text("登录 / 注册")}
    Button(onClick=onExitGuest) {Text("退出游客")}
   }
  }
  Row(Modifier.weight(1f),horizontalArrangement=Arrangement.spacedBy(24.dp)) {
   Column(Modifier.weight(1.05f),verticalArrangement=Arrangement.spacedBy(12.dp)) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
     Text("全部 ${s.photos.size} 项 · 3 列",Modifier.weight(1f),color=PicoTheme.colorScheme.labelPrimary)
     Button(onClick={onEvent(AlbumEvent.Dialog("import"))}) {Text("导入照片 / 视频")}
     Spacer(Modifier.width(8.dp))
     Button(onClick={onEvent(AlbumEvent.Dialog("thought"))}) {Text("意念生成")}
    }
    if(s.photos.isEmpty()) Text("还没有内容，导入第一份空间记忆",color=PicoTheme.colorScheme.labelPrimary)
    LazyVerticalGrid(GridCells.Fixed(3),state=grid,modifier=Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
     items(s.photos.drop(s.page*pageSize).take(pageSize),key={it.id}) { photo ->
      Column(Modifier.clip(RoundedCornerShape(12.dp)).spatialHoverEffect().clickable {onEvent(AlbumEvent.Select(photo.id))}.padding(6.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
       PhotoImage(if(photo.isVideo) photo.path+".thumb.jpg" else photo.stereoPath ?: photo.path,Modifier.fillMaxWidth().height(94.dp),!photo.isVideo&&photo.stereoPath!=null)
       Text((if(photo.id==s.selectedId) "✓ " else "")+photo.title,maxLines=1,style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelPrimary)
       Text(if(photo.isVideo) "▶ ${photo.status}" else photo.status,maxLines=1,style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
      }
     }
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.CenterVertically) {
     Text("${s.page+1} / ${((s.photos.size+pageSize-1)/pageSize).coerceAtLeast(1)} · 每页 9 行",Modifier.weight(1f),style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
     Button(onClick={onEvent(AlbumEvent.Page(-1))},enabled=s.page>0) {Text("上一页")}
     Button(onClick={onEvent(AlbumEvent.Page(1))},enabled=(s.page+1)*pageSize<s.photos.size) {Text("下一页")}
    }
   }
   Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(12.dp),horizontalAlignment=Alignment.CenterHorizontally) {
    Text(s.selected?.title ?: "内容预览",color=PicoTheme.colorScheme.labelPrimary)
    s.selected?.let { photo ->
     if(photo.isVideo) key(photo.id,photo.videoLayout) {VideoPreview(photo,Modifier.fillMaxWidth().weight(1f))}
     else PhotoImage(photo.stereoPath ?: photo.path,Modifier.fillMaxWidth().weight(1f),photo.stereoPath!=null)
    } ?: Spacer(Modifier.weight(1f))
    Text(s.selected?.status?.let { "$it · 本机保存" } ?: "选择照片或视频",color=PicoTheme.colorScheme.labelSecondary)
    if(s.selected?.isVideo==true) Button(onClick=onVideoLayout) {Text(if(s.selected?.videoLayout=="SBS") "切换为普通 2D" else "按左右 SBS 空间视频播放")}
    if(s.selected!=null && s.selected?.isVideo==false && s.selected?.stereoPath==null) Button(onClick=onGenerate,enabled=s.selected?.status!="生成中") {Text("生成空间照片")}
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
     if(s.selected?.isVideo!=true) Button(onClick=onImmersive,enabled=s.selected?.stereoPath!=null) {Text("进入沉浸")}
     Button(onClick={onEvent(AlbumEvent.Dialog("delete"))},enabled=s.selected!=null) {Text("删除")}
    }
   }
  }
  ProviderFooter()
 }
 if(s.dialog!=null && s.dialog!="thought") AlertDialog(onDismissRequest={onEvent(AlbumEvent.Dialog(null))},title={Text(if(s.dialog=="delete") "删除这项内容？" else "导入照片或视频")},content={Text(if(s.dialog=="delete") "仅删除本相册内的副本，不影响系统相册。" else "照片会在本机生成空间版本。视频保留原格式，导入后点击即可播放；左右眼视频可选择 SBS 模式。")},buttons={
  Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
   Button(onClick={onEvent(AlbumEvent.Dialog(null))}) {Text("取消")}
   Button(onClick={if(s.dialog=="delete") onEvent(AlbumEvent.Delete) else {onEvent(AlbumEvent.Dialog(null));onImport()}}) {Text(if(s.dialog=="delete") "删除" else "选择文件")}
  }
 })
}
fun storageSize(bytes:Long):String = if(bytes>=1073741824L) "%.1f GB".format(bytes/1073741824.0) else "%.1f MB".format(bytes/1048576.0)
