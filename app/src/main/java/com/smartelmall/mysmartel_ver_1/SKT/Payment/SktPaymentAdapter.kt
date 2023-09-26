package com.smartelmall.mysmartel_ver_1.SKT.Payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class SktPaymentAdapter(private val payments: List<SktPayment>) : RecyclerView.Adapter<SktPaymentAdapter.SktPaymentViewHolder>() {

    // ViewHolder 내부클래스입니다. 여기서 각 아이템의 뷰들을 초기화합니다.
    class SktPaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate = view.findViewById<TextView>(R.id.txt_date)
        val txtTitle = view.findViewById<TextView>(R.id.txt_title)
        val txtTitle2 = view.findViewById<TextView>(R.id.txt_title2)
        val txtValue = view.findViewById<TextView>(R.id.txt_value)
        val txtValue2 = view.findViewById<TextView>(R.id.txt_value2)

        // bind 함수에서 실제로 값을 설정해줍니다.
        fun bind(payment: SktPayment) {
            txtDate.text = "\n${payment.formattedDate}"
            txtTitle.text = "청구금액"
            txtValue.text = "${payment.INV_AMT}원"
            txtTitle2.text ="미납금액"
            txtValue2.text ="${payment.COL_BAMT}원\n\n\n"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SktPaymentViewHolder {
        // skt_payment_item.xml 레이아웃 파일을 inflate하여 ViewHolder 객체를 생성합니다.
        return SktPaymentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.skt_payment_item, parent, false))
    }

    override fun onBindViewHolder(holder: SktPaymentViewHolder, position: Int) {
        holder.bind(payments[position])  // 실제로 값을 설정하는 부분입니다.
    }

    override fun getItemCount()= payments.size  // 전체 아이템 개수 반환

}
