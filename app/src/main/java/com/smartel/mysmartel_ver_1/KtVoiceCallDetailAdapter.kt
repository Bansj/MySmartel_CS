package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class KtVoiceCallDetailAdapter(private var voiceCallDetailList: List<KtDeductApiResponse.BodyData.VoiceCallDetailDtoData>) :
    RecyclerView.Adapter<KtVoiceCallDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kt_item_voice_call_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voiceCallDetailDto = voiceCallDetailList[position]

        // Bind the data to the views
        holder.tvBunGun.text = voiceCallDetailDto.strBunGun
        holder.tvSvcName.text = voiceCallDetailDto.strSvcName
        holder.tvFreeMinCur.text = voiceCallDetailDto.strFreeMinCur
        holder.tvFreeminRoll.text = voiceCallDetailDto.strFreeminRoll
        holder.tvFreeMinTotal.text = voiceCallDetailDto.strFreeMinTotal
        holder.tvFreeMinUse.text = voiceCallDetailDto.strFreeMinUse
        holder.tvFreeMinReMain.text = voiceCallDetailDto.strFreeMinReMain
    }

    override fun getItemCount(): Int {
        return voiceCallDetailList.size
    }

    fun setData(data: List<KtDeductApiResponse.BodyData.VoiceCallDetailDtoData>) {
        voiceCallDetailList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBunGun: TextView = itemView.findViewById(R.id.tvBunGun)
        val tvSvcName: TextView = itemView.findViewById(R.id.tvSvcName)
        val tvFreeMinCur: TextView = itemView.findViewById(R.id.tvFreeMinCur)
        val tvFreeminRoll: TextView = itemView.findViewById(R.id.tvFreeminRoll)
        val tvFreeMinTotal: TextView = itemView.findViewById(R.id.tvFreeMinTotal)
        val tvFreeMinUse: TextView = itemView.findViewById(R.id.tvFreeMinUse)
        val tvFreeMinReMain: TextView = itemView.findViewById(R.id.tvFreeMinReMain)
    }
}
