package com.example.saveavi.ui.audio

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
import com.example.saveavi.databinding.FragmentAudioBinding
import com.example.saveavi.presentation.AudioViewModel
import com.example.saveavi.presentation.AudioViewModelFactory

class AudioFragment : Fragment(R.layout.fragment_audio) {
    private lateinit var binding : FragmentAudioBinding
    private val viewModel by viewModels<AudioViewModel> { AudioViewModelFactory(AppDatabase.getAudioDatabase(requireContext()).AudioDao()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAudioBinding.bind(view)

        obtainAudios()
        clicks()

    }

    private fun obtainAudios() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchAudios().collect {
                    when(it){
                        is Result.Loading->{}
                        is Result.Success->{
                            val adapter = AudioAdapter(it.data)
                            binding.rvAudios.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvAudios.adapter=adapter
                        }
                        is Result.Failure->{}
                    }
                }
            }
        }
    }

    private fun clicks() {
        binding.btnAddAudio.setOnClickListener { findNavController().navigate(R.id.action_audioFragment_to_createAudioFragment) }
    }
}