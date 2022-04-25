package com.example.saveavi.ui.video

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.saveavi.data.models.VideoEntity
import com.example.saveavi.databinding.ItemVideoBinding

class VideoAdapter(private val videos:List<VideoEntity>):RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemBinding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoViewHolder(itemBinding,parent.context)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    inner class VideoViewHolder(private val binding:ItemVideoBinding,private val context:Context):RecyclerView.ViewHolder(binding.root){
        fun bind(video:VideoEntity){
            val mediaController = MediaController(context)
            mediaController.setAnchorView(binding.videoBackGround)

            binding.videoBackGround.setMediaController(mediaController)
            binding.videoBackGround.setVideoURI(video.video.toUri())
            binding.videoBackGround.start()
        }
    }
}