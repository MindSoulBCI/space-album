package com.example.spatialalbum.p20261115.ui.album.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.R
@Composable fun SignedOutContent(modifier:Modifier,onLogin:()->Unit,onGuest:()->Unit) {
 Column(modifier.fillMaxSize().padding(start=32.dp,top=32.dp,end=32.dp,bottom=8.dp),verticalArrangement=Arrangement.spacedBy(24.dp)) {
  Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
   Image(painterResource(R.drawable.album_logo_foreground),"空间相册",Modifier.size(64.dp))
   Column(Modifier.weight(1f)) {
    Text("空间相册",style=PicoTheme.typography.headlineLarge,color=PicoTheme.colorScheme.labelPrimary)
    Text("让你的空间记忆都在这里...",color=PicoTheme.colorScheme.labelSecondary)
   }
   Text("未登录",color=PicoTheme.colorScheme.labelSecondary)
  }
  Row(Modifier.weight(1f).fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(48.dp),verticalAlignment=Alignment.CenterVertically) {
   Column(Modifier.weight(1.2f),verticalArrangement=Arrangement.spacedBy(24.dp),horizontalAlignment=Alignment.CenterHorizontally) {
    Image(painterResource(R.drawable.album_logo_foreground),"",Modifier.size(152.dp))
    Text("登录后查看你的空间记忆",style=PicoTheme.typography.headlineMedium,color=PicoTheme.colorScheme.labelPrimary)
    Text("照片、视频与沉浸相册，在这里相聚。",color=PicoTheme.colorScheme.labelSecondary)
    Button(onClick=onLogin) {Text("邮箱登录 / 注册")}
   }
   Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(20.dp),horizontalAlignment=Alignment.CenterHorizontally) {
    Text("先感受空间记忆",style=PicoTheme.typography.headlineMedium,color=PicoTheme.colorScheme.labelPrimary)
    Text("通过示例照片与视频，体验立体预览和环绕画廊。",color=PicoTheme.colorScheme.labelSecondary)
    Button(onClick=onGuest) {Text("游客模式体验")}
    Text("无需登录 · 使用本机示例内容",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
   }
  }
  ProviderFooter()
 }
}
