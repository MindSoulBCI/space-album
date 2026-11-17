package com.example.spatialalbum.p20261115.ui.album
import com.example.spatialalbum.p20261115.domain.model.Photo
data class AlbumUiState(val photos: List<Photo> = emptyList(),val selectedId: String? = null,val page: Int = 0,val dialog: String? = null,val busy: Boolean = false,val error: String? = null) {
 val selected get() = photos.firstOrNull { it.id == selectedId }
}
sealed interface AlbumEvent {
 data object Refresh: AlbumEvent
 data class Select(val id: String): AlbumEvent
 data class Page(val delta: Int): AlbumEvent
 data class Dialog(val value: String?): AlbumEvent
 data object Delete: AlbumEvent
}
