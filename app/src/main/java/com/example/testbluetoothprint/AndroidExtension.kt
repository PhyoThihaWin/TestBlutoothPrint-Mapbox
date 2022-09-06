package com.example.testbluetoothprint

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.getInflatedLayout(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}
fun Activity.getVerticalLayoutManager() = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

fun Fragment.getVerticalLayoutManager() = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

fun Context.toast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}