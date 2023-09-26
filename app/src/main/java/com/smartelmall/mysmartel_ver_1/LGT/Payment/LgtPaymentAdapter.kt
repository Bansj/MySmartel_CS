package com.smartelmall.mysmartel_ver_1.LGT.Payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// ViewHolder 정의
class LgtPaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtDate: TextView = view.findViewById(R.id.txt_date)
    val txtTitle1: TextView = view.findViewById(R.id.txt_title)
    val txtTitle2: TextView = view.findViewById(R.id.txt_title2)
    val txtValue1: TextView = view.findViewById(R.id.txt_value)
    val txtValue2: TextView = view.findViewById(R.id.txt_value2)
}

// Adapter 정의
class LgtPaymentAdapter(private var list : ArrayList<LgtPayment>) : RecyclerView.Adapter<LgtPaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):LgtPaymentViewHolder {
        // lgt_payment_item.xml 파일로 뷰 생성
        return LgtPaymentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lgt_payment_item,parent,false))
    }

    override fun onBindViewHolder(holder:LgtPaymentViewHolder, position:Int) {
        // position 위치의 데이터와 holder 연결
        // 날짜 형식 변환 (yyyyMMdd -> yyyy년 MM월 dd일)
        var dateFormatInput = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        var dateFormatOutput = SimpleDateFormat("\nyyyy년 MM월 dd일", Locale.getDefault())

        var dateInputParsed = dateFormatInput.parse(list[position].date)

        holder.txtDate.text=dateFormatOutput.format(dateInputParsed ?: Date())

        holder.txtTitle1.text="\n청구요금"
        holder.txtValue1.text="\n${list[position].amount}원"

        holder.txtTitle2.text="납부방법"
        holder.txtValue2.text="${list[position].method}\n\n\n"

    }

    override fun getItemCount():Int{
        return list.size // 아이템 개수 반환
    }
}