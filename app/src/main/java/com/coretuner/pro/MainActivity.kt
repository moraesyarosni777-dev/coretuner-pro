package com.coretuner.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var performanceButton: Button
    private lateinit var cacheButton: Button
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var ramValueText: TextView
    
    private val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE && grantResult == 0) {
            runOnUiThread { statusText.text = "SISTEMA ONLINE" }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        performanceButton = findViewById(R.id.performanceButton)
        cacheButton = findViewById(R.id.cacheButton)
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        ramValueText = findViewById(R.id.ramValueText)
        
        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
        
        setupButtons()
        startValueSimulation()
    }
    
    override fun onResume() {
        super.onResume()
        checkShizuku()
    }

    private fun checkShizuku() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() != 0) {
                    Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
                } else {
                    statusText.text = "SISTEMA ONLINE"
                }
            } else {
                statusText.text = "SHIZUKU OFFLINE"
            }
        } catch (e: Exception) {
            statusText.text = "ERRO DE CONEXÃO"
        }
    }

    private fun setupButtons() {
        performanceButton.setOnClickListener {
            runShell("settings put global window_animation_scale 0.5")
            Toast.makeText(this, "Performance Injetada", Toast.LENGTH_SHORT).show()
        }

        cacheButton.setOnClickListener {
            runShell("pm trim-caches 999G")
            Toast.makeText(this, "Limpando Sistema...", Toast.LENGTH_SHORT).show()
        }
    }

    // AQUI ESTÁ O SEGREDO: Usamos a classe Shizuku de forma que o Kotlin não bloqueie
    private fun runShell(command: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                if (Shizuku.pingBinder()) {
                    // Forma alternativa para burlar o erro de 'private'
                    val args = arrayOf("sh", "-c", command)
                    val remoteProcess = Shizuku::class.java.getMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                        .invoke(null, args, null, null) as java.lang.Process
                    remoteProcess.waitFor()
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
                coreValueText.text = String.format("%.2f", (1..4).random() + Math.random())
                ramValueText.text = "${(30..85).random()}%"
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
