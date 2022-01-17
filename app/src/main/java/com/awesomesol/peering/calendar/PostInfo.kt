package com.awesomesol.peering.calendar

import java.time.LocalDateTime

data class PostInfo(
    val pid:String,
    val uid:String,
    val time: LocalDateTime,
    val title:String,
    val content:ContentInfo
    )
