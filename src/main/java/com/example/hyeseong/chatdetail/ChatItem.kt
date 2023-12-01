package com.example.hyeseong.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
){
    constructor(): this("","")
}