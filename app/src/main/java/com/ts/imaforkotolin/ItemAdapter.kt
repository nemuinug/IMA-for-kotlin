package com.ts.imaforkotolin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemSwitch: SwitchCompat = view.findViewById(R.id.itemSwitch)
        val itemText: TextView = view.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemText.text = item.name
        holder.itemSwitch.isChecked = item.isChecked

        holder.itemSwitch.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }
    }


    override fun getItemCount() = itemList.size

    fun addItem(item: Item) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex) // ✅ RecyclerView に変更を通知
    }
    fun updateItems(newItems: List<Item>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()  // RecyclerView に更新を通知
    }

}
