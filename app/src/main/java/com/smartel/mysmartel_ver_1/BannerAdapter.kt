package com.smartel.mysmartel_ver_1


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysmartel_ver_1.R
import com.example.mysmartel_ver_1.databinding.ItemBannerBinding

// 배너 어댑터 클래스
/*class BannerAdapter(private val onBannerClick: (String) -> Unit) :
    ListAdapter<Banner, BannerAdapter.ViewHolder>(BannerDiffCallback()) {

    // 뷰홀더 클래스
    inner class ViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 배너를 뷰에 바인딩
        fun bind(banner: Banner) {
            binding.apply {
                // 이미지 로드 및 이벤트 설정
                Glide.with(itemView).load(banner.imagePath).into(ivBanner)
                root.setOnClickListener { onBannerClick(banner.imageLink) }
            }
        }
    }

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position % itemCount))
    }
}

// 배너 변경 감지를 위한 Diff Callback 클래스
class BannerDiffCallback : DiffUtil.ItemCallback<Banner>() {
    override fun areItemsTheSame(oldItem: Banner, newItem: Banner) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Banner, newItem: Banner) = oldItem == newItem
}
*/

