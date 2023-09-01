package com.smartelmall.mysmartel_ver_1.SKT.Deduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartelmall.mysmartel_ver_1.R

class SktDeductRecyclerAdapter(private var itemsList : List<SktDeductRecyclerResponse>) : RecyclerView.Adapter<SktDeductRecyclerAdapter.SktDeductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SktDeductViewHolder {
        return SktDeductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.skt_deduct_item, parent, false))
    }

    override fun onBindViewHolder(holder: SktDeductViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int = itemsList.size


class SktDeductViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    private var txtTitle = view.findViewById<TextView>(R.id.txt_title)
    private var txtTotalTitle = view.findViewById<TextView>(R.id.txt_remainTitle)
    private var txtUseTitle = view.findViewById<TextView>(R.id.txt_remainUse)
    private var txtLeftTitle = view.findViewById<TextView>(R.id.txt_remainLeft)

    private var txtTotalValue = view.findViewById<TextView>(R.id.txt_totalValue)
    private var txtUseValue = view.findViewById<TextView>(R.id.txt_useValue)
    private var txtLeftValue = view.findViewById<TextView>(R.id.txt_leftValue)

    fun bind(item: SktDeductRecyclerResponse){
        with(item){
            txtTitle.text= title
            txtTotalTitle.text = totalTitle
            txtUseTitle.text = useTitle
            txtLeftTitle.text = leftTitle

            txtTotalValue.text = totalValue
            txtUseValue.text = useValue
            txtLeftValue.text = leftValue
        }
    }
}
}

