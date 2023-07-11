package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class KtVoiceCallDetailTotAdapter(private var voiceCallDetailTotList: List<KtDeductApiResponse.BodyData.VoiceCallDetailTotDtoData>) :
    RecyclerView.Adapter<KtVoiceCallDetailTotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kt_item_voice_call_detail_tot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voiceCallDetailTotDto = voiceCallDetailTotList[position]

        // Bind the data to the views
        holder.tvTotal.text = voiceCallDetailTotDto.total
        holder.tvFreeminCurSum.text = voiceCallDetailTotDto.iFreeminCurSum
        holder.tvFreeMinRollSum.text = voiceCallDetailTotDto.iFreeMinRollSum
        holder.tvFreeminTotalSum.text = voiceCallDetailTotDto.iFreeminTotalSum
        holder.tvFreeMinUseSum.text = voiceCallDetailTotDto.iFreeMinUseSum
        holder.tvFreeMinRemainSum.text = voiceCallDetailTotDto.iFreeMinRemainSum
    }

    override fun getItemCount(): Int {
        return voiceCallDetailTotList.size
    }

    fun setData(data: List<KtDeductApiResponse.BodyData.VoiceCallDetailTotDtoData>) {
        voiceCallDetailTotList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvFreeminCurSum: TextView = itemView.findViewById(R.id.tvFreeminCurSum)
        val tvFreeMinRollSum: TextView = itemView.findViewById(R.id.tvFreeMinRollSum)
        val tvFreeminTotalSum: TextView = itemView.findViewById(R.id.tvFreeminTotalSum)
        val tvFreeMinUseSum: TextView = itemView.findViewById(R.id.tvFreeMinUseSum)
        val tvFreeMinRemainSum: TextView = itemView.findViewById(R.id.tvFreeMinRemainSum)
    }
}
