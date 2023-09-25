package com.smartelmall.mysmartel_ver_1.KT.Bill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R
import java.text.NumberFormat
import java.util.*

class KtBillAdapter(private var itemList: List<KtBillItem>) : RecyclerView.Adapter<KtBillAdapter.KtBillViewHolder>() {

    // ViewHolder 정의
    inner class KtBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val txtValue: TextView = itemView.findViewById(R.id.txt_value)
    }

    // onCreateViewHolder에서는 ViewHolder가 생성되는 로직을 구현합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KtBillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kt_item_bill, parent, false)
        return KtBillViewHolder(view)
    }

    // onBindViewHolder에서는 ViewHolder가 데이터와 결합(bind)되는 로직을 구현합니다.
    override fun onBindViewHolder(holder: KtBillViewHolder, position: Int) {
        holder.txtTitle.text = itemList[position].title
        holder.txtValue.text = itemList[position].value
    }

    // getItemCount에서는 RecyclerView에 들어갈 아이템의 개수를 반환합니다.
    override fun getItemCount(): Int {
        return itemList.size
    }
}

