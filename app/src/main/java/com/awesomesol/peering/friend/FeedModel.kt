package com.awesomesol.peering.friend

data class FeedModel (
    var cid:String="",
    var uid:String="",
    var nickname : String = "",
    var mainImg : String = "",
    var profileImg : String = "",
    var content : String = "",
    val type : Int,
)
