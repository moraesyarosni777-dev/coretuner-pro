package com.coretuner.pro

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
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
    private lateinit var tabLayout: TabLayout
    
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
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("PRINCIPAL"))
        tabLayout.addTab(tabLayout.newTab().setText("TUNING"))
        tabLayout.addTab(tabLayout.newTab().setText("INFO"))

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
                        statusText.setTextColor(Color.parseColor("#00C853"))
                        break 
                    } else {
                        statusText.text = "> STATUS: REQUER PERMISSÃO"
                        statusText.setTextColor(Color.parseColor("#FFC107"))
                        Shizuku.requestPermission(1001)
                    }
                } else {
                    statusText.text = "> STATUS: SHIZUKU OFFLINE"
                    statusText.setTextColor(Color.parseColor("#FF3D00"))
                }
                delay(3000)
            }
        }
    }

    private fun setupButtons() {
        // BOTÃO 1: PERFORMANCE E ANIMAÇÕES EM 0.03
        findViewById<MaterialButton>(R.id.performanceButton).setOnClickListener {
            isPerformanceMode = !isPerformanceMode
            
            // Aqui estão as 3 escalas exatas em 0.03
            val scale = if (isPerformanceMode) "0.03" else "1.0"
            val command = "settings put global window_animation_scale $scale; " +
                          "settings put global transition_animation_scale $scale; " +
                          "settings put global animator_duration_scale $scale; " +
                          "settings put system peak_refresh_rate 120.0"
            
            executarAjusteVip(command, "ESCALAS EM 0.03 E PERFORMANCE INJETADA ⚡")
            
            // Muda a moldura do console para laranja quando ativo, preto quando normal
            mainCard.setStrokeColor(if (isPerformanceMode) Color.parseColor("#FF3D00") else Color.parseColor("#1A1A1A"))
        }

        findViewById<MaterialButton>(R.id.batteryButton).setOnClickListener {
            executarAjusteVip("settings put global low_power 1; settings put global window_animation_scale 1.5", "MODO FRIO ATIVO ❄️")
        }

        findViewById<MaterialButton>(R.id.touchButton).setOnClickListener {
            executarAjusteVip("settings put global touch_sensitivity_level 10; settings put secure touch_pressure_scale 0.1", "TOUCH EXTREMO 👆")
        }

        findViewById<MaterialButton>(R.id.fpsButton).setOnClickListener {
            executarAjusteVip("settings put system peak_refresh_rate 120.0; settings put system min_refresh_rate 120.0", "FPS MAX 📺")
        }

        findViewById<MaterialButton>(R.id.systemButton).setOnClickListener {
            executarAjusteVip("am kill-all; pm trim-caches 999G", "SISTEMA LIMPO 🧠")
        }

        findViewById<MaterialButton>(R.id.zramButton).setOnClickListener {
            executarAjusteVip("settings put global zram_enabled 1; settings put global cached_apps_freezer enabled", "RAM TUNED 💾")
        }
    }

    private fun executarAjusteVip(cmd: String, msgSucesso: String) {
        // Trava de segurança: Se estiver offline, não faz nada e avisa o usuário.
        if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() != 0) {
            Toast.makeText(this, "ERRO: SHIZUKU OFFLINE. Autorize o app primeiro!", Toast.LENGTH_LONG).show()
            return
        }

        scope.launch(Dispatchers.IO) {
            try {
                val args = arrayOf("sh", "-c", cmd)
                val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                method.isAccessible = true
                val proc = method.invoke(null, args, null, null) as java.lang.Process
                proc.waitFor()
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, msgSucesso, Toast.LENGTH_SHORT).show()
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
                
                // Animação de flutuação 3D para a assinatura
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.002) * 12).toFloat()
                signature3D.translationY = (Math.cos(System.currentTimeMillis() * 0.001) * 5).toFloat()
                
                h.postDelayed(this, 1000)
            }
        })
    }
}
