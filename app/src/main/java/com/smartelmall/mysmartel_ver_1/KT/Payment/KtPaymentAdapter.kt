package com.smartelmall.mysmartel_ver_1.KT.Payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class KtPaymentAdapter(private var paymentsList : List<KtPayment>) : RecyclerView.Adapter<KtPaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):KtPaymentViewHolder{
        return KtPaymentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.kt_payment_item,
                parent,
                false))
    }

    override fun onBindViewHolder(holder :KtPaymentViewHolder , position:Int){
        holder.bind(paymentsList[position])
    }

    override fun getItemCount():Int{
        return paymentsList.size;
    }

    // 데이터가 변경되었을 때 새로운 리스트로 업데이트하는 함수.
    fun updatePayments(newPaymentsList : List<KtPayment>){
        paymentsList = newPaymentsList
        notifyDataSetChanged()
    }
}

class KtPaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // XML 레이아웃에 있는 TextView 참조 가져오기
    private val txtDate: TextView = view.findViewById(R.id.txt_date)
    private val txtTitle1: TextView = view.findViewById(R.id.txt_title)
    private val txtValue1: TextView = view.findViewById(R.id.txt_value)
    private val txtTitle2: TextView = view.findViewById(R.id.txt_title2)
    private val txtValue2: TextView = view.findViewById(R.id.txt_value2)

    // ViewHolder가 화면에 표시될 때 호출되는 함수로, 실제 데이터를 뷰에 바인딩하는 역할을 함.
    fun bind(payment: KtPayment) {
        // 날짜 설정
        txtDate.text = "\n${payment.date}"

        // 청구요금 타이틀 설정 (고정값)
        txtTitle1.text = "청구요금"

        // 청구요금 값 설정
        txtValue1.text = payment.thisMonthAmount

        // 미납요금 타이틀 설정 (고정값)
        txtTitle2.text = "미납요금"

        // 미납요금 값 설정
        txtValue2.text = "${payment.pastDueAmtAmount}\n\n\n"
    }
}
