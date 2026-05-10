package com.coretuner.pro

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import rikka.shizuku.Shizuku

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            backgroundColor = Color.parseColor("#0F111A")
        }

        val title = TextView(this).apply {
            text = "CoreTuner Pro"
            textSize = 24f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 50)
        }
        layout.addView(title)

        // Função para criar os botões
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

        // Botões com comandos
        addBtn("Otimizar Touch (Latência)", "#00FF66", "setprop debug.sf.latch_unsignaled 1 && setprop debug.hwui.fps_divisor 1")
        addBtn("Turbo RAM / LMK", "#A020F0", "device_config put activity_manager max_phantom_processes 2147483647")
        addBtn("I/O & Storage Boost", "#00FF66", "sm fstrim && pm trim-caches 32G")

        setContentView(layout)

        // Pede permissão ao abrir
        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }
    }

    // Função que roda os códigos via Shizuku (Modo Reflexão para evitar erro de acesso)
    private fun executar(comando: String) {
        if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            try {
                val metodo = Shizuku::class.java.getDeclaredMethod(
                    "newProcess", 
                    Array<String>::class.java, 
                    String::class.java, 
                    Int::class.javaPrimitiveType
                )
                metodo.isAccessible = true
                val processo = metodo.invoke(null, arrayOf("sh", "-c", comando), null, 0) as Process
                processo.waitFor()
                Toast.makeText(this, "Tuning aplicado!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Erro no Shell: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permita o app no Shizuku!", Toast.LENGTH_LONG).show()
            Shizuku.requestPermission(0)
        }
    }
}
