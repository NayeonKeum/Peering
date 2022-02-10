package com.awesomesol.peering.friend

data class FeedModel (
    var cid:String="",
    var uid:String="",
    var nickname : String = "",
    var mainImg : ArrayList<HashMap<String, Any>> = arrayListOf(),
    var profileImg : String = "",
    var content : String = "",
    var publicScope:Long=0,
    var category:String="",
    var date:String="",
    val type : Long=0,
    val isGroup:Long=0, // 0이면 그룹 아님, 1이면 그룹임
)