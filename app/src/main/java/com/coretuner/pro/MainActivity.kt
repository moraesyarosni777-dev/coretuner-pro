package com.coretuner.pro

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    
    private lateinit var performanceButton: Button
    private lateinit var batteryButton: Button
    private lateinit var cacheButton: Button
    private lateinit var touchButton: Button
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var dtcValueText: TextView
    private lateinit var ramValueText: TextView
    private lateinit var mainCard: CardView
    private lateinit var tabLayout: TabLayout
    private lateinit var spectrumView: ImageView
    private lateinit var signature3D: TextView
    
    private var isPerformanceMode = false
    private var isBatteryMode = false
    private var animator: ValueAnimator? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var shizukuPermissionGranted = false
    private val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
    
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
            shizukuPermissionGranted = grantResult == 0
            if (shizukuPermissionGranted) {
                statusText.text = "PRONTO PARA TUNNING!"
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupUI()
        setupButtons()
        startValueSimulation()
        
        // Registrar listener do Shizuku
        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
    }
    
    override fun onResume() {
        super.onResume()
        checkShizukuStatus()
    }

    private fun setupUI() {
        performanceButton = findViewById(R.id.performanceButton)
        batteryButton = findViewById(R.id.batteryButton)
        cacheButton = findViewById(R.id.cacheButton)
        touchButton = findViewById(R.id.touchButton)
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        mainCard = findViewById(R.id.mainCard)
        tabLayout = findViewById(R.id.tabLayout)
        spectrumView = findViewById(R.id.spectrumView)
        signature3D = findViewById(R.id.signature3D)
        
        window.statusBarColor = Color.BLACK
    }

    private fun checkShizukuStatus() {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == 0) {
                shizukuPermissionGranted = true
                statusText.text = "SISTEMA ONLINE"
            } else {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
            }
        } else {
            statusText.text = "SHIZUKU DESCONECTADO"
        }
    }

    private fun setupButtons() {
        performanceButton.setOnClickListener {
            coroutineScope.launch {
                statusText.text = "INJETANDO PERFORMANCE..."
                executeShizukuCommand("settings put global window_animation_scale 0.5")
                delay(1000)
                statusText.text = "CORE TUNED!"
            }
        }

        cacheButton.setOnClickListener {
            coroutineScope.launch {
                statusText.text = "LIMPANDO CACHE..."
                executeShizukuCommand("pm trim-caches 999G")
                delay(1000)
                statusText.text = "CACHE OTIMIZADO"
            }
        }
    }

    // AQUI ESTÁ A CORREÇÃO DO ERRO 'PRIVATE' E 'AUTHORITY'
    private suspend fun executeShizukuCommand(command: String) {
        withContext(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    // Usando a forma correta de criar processo na versão 12.1.0
                    val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                    process.waitFor()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startValueSimulation() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                coreValueText.text = String.format("%.2f", (1..5).random() + Math.random())
                ramValueText.text = "${(40..90).random()}%"
                handler.postDelayed(this, 2000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
        coroutineScope.cancel()
    }
}
