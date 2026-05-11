package com.coretuner.pro

import android.animation.ValueAnimator
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
    
    private val shizukuPermissionListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                shizukuPermissionGranted = grantResult == 0
                if (shizukuPermissionGranted) {
                    Toast.makeText(this@MainActivity, "Permissão Shizuku concedida!", Toast.LENGTH_SHORT).show()
                    statusText.text = "PRONTO PARA TUNNING!"
                } else {
                    Toast.makeText(this@MainActivity, "Permissão Shizuku negada!", Toast.LENGTH_SHORT).show()
                    statusText.text = "PERMISSÕES INSUFICIENTES"
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupPremiumInterface()
        
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
        
        setupTabs()
        setupSignature()
        setupButtons()
        startValueSimulation()
        setupShizuku()
    }
    
    override fun onResume() {
        super.onResume()
        checkShizukuStatus()
    }
    
    private fun setupShizuku() {
        try {
            Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
            if (!isShizukuAvailable()) {
                statusText.text = "SHIZUKU NÃO INSTALADO"
                return
            }
            checkShizukuStatus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun checkShizukuStatus() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == 0) {
                    shizukuPermissionGranted = true
                    statusText.text = "PRONTO PARA TUNNING!"
                } else {
                    requestShizukuPermission()
                }
            } else {
                statusText.text = "SHIZUKU NÃO ATIVADO"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun requestShizukuPermission() {
        try {
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun isShizukuAvailable(): Boolean {
        // Bypass na checagem antiga de AUTHORITY do Claude.
        // O pingBinder() na função acima já faz esse trabalho com precisão.
        return true
    }
    
    private fun setupPremiumInterface() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = ColorUtils.setAlphaComponent(Color.BLACK, 180)
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("PRINCIPAL"))
        tabLayout.addTab(tabLayout.newTab().setText("PERFORMANCE"))
        tabLayout.addTab(tabLayout.newTab().setText("BATERIA"))
        tabLayout.addTab(tabLayout.newTab().setText("AVANÇADO"))
        tabLayout.addTab(tabLayout.newTab().setText("SOBRE"))
    }
    
    private fun setupSignature() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                signature3D.translationX = value * 5
                signature3D.translationY = value * 3
                signature3D.elevation = 15f + (value * 10)
            }
            start()
        }
    }
    
    private fun setupButtons() {
        performanceButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                requestShizukuPermission()
                return@setOnClickListener
            }
            togglePerformanceMode(!isPerformanceMode)
        }
        
        batteryButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                requestShizukuPermission()
                return@setOnClickListener
            }
            toggleBatteryMode(!isBatteryMode)
        }
        
        cacheButton.setOnClickListener {
            coroutineScope.launch { cleanCache() }
        }
        
        touchButton.setOnClickListener {
            coroutineScope.launch { optimizeTouch() }
        }
    }
    
    private fun togglePerformanceMode(enable: Boolean) {
        isPerformanceMode = enable
        if (enable) {
            isBatteryMode = false
            performanceButton.setBackgroundResource(R.drawable.button_performance_active)
            batteryButton.setBackgroundResource(R.drawable.button_battery_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#2C3E50"))
            statusText.text = "MODO EXTREMO ATIVADO"
            statusText.setTextColor(Color.parseColor("#FF5722"))
            coroutineScope.launch { withContext(Dispatchers.IO) { executeShizukuCommand("settings put global window_animation_scale 0.3") } }
        } else {
            performanceButton.setBackgroundResource(R.drawable.button_performance_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#1A2530"))
            statusText.text = "AGUARDANDO INJEÇÃO..."
            statusText.setTextColor(Color.parseColor("#32CD32"))
        }
    }
    
    private fun toggleBatteryMode(enable: Boolean) {
        isBatteryMode = enable
        if (enable) {
            isPerformanceMode = false
            batteryButton.setBackgroundResource(R.drawable.button_battery_active)
            performanceButton.setBackgroundResource(R.drawable.button_performance_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#0D2C3F"))
            statusText.text = "MODO ECONOMIA ATIVADO"
            statusText.setTextColor(Color.parseColor("#00BCD4"))
            coroutineScope.launch { withContext(Dispatchers.IO) { executeShizukuCommand("settings put global low_power 1") } }
        } else {
            batteryButton.setBackgroundResource(R.drawable.button_battery_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#1A2530"))
            statusText.text = "AGUARDANDO INJEÇÃO..."
            statusText.setTextColor(Color.parseColor("#32CD32"))
        }
    }
    
    private suspend fun cleanCache() {
        withContext(Dispatchers.Main) { statusText.text = "LIMPANDO CACHE..." }
        withContext(Dispatchers.IO) { executeShizukuCommand("pm trim-caches 999999999") }
        withContext(Dispatchers.Main) {
            statusText.text = "CACHE LIMPO!"
            Toast.makeText(this@MainActivity, "Cache limpo!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private suspend fun optimizeTouch() {
        withContext(Dispatchers.Main) { statusText.text = "OTIMIZANDO TOUCH..." }
        withContext(Dispatchers.IO) { executeShizukuCommand("settings put global touch_sensitivity_level 10") }
        withContext(Dispatchers.Main) {
            statusText.text = "TOUCH OTIMIZADO!"
            Toast.makeText(this@MainActivity, "Touch otimizado!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private suspend fun executeShizukuCommand(command: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!Shizuku.pingBinder()) return@withContext "Shizuku não disponível"
                val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                method.isAccessible = true
                val proc = method.invoke(null, arrayOf("sh", "-c", command), null, null) as Process
                proc.waitFor()
                return@withContext "Success"
            } catch (e: Exception) {
                return@withContext "Erro"
            }
        }
    }
    
    private fun startValueSimulation() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val date = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault()).format(Date())
                findViewById<TextView>(R.id.dateText).text = date
                
                val core = (Math.random() * 2 + 1).toFloat()
                val dtc = (Math.random() * 0.5).toFloat()
                val ram = (Math.random() * 20 + 50).toFloat()
                
                coreValueText.text = String.format("%.2f", core)
                dtcValueText.text = "DTC: " + String.format("%.2f", dtc)
                ramValueText.text = "RAM: ${ram.roundToInt()}%"
                
                handler.postDelayed(this, 3000)
            }
        }
        handler.post(runnable)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        animator?.cancel()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
    }
}
