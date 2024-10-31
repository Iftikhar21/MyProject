package com.example.ourbook

data class Guest(
    val id: Int, val name: String, val nickname: String, val email: String, val address: String
    , val birth: String, val number: String, val photo: ByteArray
)