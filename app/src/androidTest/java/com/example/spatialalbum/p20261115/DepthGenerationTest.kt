package com.example.spatialalbum.p20261115
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.File
import android.graphics.BitmapFactory
import com.example.spatialalbum.p20261115.platform.DepthGenerator
@RunWith(AndroidJUnit4::class)
class DepthGenerationTest {
 @Test fun generatesDifferentEyesOnDevice() {
  val context=InstrumentationRegistry.getInstrumentation().targetContext
  val original=File(context.cacheDir,"depth-validation.jpg")
  context.assets.open("photography/photo-01.jpg").use {input->original.outputStream().use {input.copyTo(it)}}
  DepthGenerator.generate(context,original)
  val result=BitmapFactory.decodeFile(original.path+".sbs.jpg")
  assertNotNull(result);assertTrue(result.width>result.height)
  val half=result.width/2;var differences=0
  for(y in 0 until result.height step 8)for(x in 0 until half step 8)if(result.getPixel(x,y)!=result.getPixel(x+half,y))differences++
  assertTrue("Left/right eye images must differ",differences>100)
  File(original.path+".sbs.jpg").delete();original.delete()
 }
}
