package com.awesomesol.peering.calendar

import android.net.Uri
import java.time.LocalDate
import java.time.LocalDateTime

data class PostInfo(
    val pid:String, // pk
    val uid:String,
    val title:String,
    val calDate: String,
    val contentText:String,
    //val contentPhoto: Uri,
    val category: HashMap<String, ArrayList<String>>,
    )
