package com.example.saveavi.ui.image

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveavi.R
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.AppDatabase
import com.example.saveavi.databinding.FragmentImageBinding
import com.example.saveavi.presentation.ImageViewModel
import com.example.saveavi.presentation.ImageViewModelFactory

class ImageFragment : Fragment(R.layout.fragment_image) {
private lateinit var binding : FragmentImageBinding
private val viewModel by viewModels<ImageViewModel> {
    ImageViewModelFactory(AppDatabase.getImageDatabase(requireContext()).ImageDao())
}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageBinding.bind(view)

        obtainImages()
        clicks()

    }

    private fun clicks() {
        binding.btnAddImage.setOnClickListener { findNavController().navigate(R.id.action_imageFragment_to_createImageFragment) }
    }

    private fun obtainImages() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchImages().collect {
                    when(it){
                        is Result.Loading ->{}
                        is Result.Success ->{
                            val adapter = ImageAdapter(it.data)
                            binding.rvImages.layoutManager = GridLayoutManager(requireContext(),2)
                            binding.rvImages.adapter = adapter
                        }
                        is Result.Failure ->{}
                    }
                }
            }
        }
    }
}