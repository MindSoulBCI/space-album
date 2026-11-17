package com.example.spatialalbum.p20261115
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.spatialalbum.p20261115.ui.intro.IntroScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.*
import com.pico.spatial.ui.platform.containers.*
import com.example.spatialalbum.p20261115.ui.album.AlbumScreen
import com.example.spatialalbum.p20261115.ui.gallery.GalleryScene
import kotlinx.coroutines.launch
fun mainApp(scope: SpatialAppScope) = with(scope) {
 DefaultWindowContainer {
  PicoTheme {
   val nav=LocalSpatialNavigator.current
   val coroutine=rememberCoroutineScope()
   var introDone by rememberSaveable { mutableStateOf(false) }
   val bounds=Modifier.windowConstraints(minWidth=720.dp,minHeight=540.dp,maxWidth=1440.dp,maxHeight=900.dp)
   if(!introDone) IntroScreen(bounds) {introDone=true}
   else AlbumScreen(bounds) {
    coroutine.launch { nav.openStage("Gallery",StageStyle.Mixed) }
   }
  }
 }
 Stage(id="Gallery") { PicoTheme { GalleryScene() } }
}
