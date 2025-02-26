package com.ts.imaforkotolin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemSwitch: SwitchCompat = view.findViewById(R.id.itemSwitch)
        val itemText: TextView = view.findViewById(R.id.itemTitle)
        val itemQuantityTextView: TextView = view.findViewById(R.id.itemQuantity)
        val buttonIncrease: Button = view.findViewById(R.id.buttonIncrease)
        val buttonDecrease: Button = view.findViewById(R.id.buttonDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemText.text = item.name
        holder.itemQuantityTextView.text = "数量: ${item.quantity}"
        // **スイッチの初期状態を設定**
        holder.itemSwitch.setOnCheckedChangeListener(null) // 一旦リスナーを解除
        holder.itemSwitch.isChecked = item.isChecked

        holder.itemSwitch.setOnCheckedChangeListener { _, isChecked ->
            val dbHelper = DatabaseHelper(holder.itemView.context)

            // ✅ `isChecked` の変更をデータに反映
            item.isChecked = isChecked
            dbHelper.updateIsChecked(item.id, isChecked)

            // ✅ 合計数を更新
            (holder.itemView.context as? MainActivity)?.updateTotalQuantity()

            notifyItemChanged(holder.adapterPosition)
        }

        holder.buttonIncrease.setOnClickListener {
            item.quantity += 1
            val dbHelper = DatabaseHelper(holder.itemView.context)
            dbHelper.updateQuantity(item.name, item.quantity)
            holder.itemQuantityTextView.text = "数量: ${item.quantity}"
            (holder.itemView.context as? MainActivity)?.updateTotalQuantity() // ✅ 合計を更新
        }

        holder.buttonDecrease.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity -= 1
                val dbHelper = DatabaseHelper(holder.itemView.context)
                dbHelper.updateQuantity(item.name, item.quantity)
                holder.itemQuantityTextView.text = "数量: ${item.quantity}"
                (holder.itemView.context as? MainActivity)?.updateTotalQuantity() // ✅ 合計を更新
            }
        }
    }

    override fun getItemCount() = itemList.size

    fun addItem(item: Item) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex)
    }

    fun updateItems(newItems: List<Item>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}
