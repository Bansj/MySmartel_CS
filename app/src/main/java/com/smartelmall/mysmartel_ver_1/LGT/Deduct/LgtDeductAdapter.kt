package com.smartelmall.mysmartel_ver_1.LGT.Deduct


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class LgtDeductAdapter(private var items: List<LgtDeductItem>) : RecyclerView.Adapter<LgtDeductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LgtDeductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lgt_deduct_item, parent, false)
        return LgtDeductViewHolder(view)
    }

    override fun onBindViewHolder(holder: LgtDeductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}

class LgtDeductViewHolder(view : View) : RecyclerView.ViewHolder(view){

    private var txtTitle : TextView = view.findViewById(R.id.txt_title)
    private var txtTotalTitle : TextView = view.findViewById(R.id.txt_totalTitle)
    private var txtUseTitle : TextView = view.findViewById(R.id.txt_useTitle)
    private var txtRemainTitle : TextView = view.findViewById(R.id.txt_remainTitle)
    private var txtTotalValue : TextView = view.findViewById(R.id.txt_totalValue)
    private var txtUseValue : TextView=view.findViewById(R.id.txt_useValue)
    private var txtRemainValue : TextView=view.findViewById(R.id.txt_remainValue)


    fun bind(item:LgtDeductItem){
        txtTitle.text=item.title
        txtTotalTitle.text=item.totalTitle
        txtUseTitle.text=item.useTitle
        txtTotalValue.text=item.totalValue
        txtUseValue.text=item.useValue
        txtRemainTitle.text= "${item.remainTitle}\n\n\n"
        txtRemainValue.text = item.remainValue
    }
}

