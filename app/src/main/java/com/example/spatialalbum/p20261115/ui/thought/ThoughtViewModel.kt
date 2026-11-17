package com.example.spatialalbum.p20261115.ui.thought
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.math.sin
data class ThoughtState(val device:String?=null,val running:Boolean=false,val elapsed:Int=0,val storage:Boolean=false,val bytes:Long=0,val signal:List<Float> = listOf(0f,0f,0f),val error:String?=null)
class ThoughtViewModel(private val directory:File):ViewModel() {
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
 private val mutable=MutableStateFlow(ThoughtState())
 val state=mutable.asStateFlow()
 private var job:Job?=null
 fun selectDevice(name:String){mutable.update {it.copy(device=name)}}
 fun storage(enabled:Boolean){if(!mutable.value.running)mutable.update {it.copy(storage=enabled)}}
 fun toggleTask() {
  if(mutable.value.running){stop();return}
  val save=mutable.value.storage
  mutable.update {it.copy(running=true,elapsed=0,bytes=0,error=null)}
  job=scope.launch {
   var output:java.io.BufferedWriter?=null
   var file:File?=null
   try {
    if(save){directory.mkdirs();file=File(directory,"eeg-demo-${System.currentTimeMillis()}.csv");output=file.bufferedWriter();output.write("# SYNTHETIC DEMO, NOT RECORDED EEG\ntime_ms,CH1,CH2,CH3\n")}
    val started=System.nanoTime();var sample=0L
    while(isActive) {
     val t=sample/50.0
     val values=(0..2).map {(sin(t*(7+it))*18+sin(t*19)*5).toFloat()}
     output?.write("${sample*20},${values.joinToString(",")}\n")
     if(sample%10==0L){output?.flush();mutable.update {it.copy(elapsed=((System.nanoTime()-started)/1_000_000_000).toInt(),bytes=file?.length()?:0,signal=values)}}
     sample++;delay(20)
    }
   } catch(e:CancellationException){throw e} catch(_:Exception){mutable.update {it.copy(error="演示数据写入失败，请检查存储空间")}}
   finally {output?.close();mutable.update {it.copy(running=false,bytes=file?.length()?:it.bytes)}}
  }
 }
 fun stop(){job?.cancel();mutable.update {it.copy(running=false)}}
 fun close(){scope.cancel()}
 override fun onCleared(){close()}
}
