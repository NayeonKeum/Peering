package com.awesomesol.peering.calendar

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// cid 밑에!
data class CalendarInfo(
        var uidList:ArrayList<String>,
        var cid:String,
        var cname:String,
        var dataList4: HashMap<String, ArrayList<HashMap<String, Any>>>
)
