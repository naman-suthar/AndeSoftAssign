package com.vrcareer.myapplication.model

data class Voter(
    val id: String? = null,
    var name: String? = null,
    var phoneNo: String? = null,
    var age: String? = null,
    var hasVoted: Boolean? = false,
    var votedTo: String? = null,
    var role: String? = "voter"
)
