package com.example.spatialalbum.p20261115.ui.intro
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.domain.model.Photo
import com.example.spatialalbum.p20261115.ui.album.components.VideoPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
@Composable fun IntroScreen(modifier:Modifier=Modifier,onEnter:()->Unit) {
 val context=LocalContext.current
 val enter by rememberUpdatedState(onEnter)
 var error by remember {mutableStateOf(false)}
 val media by produceState<Photo?>(null) {
  try {value=withContext(Dispatchers.IO) {
   val file=File(context.cacheDir,"album-opening-v1.mp4")
   if(!file.exists()) {
    val pending=File(context.cacheDir,"album-opening-v1.tmp")
    context.assets.open("intro/opening.mp4").use {input->pending.outputStream().use {input.copyTo(it)}}
    check(pending.renameTo(file))
   }
   val retriever=MediaMetadataRetriever()
   val aspect=try {
    retriever.setDataSource(file.path)
    val w=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloatOrNull()?:16f
    val h=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloatOrNull()?:9f
    val rotation=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()?:0
    if(rotation==90||rotation==270)h/w else w/h
   } finally {retriever.release()}
   Photo("opening","空间相册",file.path,file.length(),isVideo=true,aspect=aspect)
  }} catch(e:kotlinx.coroutines.CancellationException){throw e} catch(_:Exception){error=true}
 }
 LaunchedEffect(error){if(error)enter()}
 Column(modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
   Text("空间相册",style=PicoTheme.typography.headlineMedium,color=PicoTheme.colorScheme.labelPrimary)
   Button(onClick={enter()}) {Text("跳过")}
  }
  Box(Modifier.fillMaxWidth().weight(1f),contentAlignment=Alignment.Center) {
   media?.let {VideoPreview(it,Modifier.fillMaxSize(),showControls=false,onCompleted={enter()},onPlaybackError={enter()},resumeOnForeground=true)}
    ?: Text("正在载入开屏动画…",color=PicoTheme.colorScheme.labelSecondary)
  }
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
   Text("让你的空间记忆都在这里...",color=PicoTheme.colorScheme.labelPrimary)
   Button(onClick={enter()}) {Text("进入空间相册")}
  }
 }
}
