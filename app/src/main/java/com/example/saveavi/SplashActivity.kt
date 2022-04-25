package com.example.saveavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.saveavi.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding :ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()
        startTimer()
    }

    private fun startTimer() {
        object : CountDownTimer(2000,200){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                val i = Intent(this@SplashActivity,AuthActivity::class.java)
                startActivity(i)
                finish()
            }
        }.start()
    }
}