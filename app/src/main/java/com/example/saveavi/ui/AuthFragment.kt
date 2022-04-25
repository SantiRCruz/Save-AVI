package com.example.saveavi.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.saveavi.MainActivity
import com.example.saveavi.R
import com.example.saveavi.databinding.FragmentAuthBinding


class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var binding : FragmentAuthBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthBinding.bind(view)

        clicks()
    }

    private fun clicks() {
        binding.btnAccess.setOnClickListener {
            val i = Intent(requireActivity(),MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }
}