package com.awesomesol.peering.character

data class UserInfo(
    val uid:String, // pk
    val nickName:String,
    val profileUrl:String,
    val email:String,
    val friendList: ArrayList<String>, // 현재 내 친구 목록
    // 피어링 계정이 있는 친구를 거르거나 해야할듯
)
