package com.example.saveavi.ui.audio

import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.saveavi.R
import com.example.saveavi.databinding.DialogChooseMediaBinding
import com.example.saveavi.databinding.DialogRecorderBinding
import com.example.saveavi.databinding.FragmentCreateAudioBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import android.Manifest
import android.provider.MediaStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.AppDatabase
import com.example.saveavi.data.models.AudioEntity
import com.example.saveavi.presentation.AudioViewModel
import com.example.saveavi.presentation.AudioViewModelFactory

class CreateAudioFragment : Fragment(R.layout.fragment_create_audio) {
    private lateinit var binding: FragmentCreateAudioBinding
    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.S)
    private lateinit var mediaRecorder: MediaRecorder
    private var root: String? = null
    private var uriResult: Uri? = null
    private var firstTimeSound = 0
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickAudioFromGallery()
            } else {
                Snackbar.make(
                    binding.root,
                    "You need to enable the permission",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                mediaPlayer = MediaPlayer()
                firstTimeSound = 0
                binding.imgPlay.visibility = View.VISIBLE
                binding.imgStop.visibility = View.GONE
                val data = it.data?.data
                uriResult = null
                uriResult = data
            }
        }

    private val viewModel by viewModels<AudioViewModel> { AudioViewModelFactory(AppDatabase.getAudioDatabase(requireContext()).AudioDao()) }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateAudioBinding.bind(view)

        clicks()

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun clicks() {
        binding.imgPlay.setOnClickListener {
            if (firstTimeSound == 0) {
                mediaPlayer.setDataSource(requireContext(), uriResult!!)
                firstTimeSound = 1
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
            binding.imgPlay.visibility = View.GONE
            binding.imgStop.visibility = View.VISIBLE
        }
        binding.imgStop.setOnClickListener {
            mediaPlayer.stop()
            binding.imgPlay.visibility = View.VISIBLE
            binding.imgStop.visibility = View.GONE
        }
        binding.imgBack.setOnClickListener {
            mediaPlayer.stop()
            findNavController().popBackStack()
        }
        binding.imgUpload.setOnClickListener { dialogMedia() }
        binding.btnSave.setOnClickListener { saveAudio() }
    }

    private fun saveAudio() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.saveAudio(AudioEntity(0,uriResult.toString(),binding.titleEdt.text.toString(),binding.DescriptionEdt.text.toString())).collect {
                    when(it){
                        is Result.Loading ->{}
                        is Result.Success ->{
                            Snackbar.make(binding.root,"Save Correctly",Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is Result.Failure ->{
                            Snackbar.make(binding.root,it.toString(),Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun dialogMedia() {
        val dialogBinding = DialogChooseMediaBinding.inflate(LayoutInflater.from(requireContext()))

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.cameraIcon.setImageResource(R.drawable.ic_baseline_mic_none_24)
        dialogBinding.textView2.text = "Storage"
        dialogBinding.txtCameraIcon.text = "Record"
        dialogBinding.galleryIcon.setImageResource(R.drawable.ic_baseline_storage_24)
        dialogBinding.imgExit.setOnClickListener { alertDialog.dismiss() }
        dialogBinding.imgGallery.setOnClickListener {
            requestPermission()
            alertDialog.dismiss()
        }
        dialogBinding.imgCamera.setOnClickListener {
            dialogRecorder()
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickAudioFromGallery()
                }
                else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun pickAudioFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        galleryResult.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun dialogRecorder() {
        val dialogBinding = DialogRecorderBinding.inflate(LayoutInflater.from(requireContext()))

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()


        dialogBinding.imgExit.setOnClickListener { alertDialog.dismiss() }
        dialogBinding.imgRecord.setOnClickListener {
            if (isMicrophonePresent()) {
                getMicrophonePermission()
            }
            try {
                mediaRecorder = MediaRecorder()
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setOutputFile(getRecordingFilePath())
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder.prepare()
                mediaRecorder.start()
                dialogBinding.imgRecord.visibility = View.GONE
                dialogBinding.imgStop.visibility = View.VISIBLE
                dialogBinding.txtRecording.visibility = View.VISIBLE
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_SHORT).show()
            }


        }
        dialogBinding.imgStop.setOnClickListener {
            mediaRecorder.stop()
            mediaRecorder.release()
            dialogBinding.imgRecord.visibility = View.GONE
            dialogBinding.imgStop.visibility = View.VISIBLE
            dialogBinding.txtRecording.visibility = View.VISIBLE
            binding.imgPlay.visibility = View.VISIBLE
            firstTimeSound = 0
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun getRecordingFilePath(): String {
        val audioName = "audio_"
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File.createTempFile(audioName, ".mp3", directory)
        uriResult = file.path.toUri()
        mediaPlayer = MediaPlayer()
        return file.path

    }

    private fun isMicrophonePresent(): Boolean {
        return requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    private fun getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ),
                200
            )
        }
    }
}