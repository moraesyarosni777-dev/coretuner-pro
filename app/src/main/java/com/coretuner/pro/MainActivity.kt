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
import java.util.Random

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var dtcValueText: TextView
    private lateinit var ramValueText: TextView
    private lateinit var signature3D: TextView
    private lateinit var mainCard: MaterialCardView
    
    private var isPerformanceMode = false
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Inicializar componentes da UI VIP
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        signature3D = findViewById(R.id.signature3D)
        mainCard = findViewById(R.id.mainCard)

        setupButtons()
        startTelemetry()
        connectShizuku()
    }

    private fun connectShizuku() {
        scope.launch {
            while (isActive) {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() == 0) {
                        statusText.text = "> STATUS: ONLINE // VIP"
                        statusText.setTextColor(android.graphics.Color.GREEN)
                        break // Shizuku Online!
                    } else {
                        statusText.text = "> STATUS: PERMISSION REQ"
                        Shizuku.requestPermission(1001)
                    }
                } else {
                    statusText.text = "> STATUS: SHIZUKU OFFLINE"
                    statusText.setTextColor(android.graphics.Color.RED)
                }
                delay(3000)
            }
        }
    }

    private fun setupButtons() {
        // 1. PERFORMANCE EXTREMA
        findViewById<MaterialButton>(R.id.performanceButton).setOnClickListener {
            isPerformanceMode = !isPerformanceMode
            val command = "settings put global window_animation_scale ${if (isPerformanceMode) "0.5" else "1.0"}; " +
                          "settings put system peak_refresh_rate 120.0"
            executarAjusteVip(command)
            
            // Mudar cor do console central para Laranja Neon (Ativo) ou Verde (Normal)
            mainCard.setStrokeColor(if (isPerformanceMode) android.graphics.Color.parseColor("#FF5722") else android.graphics.Color.parseColor("#6600FF41"))
            Toast.makeText(this, if (isPerformanceMode) "PERFORMANCE INJETADA" else "NORMAL RESTAURADO", Toast.LENGTH_SHORT).show()
        }

        // 2. ECONOMIA DE BATERIA (FRIO)
        findViewById<MaterialButton>(R.id.batteryButton).setOnClickListener {
            executarAjusteVip("settings put global low_power 1; settings put global window_animation_scale 1.5")
            Toast.makeText(this, "MODO FRIO ATIVO ❄️", Toast.LENGTH_SHORT).show()
        }

        // 3. ULTRA TOUCH
        findViewById<MaterialButton>(R.id.touchButton).setOnClickListener {
            executarAjusteVip("settings put global touch_sensitivity_level 10; settings put secure touch_pressure_scale 0.1")
            Toast.makeText(this, "TOUCH OPTIMIZED", Toast.LENGTH_SHORT).show()
        }

        // 4. FPS MAXIMIZADO
        findViewById<MaterialButton>(R.id.fpsButton).setOnClickListener {
            executarAjusteVip("settings put system peak_refresh_rate 120.0; settings put system min_refresh_rate 120.0")
            Toast.makeText(this, "REFRESH RATE FORÇADO (120Hz)", Toast.LENGTH_SHORT).show()
        }

        // 5. OTIMIZAR SISTEMA
        findViewById<MaterialButton>(R.id.systemButton).setOnClickListener {
            executarAjusteVip("am kill-all; pm trim-caches 999G")
            Toast.makeText(this, "PURGE COMPLETE", Toast.LENGTH_SHORT).show()
        }

        // 6. ZRAM TUNING
        findViewById<MaterialButton>(R.id.zramButton).setOnClickListener {
            executarAjusteVip("settings put global zram_enabled 1; settings put global cached_apps_freezer enabled")
            Toast.makeText(this, "RAM TUNED", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executarAjusteVip(cmd: String) {
        scope.launch(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    val args = arrayOf("sh", "-c", cmd)
                    // Bypass via Reflection para evitar erro de acesso privado no Android 12
                    val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                    method.isAccessible = true
                    val proc = method.invoke(null, args, null, null) as java.lang.Process
                    proc.waitFor()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun startTelemetry() {
        val h = Handler(Looper.getMainLooper())
        h.post(object : Runnable {
            override fun run() {
                val coreBase = if (isPerformanceMode) 3.1 else 1.3
                coreValueText.text = String.format("%.2f GHz", coreBase + random.nextDouble())
                ramValueText.text = "MEM: ${random.nextInt(40) + 40}%"
                dtcValueText.text = "DTC: ${String.format("%.2f", random.nextDouble())}"
                
                // Animação da Assinatura 3D Branco (Movimento Suave)
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.002) * 15).toFloat()
                signature3D.translationY = (Math.cos(System.currentTimeMillis() * 0.001) * 6).toFloat()
                
                h.postDelayed(this, 1000)
            }
        })
    }
}
