package com.TATE.PolyHome.models

data class Room(
    val id: Int,
    val name: String,
    val devices: MutableList<Device> = mutableListOf()
)
