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

        // Configuração do botão de Ajuste Fino como exemplo
        val btnAjuste = findViewById<MaterialCardView>(R.id.btn_ajuste)
        
        setupVipAction(btnAjuste, "#8A2BE2", "#C084FC", "Iniciando Ajuste Fino VIP...")
    }

    private fun setupVipAction(card: MaterialCardView, baseHex: String, activeHex: String, msg: String) {
        val baseCol = Color.parseColor(baseHex)
        val activeCol = Color.parseColor(activeHex)

        card.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    card.strokeColor = activeCol
                    card.strokeWidth = 4 // Efeito de brilho neon no toque
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    card.strokeColor = Color.parseColor("#1F1F26") // Volta pro stealth
                    card.strokeWidth = 2
                    if (event.action == MotionEvent.ACTION_UP) v.performClick()
                }
            }
            true
        }

        card.setOnClickListener {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
