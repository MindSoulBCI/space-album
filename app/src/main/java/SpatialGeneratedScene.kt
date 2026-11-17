package com.example.spatialalbum.p20261115.ui.gallery
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.ui.album.AlbumViewModelFactory
import com.example.spatialalbum.p20261115.ui.album.components.PhotoImage
import kotlinx.coroutines.launch
import kotlin.math.*
@Composable fun GalleryScene() {
 val context=LocalContext.current
 val vm=remember { AlbumViewModelFactory.create(context) }
 val photos=remember { vm.state.value.photos.filter {it.stereoPath!=null}.take(8) }
 val nav=LocalSpatialNavigator.current
 val coroutine=rememberCoroutineScope()
 val entities=remember { mutableListOf<Entity>() }
 var paused by remember { mutableStateOf(false) }
 SpatialView(modifier=Modifier.fillMaxSize(),attachments={
  photos.forEach { photo -> AttachmentPanel(photo.id) { PhotoImage(photo.stereoPath ?: photo.path,Modifier.size(464.dp,324.dp).border(12.dp,PicoTheme.colorScheme.labelPrimaryLight).padding(12.dp),true) } }
  AttachmentPanel("controls") {
   Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.spacedBy(16.dp)) {
    Button(onClick={paused=!paused}) {Text(if(paused) "继续环绕" else "暂停环绕")}
    Button(onClick={coroutine.launch {nav.restoreWindowContainer("YourPlanarWindowContainer");nav.closeStage()}}) {Text("退出沉浸")}
   }
  }
 }) { content,attachments ->
  val root=Entity();content.addEntity(root)
  photos.forEachIndexed { index,photo -> attachments.entity(photo.id)?.let { entity ->
   val a=(index-(photos.size-1)/2f)*0.65f
   entity.components.set(TransformComponent(Vector3(sin(a)*1.8f,2.0f,-cos(a)*1.8f),EulerAngles(0f,-a*180f/PI.toFloat(),0f),Vector3(1f,1f,1f)))
   root.addChild(entity);entities.add(entity)
  } }
  attachments.entity("controls")?.let { entity -> entity.components.set(TransformComponent().apply {setPosition(Vector3(0f,1.25f,-1.4f))});root.addChild(entity) }
  nav.minimizeWindowContainer("YourPlanarWindowContainer")
 }
 LaunchedEffect(paused) {
  if(!paused) {
   var last=0L
   while(true) { withFrameNanos { now ->
    if(last!=0L) {val delta=(now-last)/1e9f*0.075f
     entities.forEach {entity -> entity.components[TransformComponent::class.java]?.let { t ->
      val x=t.position.x;val z=t.position.z
      t.setPosition(Vector3(x*cos(delta)-z*sin(delta),t.position.y,x*sin(delta)+z*cos(delta)))
     }}
    };last=now
   } }
  }
 }
 DisposableEffect(Unit) {onDispose {entities.clear()}}
}
