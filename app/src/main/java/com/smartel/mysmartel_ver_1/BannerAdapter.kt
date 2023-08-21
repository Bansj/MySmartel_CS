package com.smartel.mysmartel_ver_1


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.mysmartel_ver_1.R

class BannerAdapter(private val context: Context, private val banners: List<BannerItem>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        Glide.with(context).load(banners[position].imagePath).into(holder.imageView)
        holder.itemView.setOnClickListener {
            // 웹 페이지로 이동하는 코드
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(banners[position].imageLink)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return banners.size
    }
}




