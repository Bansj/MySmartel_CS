package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class KtTotUseTimeCntAdapter(private var totUseTimeCntList: List<KtDeductApiResponse.BodyData.TotUseTimeCntDtoData>) :
    RecyclerView.Adapter<KtTotUseTimeCntAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kt_item_tot_use_time_cnt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val totUseTimeCntDto = totUseTimeCntList[position]

        // Bind the data to the views
        holder.tvSvcNameSms.text = totUseTimeCntDto.strSvcNameSms
        holder.tvCtnSecs.text = totUseTimeCntDto.strCtnSecs
        holder.tvFreeSmsCur.text = totUseTimeCntDto.strFreeSmsCur
        holder.tvFreeSmsRoll.text = totUseTimeCntDto.strFreeSmsRoll
        holder.tvFreeSmsTotal.text = totUseTimeCntDto.strFreeSmsTotal
        holder.tvFreesmsUse.text = totUseTimeCntDto.strFreesmsUse
        holder.tvFreeSmsRemain.text = totUseTimeCntDto.strFreeSmsRemain
    }

    override fun getItemCount(): Int {
        return totUseTimeCntList.size
    }

    fun setData(data: List<KtDeductApiResponse.BodyData.TotUseTimeCntDtoData>) {
        totUseTimeCntList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSvcNameSms: TextView = itemView.findViewById(R.id.tvSvcNameSms)
        val tvCtnSecs: TextView = itemView.findViewById(R.id.tvCtnSecs)
        val tvFreeSmsCur: TextView = itemView.findViewById(R.id.tvFreeSmsCur)
        val tvFreeSmsRoll: TextView = itemView.findViewById(R.id.tvFreeSmsRoll)
        val tvFreeSmsTotal: TextView = itemView.findViewById(R.id.tvFreeSmsTotal)
        val tvFreesmsUse: TextView = itemView.findViewById(R.id.tvFreesmsUse)
        val tvFreeSmsRemain: TextView = itemView.findViewById(R.id.tvFreeSmsRemain)
    }
}