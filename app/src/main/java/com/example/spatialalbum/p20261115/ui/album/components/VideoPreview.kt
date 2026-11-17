package com.example.spatialalbum.p20261115.ui.album.components
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.VideoPlayerComponent
import com.pico.spatial.core.ecs.resource.*
import com.pico.spatial.core.ecs.video.*
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.layout.DepthAlignment
import com.pico.spatial.ui.foundation.layout.alignDepth
import com.pico.spatial.ui.foundation.layout.depth
import com.pico.spatial.ui.platform.LengthUnit
import com.pico.spatial.ui.platform.LocalPhysicalLengthConverter
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.domain.model.Photo
@Composable fun VideoPreview(photo:Photo,modifier:Modifier=Modifier,showControls:Boolean=true,onCompleted:()->Unit={},onPlaybackError:()->Unit={},resumeOnForeground:Boolean=false) {
 val completed by rememberUpdatedState(onCompleted)
 val failed by rememberUpdatedState(onPlaybackError)
 val player=remember {CypressMediaPlayer()}
 var status by remember {mutableStateOf("正在准备视频…")}
 var ready by remember {mutableStateOf(false)}
 var playing by remember {mutableStateOf(false)}
 var position by remember {mutableLongStateOf(0)}
 var duration by remember {mutableLongStateOf(1)}
 val handler=remember {Handler(Looper.getMainLooper())}
 val lifecycle=LocalLifecycleOwner.current.lifecycle
 DisposableEffect(player) {
  var active=true
  fun update(block:()->Unit) {handler.post {if(active) block()}}
  player.registerCypressMediaPlayerCallback(object:CypressMediaPlayerCallback {
   override fun onPrepared(){update {ready=true;duration=player.getDuration().coerceAtLeast(1);player.play();status=""}}
   override fun onStarted(){update {playing=true;status=""}}
   override fun onPaused(){update {playing=false}}
   override fun onStopped(){update {playing=false}}
   override fun onCompleted(){update {playing=false;position=duration;completed()}}
   override fun onSeekToCompleted(){}
   override fun onVideoSizeChanged(width:Int,height:Int){}
   override fun onError(error:CypressMediaPlayerErrorCode){update {status="视频无法播放，请检查文件或编码";ready=false;playing=false;failed()}}
  })
  val observer=LifecycleEventObserver {_,event->if(event==Lifecycle.Event.ON_PAUSE&&player.valid&&ready) player.pause();if(event==Lifecycle.Event.ON_RESUME&&resumeOnForeground&&player.valid&&ready)player.play()}
  lifecycle.addObserver(observer)
  onDispose {active=false;lifecycle.removeObserver(observer);player.unregisterCypressMediaPlayerCallback();player.close()}
 }
 LaunchedEffect(player) {while(true) {if(ready&&player.valid) position=player.getCurrentPosition().coerceAtLeast(0);kotlinx.coroutines.delay(400)}}
 BoxWithConstraints(modifier.alignDepth(DepthAlignment.DepthBack),contentAlignment=Alignment.Center) {
  val aspect=(photo.aspect/(if(photo.videoLayout=="SBS")2f else 1f)).coerceAtLeast(.2f)
  val width=minOf(maxWidth,(maxHeight-(if(showControls)80.dp else 0.dp)).coerceAtLeast(80.dp)*aspect);val height=width/aspect
  val converter=LocalPhysicalLengthConverter.current
  val wm=converter.dpToLength(width,LengthUnit.Meters);val hm=converter.dpToLength(height,LengthUnit.Meters)
  val radius=converter.dpToLength(16.dp,LengthUnit.Meters)
  val entity=remember {Entity().apply {components.set(TransformComponent().apply {setPosition(Vector3(0f,0f,0.002f))})}}
  var prepared by remember {mutableStateOf(false)}
  DisposableEffect(wm,hm,photo.videoLayout) {
   val mesh=MeshResource.createVideoPanel(wm,hm,radius)
   val material=VideoMaterial(BlendingMode.OPAQUE,if(photo.videoLayout=="SBS")VideoDimensionMode.SIDE_BY_SIDE else VideoDimensionMode.MONO,MaterialCullingMode.BACK)
   entity.components.set(VideoPlayerComponent(player,mesh,material))
   if(!prepared){prepared=true;player.setDataSource(photo.path);player.prepareAsync()}
   onDispose { }
  }
  DisposableEffect(entity) {onDispose {entity.destroy()}}
  Column(Modifier.width(width).alignDepth(DepthAlignment.DepthBack),verticalArrangement=Arrangement.spacedBy(8.dp)) {
   Box(Modifier.width(width).height(height).alignDepth(DepthAlignment.DepthBack),contentAlignment=Alignment.Center) {
    SpatialView(modifier=Modifier.fillMaxSize().depth(8.dp),initial={content,_->content.addEntity(entity)})
    if(status.isNotEmpty()) Text(status,color=PicoTheme.colorScheme.labelPrimary)
   }
   if(showControls) {
   LinearProgressIndicator(progress={(position.toFloat()/duration).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalAlignment=Alignment.CenterVertically) {
    Button(enabled=ready,onClick={if(playing)player.pause() else if(position>=duration){player.seekTo(0);player.play()}else player.play()}) {Text(if(playing)"暂停" else "播放")}
    Button(enabled=ready,onClick={player.seekTo(0);player.play()}) {Text("重播")}
    Text("${position/1000}s / ${duration/1000}s",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
   }
   }
  }
 }
}
