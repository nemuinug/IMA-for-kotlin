package com.ts.imaforkotolin

data class Item(
    val id: Int,
    val name: String,
    var quantity: Int = 1,
    var isChecked: Boolean = false,
    var createdTime: String = "",
    var comment: String? = null,
    var image: ByteArray? = null  // 🔹 BLOB に統一
)
