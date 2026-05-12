package com.coretuner.pro

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    // DECLARAÇÃO GLOBAL
    private lateinit var txt_shizuku: TextView
    private lateinit var txt_cpu_speed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PONTE COM O XML
        txt_shizuku = findViewById(R.id.txt_shizuku)
        txt_cpu_speed = findViewById(R.id.txt_cpu_speed)

        // MAPEAMENTO DOS BOTÕES
        val btnAjusteFino = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnEconomia = findViewById<MaterialCardView>(R.id.btn_economia)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnBateria = findViewById<MaterialCardView>(R.id.btn_bateria)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnFps = findViewById<MaterialCardView>(R.id.btn_fps)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        // SHIZUKU VERIFICAÇÃO INICIAL
        if (checkShizukuPermission()) {
            txt_shizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
            txt_shizuku.setTextColor(android.graphics.Color.parseColor("#00E676"))
        } else {
            txt_shizuku.text = "SHIZUKU NÃO VINCULADO"
            txt_shizuku.setTextColor(android.graphics.Color.parseColor("#FF1744"))
        }

        // =========================================================
        // LÓGICA DE CLIQUES E TOASTS
        // =========================================================

        btnAjusteFino.setOnClickListener {
            Toast.makeText(this, "⚙️ Ajuste Fino Aplicado!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnEconomia.setOnClickListener {
            Toast.makeText(this, "🔋 Modo Economia Ativado!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnPerformance.setOnClickListener {
            Toast.makeText(this, "⚡ Performance Máxima!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnBateria.setOnClickListener {
            Toast.makeText(this, "🛡️ Bateria Otimizada!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnTouch.setOnClickListener {
            Toast.makeText(this, "👆 Touch Calibrado!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnFps.setOnClickListener {
            Toast.makeText(this, "🎮 FPS Max Destravado!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnSistema.setOnClickListener {
            Toast.makeText(this, "🖥️ Sistema Otimizado!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }

        btnZram.setOnClickListener {
            Toast.makeText(this, "🧠 ZRAM Expandida!", Toast.LENGTH_SHORT).show()
            // Injetar comando Shizuku aqui depois
        }
    }

    private fun checkShizukuPermission(): Boolean {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return true
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                return false
            } else {
                Shizuku.requestPermission(0)
                return false
            }
        }
        return false
    }
}
