package com.coretuner.pro

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mapeando todos os 6 botões do grid
        val btnAjuste = findViewById<MaterialCardView>(R.id.btn_ajuste)
        val btnLineTime = findViewById<MaterialCardView>(R.id.btn_line_time)
        val btnFingerprint = findViewById<MaterialCardView>(R.id.btn_fingerprint)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnMonitor = findViewById<MaterialCardView>(R.id.btn_monitor)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)
        
        // Cores base (Stealth) e ativas (Neon)
        val baseHex = "#1F1F26" 
        val roxoNeon = "#A044FF" 
        val cianoNeon = "#5BF0FE" 

        // Aplicando a lógica de neon e clique para cada um
        setupVipAction(btnAjuste, baseHex, roxoNeon, "Iniciando Ajuste Fino VIP...")
        setupVipAction(btnLineTime, baseHex, cianoNeon, "Módulo Line Time Ativado")
        setupVipAction(btnFingerprint, baseHex, roxoNeon, "Injetando Fingerprint...")
        setupVipAction(btnPerformance, baseHex, cianoNeon, "Modo Performance MAX")
        setupVipAction(btnMonitor, baseHex, roxoNeon, "Monitor de Hardware Aberto")
        setupVipAction(btnZram, baseHex, cianoNeon, "ZRAM Otimizada")
    }

    private fun setupVipAction(card: MaterialCardView?, baseHex: String, activeHex: String, msg: String) {
        // Trava de segurança: se o botão não existir no XML, ignora e não crasha o app
        if (card == null) return

        val baseCol = Color.parseColor(baseHex)
        val activeCol = Color.parseColor(activeHex)

        card.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    card.strokeColor = activeCol
                    card.strokeWidth = 4 
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    card.strokeColor = baseCol 
                    card.strokeWidth = 2
                    if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                }
            }
            true
        }

        card.setOnClickListener {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
