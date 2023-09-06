package com.smartelmall.mysmartel_ver_1.KT.Deduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class DeductAdapter(private val items: List<DeductItem>) :
    RecyclerView.Adapter<DeductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kt_deduct, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val svcNameTextView: TextView = itemView.findViewById(R.id.svcNameTextView)
        private val totalTextView: TextView = itemView.findViewById(R.id.totalTextView)
        private val remainTextView: TextView = itemView.findViewById(R.id.remainTextView)
        private val usageTextView: TextView = itemView.findViewById(R.id.usageTextView)

        fun bind(item: DeductItem) {
            svcNameTextView.text = item.svcName
            totalTextView.text = "${item.total}"
            usageTextView.text = "${item.usage}"
            remainTextView.text = "${item.remain}\n\n\n"
        }
    }
}
