package com.example.spatialalbum.p20261115.ui.album.components
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.design.windows.AlertDialog
import com.example.spatialalbum.p20261115.domain.model.*
import com.example.spatialalbum.p20261115.ui.thought.*
import java.io.File
import kotlin.math.sin
@Composable fun ThoughtGenerationPanel(modifier:Modifier,onDismiss:()->Unit) {
 val context=LocalContext.current
 val vm=remember {ThoughtViewModel(File(context.filesDir,"eeg-demo"))}
 val state by vm.state.collectAsState()
 DisposableEffect(vm){onDispose{vm.close()}}
 var prompt by remember {mutableStateOf("")}
 var mode by remember {mutableStateOf(true)}
 var emotion by remember {mutableStateOf(ThoughtEmotion.CALM)}
 var focus by remember {mutableFloatStateOf(50f)}
 var purchase by remember {mutableStateOf(false)}
 var devices by remember {mutableStateOf(false)}
 var preview by remember {mutableStateOf(false)}
 val recipe=thoughtRecipe(prompt,emotion,focus.toInt(),mode)
 Column(modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
  Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   Column(Modifier.weight(1f)) {
    Text("意念生成",style=PicoTheme.typography.headlineMedium,color=PicoTheme.colorScheme.labelPrimary)
    Text("用情绪与专注，描绘你的空间记忆",color=PicoTheme.colorScheme.labelSecondary)
   }
   Button(onClick={devices=true}){Text("佩戴脑机接口")}
   Button(onClick=onDismiss){Text("返回相册")}
  }
  Row(Modifier.weight(1f),horizontalArrangement=Arrangement.spacedBy(24.dp)) {
   Column(Modifier.width(300.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(14.dp)) {
    Text("脑机接口连接",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
    Text(state.device?:"尚未选择设备",color=PicoTheme.colorScheme.labelPrimary)
    Text("连接状态：未连接 · 设备 SDK 待接入",color=PicoTheme.colorScheme.labelSecondary)
    Text(if(state.running) "EEG 演示信号 · 非真实采集" else "EEG 通道 · 暂无真实信号",color=PicoTheme.colorScheme.labelPrimary)
    EegDemoWaves(state)
    Text("演示通道 CH1–CH3 · 50 Hz",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    Text("任务时长  %02d:%02d".format(state.elapsed/60,state.elapsed%60),color=PicoTheme.colorScheme.labelPrimary)
    Row(verticalAlignment=Alignment.CenterVertically) {
     Checkbox(checked=state.storage,onCheckedChange=vm::storage,enabled=!state.running)
     Text("保存演示 EEG 到本机",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelPrimary)
    }
    Text("EEG 存储：${state.bytes} B · ${if(state.storage) "演示 CSV" else "未开启"}",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    Text("真实 EEG：未采集 / 0 B",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    Button(onClick=vm::toggleTask,modifier=Modifier.fillMaxWidth()){Text(if(state.running) "停止演示任务" else "开始演示任务")}
    state.error?.let {Text(it,color=PicoTheme.colorScheme.labelPrimary)}
   }
   Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
    TextField(value=prompt,onValueChange={prompt=it.take(600);preview=false},placeholder={Text("描述你脑海里的画面，例如：日落海边，前景岩石，远处群山…")},modifier=Modifier.fillMaxWidth().height(110.dp))
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
     Button(onClick={mode=true;preview=false}) {Text(if(mode) "✓ 情绪" else "情绪")}
     Button(onClick={mode=false;preview=false}) {Text(if(!mode) "✓ 专注" else "专注")}
     Text("手动预览 · 非脑电识别",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    }
    if(mode) {
     ThoughtEmotion.entries.chunked(3).forEach {row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
      row.forEach {item->Button(onClick={emotion=item;preview=false},modifier=Modifier.weight(1f)){Text((if(emotion==item) "✓ " else "")+item.label)}}
     }}
    } else {
     Text("专注力 ${focus.toInt()}%",style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary)
     Slider(value=focus,onValueChange={focus=it;preview=false},valueRange=0f..100f,modifier=Modifier.fillMaxWidth())
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("0% · 舒缓",color=PicoTheme.colorScheme.labelSecondary);Text("100% · 聚焦",color=PicoTheme.colorScheme.labelSecondary)}
    }
    Text("画面描述预览",style=PicoTheme.typography.titleMedium,color=PicoTheme.colorScheme.labelPrimary)
    Text(recipe,style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    if(preview) {
     val sample=if(mode)emotion.sample else if(focus<34)"photo-11" else if(focus<67)"photo-02" else "photo-03"
     PhotoImage(File(context.filesDir,"photos/$sample.jpg.sbs.jpg").path,Modifier.fillMaxWidth().height(160.dp),true)
     Text("风格参考 · 现有示例照片，并非新生成结果",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    }
    Text("图像生成服务待接入；设备选择与参数均为前端体验。",style=PicoTheme.typography.bodySmall,color=PicoTheme.colorScheme.labelSecondary)
    Button(onClick={preview=true},modifier=Modifier.fillMaxWidth()){Text("预览生成方案")}
   }
  }
  Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(24.dp)) {
   Button(onClick={purchase=true},modifier=Modifier.width(300.dp)){Text("购买脑机接口")}
   Box(Modifier.weight(1f)){ProviderFooter()}
  }
 }
 if(purchase) AlertDialog(onDismissRequest={purchase=false},title={Text("购买脑机接口 / 商务合作")},content={
  Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
   Text("联系 PMTalk123，购买脑机接口或洽谈商务合作。")
   Text("PMTalk123",style=PicoTheme.typography.headlineMedium)
  }
 },buttons={Button(onClick={purchase=false}){Text("我知道了")}})
 if(devices) AlertDialog(onDismissRequest={devices=false},title={Text("佩戴脑机接口 · 选择设备")},content={
  Column(Modifier.width(440.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
   Text("沿用 PICO 设备组件；请先佩戴设备，再选择对应型号。")
   listOf("博瑞恩","BrainLink Pro").forEach {name->Button(onClick={vm.selectDevice(name);devices=false},modifier=Modifier.fillMaxWidth()){Text("选择 $name")}}
   Text("当前未接入真实设备 SDK，选择后仍显示未连接。演示波形不代表设备兼容性或真实脑电。",style=PicoTheme.typography.bodySmall)
  }
 },buttons={Button(onClick={devices=false}){Text("取消")}})
}
@Composable private fun EegDemoWaves(s:ThoughtState) {
 var phase by remember {mutableFloatStateOf(0f)}
 LaunchedEffect(s.running){while(s.running){kotlinx.coroutines.delay(50);phase+=.05f}}
 val ink=PicoTheme.colorScheme.labelPrimary
 val grid=PicoTheme.colorScheme.labelSecondary.copy(alpha=.25f)
 Row(Modifier.fillMaxWidth().height(160.dp)) {
  Column(Modifier.width(38.dp).fillMaxHeight(),verticalArrangement=Arrangement.SpaceAround){repeat(3){Text("CH${it+1}",style=PicoTheme.typography.bodySmall,color=ink)}}
  Canvas(Modifier.weight(1f).fillMaxHeight()) {
   val row=size.height/3
   repeat(3){channel->val center=row*(channel+.5f);drawLine(grid,Offset(0f,center),Offset(size.width,center));val path=Path()
    for(i in 0..150){val x=size.width*i/150f;val t=phase+i/50f;val y=center+if(s.running)(sin(t*(7+channel))*.6f+sin(t*19)*.2f)*row*.35f else 0f;if(i==0)path.moveTo(x,y)else path.lineTo(x,y)}
    drawPath(path,ink,style=Stroke(1.3.dp.toPx()))
   }
  }
 }
}
