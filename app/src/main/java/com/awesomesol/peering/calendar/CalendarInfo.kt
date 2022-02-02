package com.awesomesol.peering.calendar

import java.util.ArrayList
import java.util.HashMap

// cid 밑에!
data class CalendarInfo(
    var uidList:ArrayList<String>,
    var cid:String,
    var cname:String,
    var dataList4: HashMap<String, ArrayList<GalleryData>>
)
