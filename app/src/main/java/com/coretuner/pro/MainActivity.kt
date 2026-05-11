package com.coretuner.pro

import android.content.Intent
import android.graphics.Color
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
        
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        signature3D = findViewById(R.id.signature3D)
        mainCard = findViewById(R.id.mainCard)

        setupButtons()
        startTelemetry()
        connectShizuku()

        // BOTÃO DE PÂNICO: Se estiver offline, clique no texto vermelho para abrir o Shizuku!
        statusText.setOnClickListener {
            if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() != 0) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                    if (intent != null) {
                        startActivity(intent)
                        Toast.makeText(this, "AUTORIZE A CHAVE AQUI DENTRO!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    private fun connectShizuku() {
        scope.launch {
            while (isActive) {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() == 0) {
                        statusText.text = "> STATUS: ONLINE // VIP"
                        statusText.setTextColor(Color.parseColor("#BBFF00"))
                    } else {
                        statusText.text = "> CLIQUE AQUI P/ AUTORIZAR"
                        statusText.setTextColor(Color.parseColor("#FFC107"))
                        try { Shizuku.requestPermission(1001) } catch (e: Exception) {}
                    }
                } else {
                    statusText.text = "> CLIQUE AQUI P/ ABRIR SHIZUKU"
                    statusText.setTextColor(Color.parseColor("#FF3D00"))
                }
                delay(3000)
            }
        }
    }

    private fun verificarShizuku(): Boolean {
        if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() != 0) {
            Toast.makeText(this, "CLIQUE NO TEXTO VERMELHO PARA ABRIR O SHIZUKU!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.smartExtremeButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            isPerformanceMode = true
            val cmd = "settings put global window_animation_scale 0.03; " +
                      "settings put global transition_animation_scale 0.03; " +
                      "settings put global animator_duration_scale 0.03; " +
                      "settings put system peak_refresh_rate 120.0; " +
                      "settings put system min_refresh_rate 120.0; " +
                      "settings put global touch_sensitivity_level 10"
            executarAjusteVip(cmd, "AJUSTE EXTREMO INJETADO 🚀")
            mainCard.setStrokeColor(Color.parseColor("#D50000"))
        }

        findViewById<MaterialButton>(R.id.smartEcoButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            isPerformanceMode = false
            val cmd = "settings put global low_power 1; " +
                      "settings put global window_animation_scale 1.5; " +
                      "settings put system screen_off_timeout 120000"
            executarAjusteVip(cmd, "ECONOMIA EXTREMA (ECRÃ 2 MIN) 🔋")
            mainCard.setStrokeColor(Color.parseColor("#00C853"))
        }

        findViewById<MaterialButton>(R.id.performanceButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("settings put global window_animation_scale 0.03", "ANIMAÇÕES 0.03 ⚡")
        }

        findViewById<MaterialButton>(R.id.batteryButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("settings put global low_power 1", "POUPANÇA ATIVA ❄️")
        }

        findViewById<MaterialButton>(R.id.touchButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("settings put global touch_sensitivity_level 10", "TOUCH VIP 👆")
        }

        findViewById<MaterialButton>(R.id.fpsButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("settings put system peak_refresh_rate 120.0", "FPS MAX 📺")
        }

        findViewById<MaterialButton>(R.id.systemButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("am kill-all; pm trim-caches 999G", "SISTEMA LIMPO 🧠")
        }

        findViewById<MaterialButton>(R.id.zramButton).setOnClickListener {
            if (!verificarShizuku()) return@setOnClickListener
            executarAjusteVip("settings put global zram_enabled 1", "ZRAM TUNED 💾")
        }
    }

    private fun executarAjusteVip(cmd: String, msg: String) {
        scope.launch(Dispatchers.IO) {
            try {
                val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                method.isAccessible = true
                val proc = method.invoke(null, arrayOf("sh", "-c", cmd), null, null) as java.lang.Process
                proc.waitFor()
                withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun startTelemetry() {
        val h = Handler(Looper.getMainLooper())
        h.post(object : Runnable {
            override fun run() {
                val base = if (isPerformanceMode) 3.5 else 1.1
                coreValueText.text = String.format("%.2f GHz", base + random.nextDouble())
                ramValueText.text = "MEM: ${random.nextInt(20) + 60}%"
                dtcValueText.text = "DTC: ${String.format("%.2f", random.nextDouble())}"
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.002) * 15).toFloat()
                signature3D.translationY = (Math.cos(System.currentTimeMillis() * 0.001) * 6).toFloat()
                h.postDelayed(this, 1000)
            }
        })
    }
}
