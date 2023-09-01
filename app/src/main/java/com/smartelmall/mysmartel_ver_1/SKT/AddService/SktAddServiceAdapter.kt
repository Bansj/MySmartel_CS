package com.smartelmall.mysmartel_ver_1.SKT.AddService

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class SktAddServiceAdapter(private var items : List<SktAddServiceItem.Product>) : RecyclerView.Adapter<SktAddServiceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(item : SktAddServiceItem.Product){
            itemView.findViewById<TextView>(R.id.prodName).text = item.prodNm
            itemView.findViewById<TextView>(R.id.prodFee).text = "\n" + item.displayProdFee + "\n\n"

            // If you want to set a specific value to displayProdFee
            // item.displayProdFee = "Your Value"
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType:Int): ViewHolder{
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.skt_add_service_item,parent,false))
    }

    override fun getItemCount(): Int{
        return items.size
    }

    override fun onBindViewHolder(holder : ViewHolder, position:Int){
        holder.bind(items[position])
    }
}
