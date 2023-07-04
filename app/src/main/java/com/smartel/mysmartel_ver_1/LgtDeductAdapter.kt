package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import com.example.mysmartel_ver_1.R
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DeductionDetailsAdapter(private val deductionDetails: List<LgtRemainInfo>) :
    RecyclerView.Adapter<DeductionDetailsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.svcNm)
        val serviceType: TextView = itemView.findViewById(R.id.svcTypNm)
        val serviceUnit: TextView = itemView.findViewById(R.id.svcUnitCd)
        val allocatedValue: TextView = itemView.findViewById(R.id.alloValue)
        val usedValue: TextView = itemView.findViewById(R.id.useValue)
        val productType: TextView = itemView.findViewById(R.id.prodTypeCd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lgt_deduct_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail = deductionDetails[position]
        holder.serviceName.text = detail.svcNm
        holder.serviceType.text = detail.svcTypNm
        holder.serviceUnit.text = detail.svcUnitCd
        holder.allocatedValue.text = detail.alloValue
        holder.usedValue.text = detail.useValue
        holder.productType.text = detail.prodTypeCd
    }

    override fun getItemCount(): Int {
        return deductionDetails.size
    }
}




