package com.ts.imaforkotolin

data class Item(
    val id: Int,
    val name: String,
    var quantity: Int = 1,
    var isChecked: Boolean = false
)
