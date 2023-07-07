package com.smartel.mysmartel_ver_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class TotaluseTimeAdapter(private var totaluseTimeList: List<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>) :
    RecyclerView.Adapter<TotaluseTimeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_totaluse_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val totaluseTimeDto = totaluseTimeList[position]

        // Bind the data to the views
        holder.tvBunGun.text = totaluseTimeDto.strBunGun
        holder.tvSvcName.text = totaluseTimeDto.strSvcName
        holder.tvCtnSecs.text = totaluseTimeDto.strCtnSecs
        holder.tvSecsToRate.text = totaluseTimeDto.strSecsToRate
        holder.tvSecsToAmt.text = totaluseTimeDto.strSecsToAmt
        holder.tvFreeMinCur.text = totaluseTimeDto.strFreeMinCur
        holder.tvFreeminRoll.text = totaluseTimeDto.strFreeminRoll
        holder.tvFreeMinTotal.text = totaluseTimeDto.strFreeMinTotal
        holder.tvFreeMinUse.text = totaluseTimeDto.strFreeMinUse
        holder.tvFreeMinReMain.text = totaluseTimeDto.strFreeMinReMain
    }

    override fun getItemCount(): Int {
        return totaluseTimeList.size
    }
    fun setData(data: List<KtDeductApiResponse.BodyData.TotaluseTimeDtoData>) {
        totaluseTimeList = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBunGun: TextView = itemView.findViewById(R.id.tvBunGun)
        val tvSvcName: TextView = itemView.findViewById(R.id.tvSvcName)
        val tvCtnSecs: TextView = itemView.findViewById(R.id.tvCtnSecs)
        val tvSecsToRate: TextView = itemView.findViewById(R.id.tvSecsToRate)
        val tvSecsToAmt: TextView = itemView.findViewById(R.id.tvSecsToAmt)
        val tvFreeMinCur: TextView = itemView.findViewById(R.id.tvFreeMinCur)
        val tvFreeminRoll: TextView = itemView.findViewById(R.id.tvFreeminRoll)
        val tvFreeMinTotal: TextView = itemView.findViewById(R.id.tvFreeMinTotal)
        val tvFreeMinUse: TextView = itemView.findViewById(R.id.tvFreeMinUse)
        val tvFreeMinReMain: TextView = itemView.findViewById(R.id.tvFreeMinReMain)

    }
}
