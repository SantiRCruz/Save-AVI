package com.example.saveavi.ui.image

import android.app.Activity
import android.app.AlertDialog
import android.app.usage.ExternalStorageStats
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saveavi.R
import com.example.saveavi.core.Result
import com.example.saveavi.data.local.AppDatabase
import com.example.saveavi.data.models.ImageEntity
import com.example.saveavi.databinding.DialogChooseMediaBinding
import com.example.saveavi.databinding.FragmentCreateImageBinding
import com.example.saveavi.presentation.ImageViewModel
import com.example.saveavi.presentation.ImageViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.jar.Manifest


class CreateImageFragment : Fragment(R.layout.fragment_create_image) {
    private lateinit var binding : FragmentCreateImageBinding
    private var uriResult : Uri?= null
    private lateinit var root:String

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_OK){
//                val imageBitmap = it?.data?.extras?.get("data") as Bitmap
//                val imageBitmap = BitmapFactory.decodeFile(root)
//                bitmapResult = imageBitmap
                uriResult = root.toUri()
                binding.imgBackGround.setImageURI(root.toUri())
            }
        })

    private val viewModel by viewModels<ImageViewModel> {ImageViewModelFactory(AppDatabase.getImageDatabase(requireContext()).ImageDao())}

    @RequiresApi(Build.VERSION_CODES.P)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            pickPhotoFromGallery()
        }else{
            Snackbar.make(binding.root,"You need to enable the permission",Snackbar.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data?.data
            uriResult = data
            // con esto convertimos de uri a bitmap con el nuevo ImageDecorder
//            val source = ImageDecoder.createSource(requireContext().contentResolver,data!!)
//            bitmapResult = ImageDecoder.decodeBitmap(source)
            // con esto convertimos de uri a bitmap con el nuevo ImageDecorder

            binding.imgBackGround.setImageURI(data)
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateImageBinding.bind(view)
        clicks()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun clicks() {
        binding.imgBack.setOnClickListener {findNavController().popBackStack()}
        binding.imgUpload.setOnClickListener { dialogMedia() }
        binding.btnSave.setOnClickListener {saveImage()}
    }

    private fun saveImage() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.saveImage(ImageEntity(0,uriResult.toString(),binding.titleEdt.text.toString(),binding.DescriptionEdt.text.toString())).collect {
                    when(it){
                        is Result.Loading ->{}
                        is Result.Success ->{
                            Snackbar.make(binding.root,"saved correctly",Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is Result.Failure ->{}
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun dialogMedia(){
        val dialogBinding = DialogChooseMediaBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.imgExit.setOnClickListener {dialog.dismiss()}
        dialogBinding.imgCamera.setOnClickListener {
            pickPhotoCamera()
            dialog.dismiss()
        }
        dialogBinding.imgGallery.setOnClickListener {
            requestPermission()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->{ pickPhotoFromGallery() }
                else ->requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            pickPhotoFromGallery()
        }
    }

    private fun pickPhotoCamera(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA),1000)
        }

        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile:File ?= null
        try {
            photoFile = saveImageStorage()
        }catch (e : Exception){
            Snackbar.make(binding.root,"No se encontro ninguna app para abrir la camara",Snackbar.LENGTH_SHORT).show()
        }
        if (photoFile != null){
            val uri = FileProvider.getUriForFile(requireContext(),"com.example.saveavi.fileprovider",photoFile)
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            try {
                imageResult.launch(takePicture)
            }catch (e:Exception){
                Snackbar.make(binding.root,"No se encontro ninguna app para abrir la camara",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageStorage(): File? {
        val photoName = "foto_"
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photo = File.createTempFile(photoName,".jpg",directory)
        root = photo.absolutePath
        return photo

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        galleryResult.launch(intent)
    }
}