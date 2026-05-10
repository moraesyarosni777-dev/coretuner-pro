package com.coretuner.pro

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import rikka.shizuku.Shizuku

class MainActivity : Activity() {

    private lateinit var console: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fundo Escuro Premium (Glassmorphism Base)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#0A0A0A"))
        }

        root.addView(TextView(this).apply {
            text = "CoreTuner Pro MAX"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })

        // Assinatura by Yarosni (Neon Green Realista)
        root.addView(TextView(this).apply {
            text = "by Yarosni"
            textSize = 46f
            typeface = Typeface.create("cursive", Typeface.BOLD)
            setTextColor(Color.parseColor("#00FF00"))
            setShadowLayer(18f, 0f, 0f, Color.parseColor("#00FF00"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
        })

        root.addView(TextView(this).apply {
            text = "Hardcore tuning, brutal performance."
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#AAAAAA"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 5)
        })
        root.addView(TextView(this).apply {
            text = "Developed by Moraes Yarosni"
            textSize = 10f
            setTextColor(Color.parseColor("#777777"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        })

        // Painel Terminal Embutido (Borda Verde)
        val terminalBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1A1A1A"))
                setStroke(3, Color.parseColor("#00FF00"))
                cornerRadius = 20f
            }
        }
        
        terminalBox.addView(TextView(this).apply {
            text = "[ CORE INSTALADO ]\nMotorola Moto G60"
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#00FF00"))
            gravity = Gravity.CENTER
        })

        console = TextView(this).apply {
            text = "Aguardando injeção de código MAX..."
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#00FF00"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
        }
        terminalBox.addView(console)
        root.addView(terminalBox)

        val scrollBtn = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, -1)
            setPadding(0, 40, 0, 0)
        }
        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Motor de Botões Néon 3D Programático
        fun addNeonBtn(label: String, colorHex: String, cmd: String, loadingMsg: String) {
            val b = Button(this).apply {
                text = label
                isAllCaps = false
                textSize = 14f
                setTextColor(Color.parseColor(colorHex))
                typeface = Typeface.DEFAULT_BOLD
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#00000000")) // Fundo translúcido
                    setStroke(4, Color.parseColor(colorHex)) // Borda Néon
                    cornerRadius = 25f
                }
                setOnClickListener {
                    animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).withEndAction {
                        animate().scaleX(1f).scaleY(1f).setDuration(50)
                        rodar(cmd, loadingMsg)
                    }
                }
            }
            val params = LinearLayout.LayoutParams(-1, 140)
            params.setMargins(0, 0, 0, 30)
            btnLayout.addView(b, params)
        }

        // Comandos Extremos Reais Integrados
        addNeonBtn("🎯 Otimizar Touch (Latência)", "#00FF00", "setprop debug.sf.latch_unsignaled 1", "Otimizando touch...")
        addNeonBtn("🚀 Turbo RAM / LMK", "#BF00FF", "device_config put activity_manager max_phantom_processes 2147483647", "Injetando Turbo RAM...")
        addNeonBtn("🧹 Limpar Cache Oculto", "#00FF00", "pm trim-caches 32G", "Limpando caches...")
        addNeonBtn("⚡ Forçar 120Hz Real", "#00FF00", "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0", "Cravando 120Hz...")
        addNeonBtn("🏎️ Modo Relâmpago (Animações)", "#FF0000", "settings put global window_animation_scale 0.25 && settings put global transition_animation_scale 0.25 && settings put global animator_duration_scale 0.25", "Acelerando animações para 0.25x...")
        addNeonBtn("🛠️ Manutenção Profunda (ART/Trim)", "#FFA500", "sm fstrim && cmd package bg-dexopt-job", "Executando faxina profunda...")
        addNeonBtn("🚨 Injeção Extrema -m speed", "#FF0000", "pm compile -a -f -m speed", "Aviso: Compilação speed iniciada!")

        scrollBtn.addView(btnLayout)
        root.addView(scrollBtn)

        setContentView(root)
    }

    private fun rodar(c: String, msg: String) {
        console.text = msg
        console.setTextColor(Color.WHITE)
        
        Thread {
            try {
                val method = Shizuku::class.java.getDeclaredMethod(
                    "newProcess", 
                    Array<String>::class.java, 
                    Array<String>::class.java, 
                    String::class.java
                )
                method.isAccessible = true
                val p = method.invoke(null, arrayOf("sh", "-c", c), null, null) as Process
                p.waitFor() // Processa em background sem travar a interface
                
                runOnUiThread {
                    console.text = "Injeção 'by Yarosni' validada."
                    console.setTextColor(Color.parseColor("#00FF00"))
                }
            } catch (e: Exception) {
                runOnUiThread {
                    console.text = "Erro Shizuku: Permissão Negada"
                    console.setTextColor(Color.RED)
                }
            }
        }.start()
    }
}
