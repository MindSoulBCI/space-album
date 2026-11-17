package com.example.spatialalbum.p20261115.domain.model
data class Photo(val id: String,val title: String,val path: String,val bytes: Long,val stereoPath: String? = null,val status: String = "待生成",val isVideo:Boolean=false,val videoLayout:String="MONO",val aspect:Float=1.5f)
