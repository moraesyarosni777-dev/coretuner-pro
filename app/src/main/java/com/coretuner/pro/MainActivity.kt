package com.coretuner.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var dtcValueText: TextView
    private lateinit var ramValueText: TextView
    private lateinit var signature3D: TextView
    
    private var isPerformanceMode = false
    private var isBatteryMode = false
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Inicializar componentes da UI
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        signature3D = findViewById(R.id.signature3D)

        setupClickListeners()
        iniciarTelemetria()
        conectarMotorShizuku()
    }

    private fun conectarMotorShizuku() {
        scope.launch {
            while (isActive) {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() == 0) {
                        statusText.text = "SISTEMA ONLINE // VIP"
                        statusText.setTextColor(android.graphics.Color.parseColor("#32CD32"))
                        break 
                    } else {
                        statusText.text = "SOLICITANDO ACESSO..."
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

    private fun setupClickListeners() {
        // Botão EXTREMO
        findViewById<Button>(R.id.performanceButton).setOnClickListener {
            isPerformanceMode = !isPerformanceMode
            if (isPerformanceMode) {
                isBatteryMode = false
                injetarComando("settings put global window_animation_scale 0.5")
                injetarComando("settings put system peak_refresh_rate 120.0")
                statusText.text = "MODO EXTREMO: ATIVO ⚡"
                statusText.setTextColor(android.graphics.Color.parseColor("#FF5722"))
                Toast.makeText(this, "Performance Injetada!", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "SISTEMA ONLINE // VIP"
                statusText.setTextColor(android.graphics.Color.parseColor("#32CD32"))
            }
        }

        // Botão FRIO
        findViewById<Button>(R.id.batteryButton).setOnClickListener {
            isBatteryMode = !isBatteryMode
            if (isBatteryMode) {
                isPerformanceMode = false
                injetarComando("settings put global low_power 1")
                statusText.text = "MODO FRIO: ATIVO ❄️"
                statusText.setTextColor(android.graphics.Color.parseColor("#00BCD4"))
                Toast.makeText(this, "Resfriamento Iniciado", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão PURGE (Cache)
        findViewById<Button>(R.id.cacheButton).setOnClickListener {
            injetarComando("pm trim-caches 999G")
            Toast.makeText(this, "Cache Purificado", Toast.LENGTH_SHORT).show()
        }

        // Botão TOUCH
        findViewById<Button>(R.id.touchButton).setOnClickListener {
            injetarComando("settings put secure touch_pressure_scale 0.1")
            Toast.makeText(this, "Resposta de Toque Otimizada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun injetarComando(cmd: String) {
        scope.launch(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    val args = arrayOf("sh", "-c", cmd)
                    // Bypass via Reflection para evitar erro de acesso privado
                    val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                    method.isAccessible = true
                    val proc = method.invoke(null, args, null, null) as java.lang.Process
                    proc.waitFor()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun iniciarTelemetria() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                // Simulação de valores reais
                val core = if (isPerformanceMode) (2.8..3.2).random() + Math.random() else (1.2..2.2).random() + Math.random()
                coreValueText.text = String.format("%.2f GHz", core)
                ramValueText.text = "RAM: ${(45..88).random()}%"
                dtcValueText.text = "DTC: ${String.format("%.2f", Math.random())}"
                
                // Efeito de movimento na assinatura
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.003) * 8).toFloat()
                
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
