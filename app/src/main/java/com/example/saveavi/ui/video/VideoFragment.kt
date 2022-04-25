package com.example.saveavi.ui.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveavi.R
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.AppDatabase
import com.example.saveavi.databinding.FragmentVideoBinding
import com.example.saveavi.presentation.VideoViewModel
import com.example.saveavi.presentation.VideoViewModelFactory

class VideoFragment : Fragment(R.layout.fragment_video) {
    private lateinit var binding : FragmentVideoBinding
    private val viewModel  by viewModels<VideoViewModel> { VideoViewModelFactory(AppDatabase.getVideoDatabase(requireContext()).VideoDao()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideoBinding.bind(view)

        obtainVideos()
        clicks()

    }

    private fun obtainVideos() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchVideos().collect {
                    when(it){
                        is Result.Loading ->{}
                        is Result.Success ->{
                            val adapter = VideoAdapter(it.data)
                            binding.rvVideos.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                            binding.rvVideos.adapter = adapter
                        }
                        is Result.Failure ->{}
                    }
                }
            }
        }
    }

    private fun clicks() {
        binding.btnAddVideo.setOnClickListener {findNavController().navigate(R.id.action_videoFragment_to_createVideoFragment)}
    }
}