package com.awesomesol.peering.friend

data class FeedModel (
    var cid:String="",
    var uid:String="",
    var nickname : String = "",
    var mainImg : String = "",
    var profileImg : String = "",
    var content : String = "",
    var publicScope:Long=0,
    var category:String="",
    var date:String=""
)