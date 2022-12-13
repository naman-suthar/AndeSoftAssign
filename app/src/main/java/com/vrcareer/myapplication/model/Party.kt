package com.vrcareer.myapplication.model

data class Party(
    val name: String? = null,
    val candidateName: String? = null,
    var votersList: MutableList<Voter>? = null
)
