package com.example.spatialalbum.p20261115.ui.album.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.R
@Composable fun ProviderFooter() {
 val context=LocalContext.current
 Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally) {
 Link(onClick={try {context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://mindsoul.cn")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))}catch(_:android.content.ActivityNotFoundException){Toast.makeText(context,"请使用浏览器访问 mindsoul.cn",Toast.LENGTH_LONG).show()}}) {
 Row(Modifier,horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically) {
  Image(painterResource(R.drawable.mindsoul_support),"MindSoul",Modifier.size(48.dp))
  Spacer(Modifier.width(12.dp))
  Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
   Text("由 MindSoul 脑机接口基座大模型提供支持 ↗",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)

  }
 }
 }
 Text("由成都市脑云矩阵科技有限公司提供",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
 }
}
