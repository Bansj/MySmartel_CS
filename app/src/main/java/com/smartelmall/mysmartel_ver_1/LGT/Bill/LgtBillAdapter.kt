package com.smartelmall.mysmartel_ver_1.LGT.Bill

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class LgtBillAdapter(private val billList: List<LgtBill>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 뷰홀더 클래스 정의
    class LgtBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val txtValue: TextView = itemView.findViewById(R.id.txt_value)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingText: TextView = itemView.findViewById(R.id.loading_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.loading_item_layout, parent, false)
            )
        } else {
            LgtBillViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.lgt_item_bill, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LgtBillViewHolder && billList != null && billList.isNotEmpty()) {
            holder.txtTitle.text = billList[position].title
            holder.txtValue.text = billList[position].value

            if (billList[position].title.contains("총 납부하실 금액", ignoreCase = true)) {
                holder.txtValue.setTypeface(null, Typeface.BOLD)
            }
        } else if (holder is LoadingViewHolder){
            holder.loadingText.text = "Loading..." // or any placeholder text
        }
    }

    override fun getItemViewType(position:Int):Int{
        return if(billList == null || billList.isEmpty()) 0 else 1
    }


    override fun getItemCount(): Int{
        //return if(billList == null || billList.isEmpty()) 1 else billlist.size
        return if(billList == null || billList.isEmpty()) 1 else billList.size // 수정된 부분
    }
}
