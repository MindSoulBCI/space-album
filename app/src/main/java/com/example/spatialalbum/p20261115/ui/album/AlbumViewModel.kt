package com.example.spatialalbum.p20261115.ui.album
import androidx.lifecycle.ViewModel
import com.example.spatialalbum.p20261115.domain.usecase.AlbumUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
class AlbumViewModel(private val useCase: AlbumUseCase): ViewModel() {
 private val mutable = MutableStateFlow(AlbumUiState())
 val state = mutable.asStateFlow()
 init { onEvent(AlbumEvent.Refresh) }
 fun onEvent(event: AlbumEvent) {
  val s=mutable.value
  mutable.value=when(event) {
   AlbumEvent.Refresh -> { val photos=useCase.load(); s.copy(photos=photos,selectedId=s.selectedId?.takeIf { id -> photos.any { it.id==id } } ?: photos.firstOrNull()?.id,page=useCase.move(s.page,0,photos.size)) }
   is AlbumEvent.Select -> s.copy(selectedId=event.id)
   is AlbumEvent.Page -> s.copy(page=useCase.move(s.page,event.delta,s.photos.size))
   is AlbumEvent.Dialog -> s.copy(dialog=event.value)
   AlbumEvent.Delete -> { s.selectedId?.let(useCase::delete); val photos=useCase.load(); s.copy(photos=photos,selectedId=photos.firstOrNull()?.id,dialog=null,page=useCase.move(s.page,0,photos.size)) }
  }
 }
}
