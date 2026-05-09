package com.coretuner.pro

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.view.Gravity
import rikka.shizuku.Shizuku

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fundo ultra dark texturizado
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0B12")) 
            setPadding(60, 100, 60, 60)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Título verde neon
        val title = TextView(this).apply {
            text = "CORETUNER PRO"
            setTextColor(Color.parseColor("#00FF66")) 
            textSize = 28f
            setPadding(0, 0, 0, 80)
        }
        layout.addView(title)

        // Função para criar os botões táticos
        fun addBtn(name: String, color: String, cmd: String) {
            val btn = Button(this).apply {
                text = name
                setTextColor(Color.parseColor(color))
                setBackgroundColor(Color.parseColor("#161622"))
                setPadding(0, 50, 0, 50)
                setOnClickListener { executar(cmd) }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 40)
            layout.addView(btn, params)
        }

        // Os botões injetando os comandos do XDA
        addBtn("Otimizar Touch (Latência)", "#00FF66", "setprop debug.sf.latch_unsignaled 1 && setprop debug.hwui.target_cpu_time_percent 100 && setprop debug.cpurend.vsync false")
        addBtn("Turbo RAM / LMK", "#A020F0", "device_config put activity_manager max_phantom_processes 2147483647 && device_config put activity_manager max_cached_processes 32 && settings put global cached_apps_freezer enabled")
        addBtn("I/O & Storage Boost", "#00FF66", "sm fstrim && pm trim-caches 32G")

        setContentView(layout)
        
        // Pede permissão pro Shizuku assim que abrir
        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }
    }

    // O motor que roda os códigos silenciosamente
    private fun executar(comando: String) {
        if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            try {
                val processo = Shizuku.newProcess(arrayOf("sh", "-c", comando), null, null)
                processo.waitFor()
                Toast.makeText(this, "Tuning aplicado na raiz!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Erro de permissão no Shell.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permita o app no Shizuku primeiro.", Toast.LENGTH_LONG).show()
            Shizuku.requestPermission(0)
        }
    }
}
