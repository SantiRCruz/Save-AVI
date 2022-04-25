package com.example.saveavi.ui.image

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.saveavi.data.models.ImageEntity
import com.example.saveavi.databinding.ItemImageBinding

class ImageAdapter(private val images:List<ImageEntity>):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int  = images.size

    inner class ImageViewHolder(private val binding : ItemImageBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(image : ImageEntity){
            binding.txtTitle.text = image.title
            Log.e("bind: ",image.image )
            binding.imgBackGround.setImageURI(image.image.toUri())
        }
    }
}
