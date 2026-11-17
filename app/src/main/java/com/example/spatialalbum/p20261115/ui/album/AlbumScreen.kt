package com.example.spatialalbum.p20261115.ui.album
import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.spatialalbum.p20261115.ui.album.components.AlbumContent
import com.example.spatialalbum.p20261115.platform.ImportActivity
@Composable fun AlbumScreen(modifier: Modifier=Modifier,onImmersive: ()->Unit) {
 val context=LocalContext.current
 val vm=remember { AlbumViewModelFactory.create(context) }
 var guest by rememberSaveable {mutableStateOf(false)}
 var account by remember {mutableStateOf(false)}
 var available by remember {mutableLongStateOf(0)}
 var total by remember {mutableLongStateOf(1)}
 val state by vm.state.collectAsStateWithLifecycle()
 LaunchedEffect(Unit) { while(true) { vm.onEvent(AlbumEvent.Refresh); val disk=android.os.StatFs(context.filesDir.path);available=disk.availableBytes;total=disk.totalBytes; kotlinx.coroutines.delay(1000) } }
 if(!guest) com.example.spatialalbum.p20261115.ui.album.components.SignedOutContent(modifier,{account=true},{guest=true})
 else AlbumContent(state,vm::onEvent,{context.startActivity(Intent(context,ImportActivity::class.java))},onImmersive,{state.selected?.let {com.example.spatialalbum.p20261115.platform.DepthGenerator.enqueue(context.applicationContext,java.io.File(it.path))}},modifier,available,total,{account=true},{state.selected?.takeIf {it.isVideo}?.let {photo->java.io.File(photo.path+".meta").writeText((if(photo.videoLayout=="SBS") "MONO" else "SBS")+"\n"+photo.aspect);vm.onEvent(AlbumEvent.Refresh)}},{guest=false})
 if(account) com.example.spatialalbum.p20261115.ui.account.AccountDialog(onDismiss={account=false},onSuccess={guest=true})
}
