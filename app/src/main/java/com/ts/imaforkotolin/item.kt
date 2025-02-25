package com.ts.imaforkotolin

data class Item(
    val name: String,
    var quantity: Int = 1,  // 🔹 0 ではなく 1 にする
    var isChecked: Boolean = false
)
