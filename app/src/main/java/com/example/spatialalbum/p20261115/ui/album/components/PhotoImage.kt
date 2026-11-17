package com.example.spatialalbum.p20261115.ui.album.components
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.stereoImage.StereoImage
import com.pico.spatial.ui.design.stereoImage.TextureLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
@Composable fun PhotoImage(path: String, modifier: Modifier = Modifier, stereo: Boolean = false) {
 val bitmap by produceState<android.graphics.Bitmap?>(null,path) { value=withContext(Dispatchers.IO) { BitmapFactory.decodeFile(path,BitmapFactory.Options().apply { inSampleSize=2 }) } }
 bitmap?.let { b -> BoxWithConstraints(modifier,contentAlignment=Alignment.Center) {
 val aspect=b.width.toFloat()/(if(stereo)2 else 1)/b.height
 val width=minOf(maxWidth,maxHeight*aspect)
 val shape=Modifier.width(width).height(width/aspect).clip(RoundedCornerShape(16.dp))
 if(stereo) StereoImage(b.asImageBitmap(),TextureLayout.SideBySide,shape)
 else Image(b.asImageBitmap(),"照片",shape,contentScale=ContentScale.Fit)
 } } ?: Box(modifier,contentAlignment=Alignment.Center) {Text("正在读取内容")}
}
