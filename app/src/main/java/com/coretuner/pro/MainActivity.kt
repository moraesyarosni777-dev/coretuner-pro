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

        configureNeonButtons()
    }

    private fun configureNeonButtons() {
        val btnAjuste = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnEco = findViewById<MaterialCardView>(R.id.btn_economia)
        val btnPerf = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnBat = findViewById<MaterialCardView>(R.id.btn_bateria)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnFps = findViewById<MaterialCardView>(R.id.btn_fps)

        // Cores do Neon baseado no seu print
        val baseDarkStroke = Color.parseColor("#251F30") 
        val cyanNeonStroke = Color.parseColor("#67E8F9")
        
        // O Ajuste fino já começa aceso no roxo no XML, mas os outros acendem em Ciano
        setupButtonAction(btnEco, baseDarkStroke, cyanNeonStroke, "Economia Ativada")
        setupButtonAction(btnPerf, baseDarkStroke, cyanNeonStroke, "Performance Ativada")
        setupButtonAction(btnBat, baseDarkStroke, cyanNeonStroke, "Bateria Otimizada")
        setupButtonAction(btnTouch, baseDarkStroke, cyanNeonStroke, "Touch Calibrado")
        setupButtonAction(btnFps, baseDarkStroke, cyanNeonStroke, "FPS Max Injetado")
        
        // Mantendo o clique no Ajuste Fino
        btnAjuste.setOnClickListener {
            Toast.makeText(this, "Ajuste Fino Configurado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonAction(button: MaterialCardView, baseColor: Int, highlightColor: Int, message: String) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    button.strokeColor = highlightColor
                    button.strokeWidth = 4 // Engrossa a borda pra dar o "Glow" do neon
                }
                MotionEvent.ACTION_UP -> {
                    button.strokeColor = baseColor
                    button.strokeWidth = 2
                    v.performClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    button.strokeColor = baseColor
                    button.strokeWidth = 2
                }
            }
            true 
        }

        button.setOnClickListener {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
