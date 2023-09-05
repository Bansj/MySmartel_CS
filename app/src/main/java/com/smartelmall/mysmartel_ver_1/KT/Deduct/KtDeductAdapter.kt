package com.smartelmall.mysmartel_ver_1.KT.Deduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class KtDeductAdapter(private val items: List<KtDeductItem>) :
    RecyclerView.Adapter<KtDeductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView = view.findViewById<TextView>(R.id.txt_title)
        val totalTitleTextView = view.findViewById<TextView>(R.id.txt_remainTitle)
        val useTitleTextView = view.findViewById<TextView>(R.id.txt_remainUse)
        val leftTitleTextView = view.findViewById<TextView>(R.id.txt_remainLeft)

        val totalValueTextView = view.findViewById<TextView>(R.id.txt_totalValue)
        val useValueTextView = view.findViewById<TextView>(R.id.txt_useValue)
        val leftValueTextView = view.findViewById<TextView>(R.id.txt_leftValue)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.kt_deduct_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Set the title for each TextView
        holder.titleTextView.text=items[position].title

        // Set the values for each TextView
        holder.totalValueTextView.text=items[position].total
        holder.useValueTextView.text=items[position].use
        holder.leftValueTextView.text=items[position].left

        // If you want to change the titles dynamically according to your needs you can do it here.
        // For example:
        // holder.totalTitleTextview.text="your desired text"
        // Same for 'use' and 'left'

    }

    override fun getItemCount() = items.size

}
