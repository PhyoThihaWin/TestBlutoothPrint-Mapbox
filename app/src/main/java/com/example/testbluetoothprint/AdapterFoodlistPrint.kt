package com.example.testbluetoothprint

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdapterFoodlistPrint(val data: List<Food>) :
    RecyclerView.Adapter<AdapterFoodlistPrint.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.getInflatedLayout(R.layout.item_order_food)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.findViewById<TextView>(R.id.tvFoodName).text = "${data[position].qty} x ${data[position].name}"
        holder.itemView.findViewById<TextView>(R.id.tvPrice).text = "Ks, ${data[position].price.toString()}"

    }

    override fun getItemCount(): Int {
        return data.size
    }

}