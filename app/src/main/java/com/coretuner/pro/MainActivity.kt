package com.coretuner.pro // Lembre de conferir se este pacote bate com o seu

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Carrega dados fake na telemetria
        findViewById<TextView>(R.id.txt_data_val).text = "1.30"
        findViewById<TextView>(R.id.txt_atu_val).text = "53.57 MHz"
        findViewById<TextView>(R.id.txt_upi_val).text = "25.03"
        findViewById<TextView>(R.id.txt_bpg_val).text = "38.05 MHs"

        configureVipModules()
    }

    private fun configureVipModules() {
        val btnAjuste = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnFinger = findViewById<MaterialCardView>(R.id.btn_fingerprint)
        val btnMonitor = findViewById<MaterialCardView>(R.id.btn_monitor)
        
        val btnLine = findViewById<MaterialCardView>(R.id.btn_line_time)
        val btnPerf = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        val colorPurpleBase = Color.parseColor("#8A2BE2")
        val colorPurpleActive = Color.parseColor("#C084FC")
        val colorCyanBase = Color.parseColor("#22D3EE")
        val colorCyanActive = Color.parseColor("#FFFFFF")

        // Botões Roxos
        setupButtonAction(btnAjuste, colorPurpleBase, colorPurpleActive, "Módulo: Ajuste Fino iniciado!")
        setupButtonAction(btnFinger, colorPurpleBase, colorPurpleActive, "Módulo: Fingerprint iniciado!")
        setupButtonAction(btnMonitor, colorPurpleBase, colorPurpleActive, "Módulo: Monitor iniciado!")

        // Botões Ciano
        setupButtonAction(btnLine, colorCyanBase, colorCyanActive, "Módulo: Line Time iniciado!")
        setupButtonAction(btnPerf, colorCyanBase, colorCyanActive, "Módulo: Performance iniciado!")
        setupButtonAction(btnZram, colorCyanBase, colorCyanActive, "Módulo: ZRAM iniciado!")
    }

    private fun setupButtonAction(button: MaterialCardView, baseColor: Int, highlightColor: Int, message: String) {
        button.strokeColor = baseColor
        
        // Efeito visual de neon
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> button.strokeColor = highlightColor
                MotionEvent.ACTION_UP -> {
                    button.strokeColor = baseColor
                    v.performClick() // Garante que o clique seja disparado
                }
                MotionEvent.ACTION_CANCEL -> button.strokeColor = baseColor
            }
            true // Consome o evento de toque
        }

        // Ação real que vai funcionar na tela
        button.setOnClickListener {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            // AQUI VOCÊ COLOCARÁ SEU CÓDIGO SHELL/SHIZUKU DEPOIS
        }
    }
}
