package com.awesomesol.peering.calendar

import android.net.Uri

data class CommentInfo(
    val cid:String, // pk
    val pid:String,
    val commentText:String,
    val date:String
    )
