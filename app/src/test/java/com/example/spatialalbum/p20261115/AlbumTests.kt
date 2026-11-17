package com.example.spatialalbum.p20261115
import org.junit.Test
import org.junit.Assert.*
import com.example.spatialalbum.p20261115.data.repository.PhotoRepository
import com.example.spatialalbum.p20261115.domain.model.Photo
import com.example.spatialalbum.p20261115.domain.usecase.AlbumUseCase
import com.example.spatialalbum.p20261115.ui.album.*
class FakePhotos:PhotoRepository {var photos=(1..25).map {Photo("$it","Photo $it","/$it",100)};override fun load()=photos;override fun delete(id:String){photos=photos.filterNot {it.id==id}}}
class AlbumUseCaseTest {
 @Test fun pageHoldsTwentySevenItems(){val r=FakePhotos();val items=(1..55).map {Photo("$it","Photo $it","/$it",100)};val u=AlbumUseCase(r);assertEquals(27,u.page(items,0).size);assertEquals("28",u.page(items,1).first().id);assertEquals(1,u.page(items,2).size)}
 @Test fun pagingClampsAtBothEnds(){val u=AlbumUseCase(FakePhotos());assertEquals(0,u.move(0,-1,25));assertEquals(2,u.move(2,1,55));assertEquals(0,u.move(2,1,0))}
}
class AlbumViewModelTest {
 @Test fun loadsTwentyFivePhotos(){assertEquals(25,AlbumViewModel(AlbumUseCase(FakePhotos())).state.value.photos.size)}
 @Test fun selectionSurvivesRefresh(){val v=AlbumViewModel(AlbumUseCase(FakePhotos()));v.onEvent(AlbumEvent.Select("8"));v.onEvent(AlbumEvent.Refresh);assertEquals("8",v.state.value.selectedId)}
 @Test fun deleteSelectsRemainingPhoto(){val v=AlbumViewModel(AlbumUseCase(FakePhotos()));v.onEvent(AlbumEvent.Delete);assertEquals(24,v.state.value.photos.size);assertEquals("2",v.state.value.selectedId)}
 @Test fun emptyLibraryHasNoSelection(){val r=FakePhotos();r.photos=emptyList();val v=AlbumViewModel(AlbumUseCase(r));v.onEvent(AlbumEvent.Page(1));assertNull(v.state.value.selected);assertEquals(0,v.state.value.page)}
}

class MixedMediaRepositoryTest {
 @Test fun mediaCountsSidecarsOnceAndDeletesTogether() {
  val dir=java.nio.file.Files.createTempDirectory("album-test").toFile()
  try {
   java.io.File(dir,"a.jpg").writeBytes(ByteArray(10))
   java.io.File(dir,"a.jpg.sbs.jpg").writeBytes(ByteArray(20))
   java.io.File(dir,"b.mp4").writeBytes(ByteArray(40))
   java.io.File(dir,"b.mp4.thumb.jpg").writeBytes(ByteArray(5))
   java.io.File(dir,"b.mp4.meta").writeText("SBS\n2.0")
   val r=com.example.spatialalbum.p20261115.data.repository.LocalPhotoRepository(dir)
   val media=r.load();assertEquals(2,media.size);assertEquals(75L,media.sumOf{it.bytes})
   assertTrue(media[1].isVideo);assertEquals("SBS",media[1].videoLayout)
   r.delete("b.mp4");assertEquals(1,r.load().size);assertFalse(java.io.File(dir,"b.mp4.meta").exists())
  } finally {dir.deleteRecursively()}
 }
}

class ThoughtRecipeTest {
 @Test fun nineEmotionsProduceDistinctRecipes(){
  val values=com.example.spatialalbum.p20261115.domain.model.ThoughtEmotion.entries
  assertEquals(9,values.size)
  assertEquals(9,values.map {com.example.spatialalbum.p20261115.domain.model.thoughtRecipe("海边",it,50,true)}.distinct().size)
 }
 @Test fun focusClampsAndChangesStyle(){
  val e=com.example.spatialalbum.p20261115.domain.model.ThoughtEmotion.CALM
  val low=com.example.spatialalbum.p20261115.domain.model.thoughtRecipe("",e,-4,false)
  val high=com.example.spatialalbum.p20261115.domain.model.thoughtRecipe("",e,120,false)
  assertTrue(low.contains("0%"));assertTrue(high.contains("100%"));assertNotEquals(low,high)
 }
}
