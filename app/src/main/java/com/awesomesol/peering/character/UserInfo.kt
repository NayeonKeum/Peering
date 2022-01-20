package com.awesomesol.peering.character

data class UserInfo(
    val uid:String, // pk
    val nickName:String,
    val profileUrl:String,
    val email:String,
    val friendList: HashMap<String, Int>, // 현재 내 친구 목록 {이름:친구인지아닌지}
    // 0: 친구 아님, 1: 나를 팔로우함, 2: 내가 팔로잉 함
    // 피어링 계정이 있는 친구를 거르거나 해야할듯
)
