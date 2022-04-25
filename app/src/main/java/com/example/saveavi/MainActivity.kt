package com.example.saveavi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.saveavi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.mainNavigationView.setupWithNavController(navController)

        observeDestination()
    }

    private fun observeDestination() {
        navController.addOnDestinationChangedListener { controller,destination,arguments ->
            when(destination.id){
                R.id.createImageFragment->{binding.mainNavigationView.visibility = View.GONE}
                R.id.createVideoFragment->{binding.mainNavigationView.visibility = View.GONE}
                R.id.createAudioFragment->{binding.mainNavigationView.visibility = View.GONE}
                R.id.imageFragment->{binding.mainNavigationView.visibility = View.VISIBLE}
                R.id.videoFragment->{binding.mainNavigationView.visibility = View.VISIBLE}
                R.id.audioFragment->{binding.mainNavigationView.visibility = View.VISIBLE}
            }
        }
    }
}