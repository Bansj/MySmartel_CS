package com.smartelmall.mysmartel_ver_1.SKT.Bill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class SktBillingAdapter(private val items: List<SktBillingItem>) : RecyclerView.Adapter<SktBillingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLclNm: TextView = itemView.findViewById(R.id.txt_title)
        val txtBillItnNm: TextView = itemView.findViewById(R.id.txt_title2)
        val txtInvAmt: TextView = itemView.findViewById(R.id.txt_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skt_item_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtLclNm.text = item.lclNm
        holder.txtBillItnNm.text = item.billItnNm
        holder.txtInvAmt.text = "${item.invAmt}원\n\n"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
