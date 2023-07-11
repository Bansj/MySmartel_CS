package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class KtTotUseTimeCntTotAdapter(private var totUseTimeCntTotList: List<KtDeductApiResponse.BodyData.TotUseTimeCntTotDtoData>) :
    RecyclerView.Adapter<KtTotUseTimeCntTotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.kt_item_tot_use_time_cnt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val totUseTimeCntTotDto = totUseTimeCntTotList[position]

        // Bind the data to the views
        holder.tvTotal.text = totUseTimeCntTotDto.total
        holder.tvFreeSmsCur.text = totUseTimeCntTotDto.strFreeSmsCur
        holder.tvFreeSmsRoll.text = totUseTimeCntTotDto.strFreeSmsRoll
        holder.tvFreeSmsTotal.text = totUseTimeCntTotDto.strFreeSmsTotal
        holder.tvFreeSmsuse.text = totUseTimeCntTotDto.strFreeSmsuse
        holder.tvFreeSmsRemain.text = totUseTimeCntTotDto.strFreeSmsRemain
    }

    override fun getItemCount(): Int {
        return totUseTimeCntTotList.size
    }

    fun setData(data: List<KtDeductApiResponse.BodyData.TotUseTimeCntTotDtoData>) {
        totUseTimeCntTotList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvFreeSmsCur: TextView = itemView.findViewById(R.id.tvFreeSmsCur)
        val tvFreeSmsRoll: TextView = itemView.findViewById(R.id.tvFreeSmsRoll)
        val tvFreeSmsTotal: TextView = itemView.findViewById(R.id.tvFreeSmsTotal)
        val tvFreeSmsuse: TextView = itemView.findViewById(R.id.tvFreeSmsuse)
        val tvFreeSmsRemain: TextView = itemView.findViewById(R.id.tvFreeSmsRemain)
    }
}
