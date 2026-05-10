package com.coretuner.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var dtcValueText: TextView
    private lateinit var ramValueText: TextView
    private lateinit var signature3D: TextView
    private lateinit var mainCard: MaterialCardView
    
    private var isPerformanceMode = false
    private var isBatteryMode = false
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        signature3D = findViewById(R.id.signature3D)
        mainCard = findViewById(R.id.mainCard)

        setupPremiumActions()
        iniciarFluxoDeDados()
        conectarShizukuVip()
    }

    private fun conectarShizukuVip() {
        scope.launch {
            while (isActive) {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() == 0) {
                        statusText.text = "SISTEMA ONLINE // VIP"
                        statusText.setTextColor(android.graphics.Color.parseColor("#32CD32"))
                        break 
                    } else {
                        statusText.text = "AGUARDANDO AUTORIZAÇÃO..."
                        Shizuku.requestPermission(1001)
                    }
                } else {
                    statusText.text = "SHIZUKU OFFLINE"
                    statusText.setTextColor(android.graphics.Color.RED)
                }
                delay(3000)
            }
        }
    }

    private fun setupPremiumActions() {
        // MODO EXTREMO (RAIO)
        findViewById<MaterialButton>(R.id.performanceButton).setOnClickListener {
            isPerformanceMode = !isPerformanceMode
            if (isPerformanceMode) {
                isBatteryMode = false
                executarShellVip("settings put global window_animation_scale 0.5")
                statusText.text = "MODO EXTREMO ATIVADO ⚡"
                statusText.setTextColor(android.graphics.Color.parseColor("#FF5722"))
                mainCard.setStrokeColor(android.graphics.Color.parseColor("#FF5722"))
                Toast.makeText(this, "Performance Injetada!", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "SISTEMA ONLINE // VIP"
                statusText.setTextColor(android.graphics.Color.parseColor("#32CD32"))
                mainCard.setStrokeColor(android.graphics.Color.parseColor("#3300E5FF"))
            }
        }

        // MODO ECONOMIA (GELO)
        findViewById<MaterialButton>(R.id.batteryButton).setOnClickListener {
            isBatteryMode = !isBatteryMode
            if (isBatteryMode) {
                isPerformanceMode = false
                executarShellVip("settings put global low_power 1")
                statusText.text = "MODO FRIO ATIVADO ❄️"
                statusText.setTextColor(android.graphics.Color.parseColor("#00BCD4"))
                mainCard.setStrokeColor(android.graphics.Color.parseColor("#00BCD4"))
                Toast.makeText(this, "Resfriamento Iniciado", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<MaterialButton>(R.id.cacheButton).setOnClickListener {
            executarShellVip("pm trim-caches 999G")
            Toast.makeText(this, "SISTEMA PURIFICADO", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.touchButton).setOnClickListener {
            executarShellVip("settings put secure touch_pressure_scale 0.1")
            Toast.makeText(this, "Touch Otimizado (VIP)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executarShellVip(cmd: String) {
        scope.launch(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    val args = arrayOf("sh", "-c", cmd)
                    val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                    method.isAccessible = true
                    val proc = method.invoke(null, args, null, null) as java.lang.Process
                    proc.waitFor()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun iniciarFluxoDeDados() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                // Simulação da telemetria VIP
                val core = if (isPerformanceMode) (2.8..3.4).random() + Math.random() else (1.1..2.3).random() + Math.random()
                coreValueText.text = String.format("%.2f", core)
                ramValueText.text = "RAM_LOAD: ${(30..90).random()}%"
                dtcValueText.text = "DTC_LINK: ${String.format("%.2f", Math.random())}"
                
                // Animação da Assinatura 3D Moraes Yarosni
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.002) * 12).toFloat()
                signature3D.translationY = (Math.cos(System.currentTimeMillis() * 0.001) * 5).toFloat()
                
                handler.postDelayed(this, 1000)
            }
        })
    }
}
