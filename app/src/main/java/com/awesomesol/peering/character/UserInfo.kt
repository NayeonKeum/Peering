package com.awesomesol.peering.character

data class UserInfo(
    val uid:String, // pk
    val nickName:String,
    val profileUrl:String,
    val email:String,
    // friendList
)
