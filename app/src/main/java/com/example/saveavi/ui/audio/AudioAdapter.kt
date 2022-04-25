package com.example.saveavi.ui.audio

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.saveavi.data.models.AudioEntity
import com.example.saveavi.databinding.ItemAudioBinding

class AudioAdapter(private val audios:List<AudioEntity>):RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val itemBinding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AudioViewHolder(itemBinding,parent.context)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(audios[position])
    }

    override fun getItemCount(): Int = audios.size

    inner class AudioViewHolder(private val binding : ItemAudioBinding,private val context: Context):RecyclerView.ViewHolder(binding.root){
        fun bind(audio : AudioEntity){
            val mediaPlayer  = MediaPlayer()
            var firstTimeSound = 0
            binding.txtTitle.text = audio.title
            binding.txtDescription.text = audio.description
            binding.imgPlay.setOnClickListener {
                binding.imgPlay.visibility = View.GONE
                binding.imgStop.visibility = View.VISIBLE
                if (firstTimeSound == 0) {
                    mediaPlayer.setDataSource(context, audio.audio.toUri())
                    firstTimeSound = 1
                }
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
            binding.imgStop.setOnClickListener {
                binding.imgPlay.visibility = View.VISIBLE
                binding.imgStop.visibility = View.GONE
                mediaPlayer.stop()
            }
        }
    }
}