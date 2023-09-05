package com.smartelmall.mysmartel_ver_1.LGT.Deduct

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class LgtDeductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val txtTitle = view.findViewById<TextView>(R.id.txt_title)
    private val txtRemainTitle = view.findViewById<TextView>(R.id.txt_remainTitle)
    private val txtRemainUse = view.findViewById<TextView>(R.id.txt_remainUse)
    private val txtRemainLeft = view.findViewById<TextView>(R.id.txt_remainLeft)

    private val txtTotalValue = view.findViewById<TextView>(R.id.txt_totalValue)
    private val txtUseValue = view.findViewById<TextView>(R.id.txt_useValue)
    private val txtLeftValue = view.findViewById<TextView>(R.id.txt_leftValue)


    fun bind(item: LgtDeductItem) {
        // Set the text for each TextView from the item data
        txtTitle.text = item.title
        txtRemainTitle.text = item.remainTitle
        txtRemainUse.text = item.remainUse
        txtRemainLeft.text= item.remainLeft

    }
}

class LgtDeductAdapter(private var items : List<LgtDeductItem>) : RecyclerView.Adapter<LgtDeductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):LgtDeductViewHolder{
        return LgtDeductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lgt_deduct_item,parent,false))
    }

    override fun onBindViewHolder(holder:LgtDeductViewHolder, position:Int){
        holder.bind(items[position])
    }

    override fun getItemCount()=items.size

}
