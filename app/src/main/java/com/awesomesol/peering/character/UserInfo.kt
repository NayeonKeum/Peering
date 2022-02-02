package com.awesomesol.peering.character

data class UserInfo(
    val uid:String, // pk
    var nickName:String,
    var profileUrl:String,
    var email:String,

    // 현재 내 친구 목록 {이름:친구인지아닌지}
    var friendList: HashMap<String, Int>,
    // 0: 친구 아님, 1: 나를 팔로우함, 2: 내가 팔로잉 함
    // 피어링 계정이 있는 친구를 거르거나 해야할듯

    // 이거 가져오고 불러올 수는 있는데 자기 폰이면 정보가 너무너무 많이 저장될 것 같아서 일단 주석
    // val galleryUriList: ArrayList<String>

    var calendarID: HashMap<String, String>,
)
