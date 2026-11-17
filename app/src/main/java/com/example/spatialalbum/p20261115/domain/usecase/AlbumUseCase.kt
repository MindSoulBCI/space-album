package com.example.spatialalbum.p20261115.domain.usecase
import com.example.spatialalbum.p20261115.data.repository.PhotoRepository
import com.example.spatialalbum.p20261115.domain.model.Photo
class AlbumUseCase(private val repository: PhotoRepository) {
 companion object { const val PAGE_SIZE=27 }
 fun load() = repository.load()
 fun delete(id: String) = repository.delete(id)
 fun page(items: List<Photo>, page: Int) = items.drop(page.coerceAtLeast(0)*PAGE_SIZE).take(PAGE_SIZE)
 fun move(page: Int, delta: Int, size: Int) = (page+delta).coerceIn(0,((size-1)/PAGE_SIZE).coerceAtLeast(0))
}
