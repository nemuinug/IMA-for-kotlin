package com.ts.imaforkotolin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val itemList: MutableList<Item>,
    private val onCheckedChange: () -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    companion object {
        const val MIN_QUANTITY = 0
    }

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

    fun updateItems(newItems: List<Item>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemText.text = item.name
        holder.itemQuantityTextView.text = "数量: ${item.quantity}"

        holder.itemSwitch.setOnCheckedChangeListener(null)
        holder.itemSwitch.isChecked = item.isChecked

        holder.itemSwitch.setOnCheckedChangeListener { _, isChecked ->
            val dbHelper = DatabaseHelper(holder.itemView.context)
            item.isChecked = isChecked
            dbHelper.updateIsChecked(item.id, isChecked)
            notifyItemChanged(holder.adapterPosition)

            onCheckedChange()
        }

        holder.buttonIncrease.setOnClickListener {
            item.quantity += 1
            val dbHelper = DatabaseHelper(holder.itemView.context)
            dbHelper.updateQuantity(item.name, item.quantity)
            holder.itemQuantityTextView.text = "数量: ${item.quantity}"
            onCheckedChange()
        }

        holder.buttonDecrease.setOnClickListener {
            if (item.quantity > MIN_QUANTITY) {
                item.quantity -= 1
                val dbHelper = DatabaseHelper(holder.itemView.context)
                dbHelper.updateQuantity(item.name, item.quantity)
                holder.itemQuantityTextView.text = "数量: ${item.quantity}"
                onCheckedChange()
            }
        }

        // **アイテムクリックで SubActivity を開く**
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SubActivity::class.java).apply {
                putExtra("ITEM_TITLE", item.name)
                putExtra("ITEM_QUANTITY", item.quantity)
            }
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount() = itemList.size
}
