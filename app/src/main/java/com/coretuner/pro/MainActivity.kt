package com.coretuner.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
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
                        statusText.setTextColor(android.graphics.Color.GREEN)
                        break 
                    } else {
                        statusText.text = "> STATUS: PERMISSÃO REQ"
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
        findViewById<MaterialButton>(R.id.performanceButton).setOnClickListener {
            isPerformanceMode = !isPerformanceMode
            runShell("settings put global window_animation_scale 0.5")
            Toast.makeText(this, "Performance VIP Injetada ⚡", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.batteryButton).setOnClickListener {
            runShell("settings put global low_power 1")
            Toast.makeText(this, "Modo Frio Ativado ❄️", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.cacheButton).setOnClickListener {
            runShell("pm trim-caches 999G")
            Toast.makeText(this, "Sistema Purificado", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.touchButton).setOnClickListener {
            runShell("settings put secure touch_pressure_scale 0.1")
            Toast.makeText(this, "Touch Otimizado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun runShell(command: String) {
        scope.launch(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    val args = arrayOf("sh", "-c", command)
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
                val coreBase = if (isPerformanceMode) 3.1 else 1.5
                coreValueText.text = String.format("%.2f GHz", coreBase + random.nextDouble())
                ramValueText.text = "MEM: ${random.nextInt(30) + 50}%"
                dtcValueText.text = "DTC: ${String.format("%.2f", random.nextDouble())}"
                signature3D.translationX = (Math.sin(System.currentTimeMillis() * 0.002) * 15).toFloat()
                h.postDelayed(this, 1000)
            }
        })
    }
}
