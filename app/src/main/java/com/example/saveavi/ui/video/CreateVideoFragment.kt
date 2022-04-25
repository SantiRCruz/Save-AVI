package com.example.saveavi.ui.video

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saveavi.R
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.AppDatabase
import com.example.saveavi.data.models.VideoEntity
import com.example.saveavi.databinding.DialogChooseMediaBinding
import com.example.saveavi.databinding.FragmentCreateVideoBinding
import com.example.saveavi.presentation.VideoViewModel
import com.example.saveavi.presentation.VideoViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlin.Exception

class CreateVideoFragment : Fragment(R.layout.fragment_create_video) {
    private lateinit var binding : FragmentCreateVideoBinding
    private var uriResult : Uri ?=null
    private lateinit var root : String
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data?.data
            binding.imgBackGround.visibility = View.GONE
            binding.videoBackGround.visibility = View.VISIBLE
            val mediaController  = MediaController(requireContext())
            mediaController.setAnchorView(binding.videoBackGround)

            binding.videoBackGround.setMediaController(mediaController)
            binding.videoBackGround.setVideoURI(data)
            binding.videoBackGround.start()
            uriResult = data
        }
    }
    private val videoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            uriResult = root.toUri()
            binding.imgBackGround.visibility = View.GONE
            binding.videoBackGround.visibility = View.VISIBLE
            val mediaController  = MediaController(requireContext())
            mediaController.setAnchorView(binding.videoBackGround)

            binding.videoBackGround.setMediaController(mediaController)
            binding.videoBackGround.setVideoURI(root.toUri())
            binding.videoBackGround.start()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            pickVideoFromGallery()
        }else{
            Snackbar.make(binding.root,"You need to enable the permission",Snackbar.LENGTH_SHORT).show()
        }
    }

    private val viewModel by viewModels<VideoViewModel> { VideoViewModelFactory(AppDatabase.getVideoDatabase(requireContext()).VideoDao()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateVideoBinding.bind(view)

        clicks()
    }

    private fun clicks() {
        binding.imgBack.setOnClickListener {findNavController().popBackStack()}
        binding.imgUpload.setOnClickListener { dialogMedia() }
        binding.btnSave.setOnClickListener { saveVideo() }
    }

    private fun saveVideo() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.saveVideo(VideoEntity(0,uriResult.toString(),binding.titleEdt.text.toString(),binding.DescriptionEdt.text.toString())).collect {
                    when(it){
                        is Result.Loading->{}
                        is Result.Success->{
                            Snackbar.make(binding.root,"saved correctly",Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is Result.Failure->{}
                    }
                }
            }
        }
    }

    private fun dialogMedia() {
        val dialogBinding = DialogChooseMediaBinding.inflate(LayoutInflater.from(requireContext()))

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()
        dialogBinding.imgExit.setOnClickListener { alertDialog.dismiss() }
        dialogBinding.imgGallery.setOnClickListener {
            requestPermission()
            alertDialog.dismiss() }
        dialogBinding.imgCamera.setOnClickListener {
            pickVideoFromCamera()
            alertDialog.dismiss() }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED -> {pickVideoFromGallery()}
                else ->requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            pickVideoFromGallery()
        }
    }

    private fun pickVideoFromGallery() {
       val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        galleryResult.launch(intent)

    }
    private fun pickVideoFromCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA),1000)
        }

        val takeVideo = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        var videoFile: File?= null
        try {
            videoFile = saveVideoStorage()
        }catch (e:Exception){
            Snackbar.make(binding.root,"Don't find some app to open de camera",Snackbar.LENGTH_SHORT).show()
        }
        if (videoFile != null){
            val uri = FileProvider.getUriForFile(requireContext(),"com.example.saveavi.fileprovider",videoFile)
            takeVideo.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            try {
                videoResult.launch(takeVideo)
            }catch (e:Exception){
                Snackbar.make(binding.root,"Don't find some app to open de camera",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveVideoStorage(): File? {
        val videoName = "video_"
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val video = File.createTempFile(videoName,".mp4",directory)
        root = video.absolutePath
        return video
    }


}