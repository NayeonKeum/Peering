package com.awesomesol.peering.catDiary

data class GroupInfo(
    val gid:String="",
    val groupName: String = "",
    val groupImg: String = "",
    val cid: String = "",
    val uidList: ArrayList<String>,
)