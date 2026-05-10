package com.coretuner.pro

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import rikka.shizuku.Shizuku

class MainActivity : Activity() {

    private lateinit var console: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Fundo Escuro Base
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#050505"))
        }

        root.addView(TextView(this).apply {
            text = "CoreTuner Pro MAX"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.SERIF
        })

        // 2. Assinatura Neon 3D Realista
        root.addView(TextView(this).apply {
            text = "by Yarosni"
            textSize = 48f
            typeface = Typeface.create("cursive", Typeface.BOLD)
            setTextColor(Color.parseColor("#00FF00"))
            setShadowLayer(25f, 0f, 0f, Color.parseColor("#00FF00")) // Brilho intenso
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 0)
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
            setPadding(0, 0, 0, 35)
        })

        // 3. Efeito Vidro Convexo Programático (Painel Central)
        val glassBackground = GradientDrawable().apply {
            colors = intArrayOf(Color.parseColor("#4D202020"), Color.parseColor("#1A101010"))
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = 600f
            setStroke(3, Color.parseColor("#00FF00"))
            cornerRadius = 30f
        }

        val terminalBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = glassBackground
            elevation = 15f // Profundidade 3D
        }
        
        terminalBox.addView(TextView(this).apply {
            text = "[ CORE INSTALADO ]\nMotorola Moto G60"
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#00FF00"))
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
        })

        console = TextView(this).apply {
            text = "Aguardando injeção de código MAX..."
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#00FF00"))
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 0)
        }
        terminalBox.addView(console)
        root.addView(terminalBox)

        val scrollBtn = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, -1)
            setPadding(0, 50, 0, 0)
        }
        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // 4. Construtor de Botões de Vidro (Glass Buttons)
        fun addGlassBtn(label: String, colorHex: String, cmd: String, loadingMsg: String) {
            val btnGlassBg = GradientDrawable().apply {
                setColor(Color.parseColor("#26121212")) // Acrílico super escuro e translúcido
                setStroke(4, Color.parseColor(colorHex)) // Borda Neon
                cornerRadius = 25f
            }

            val b = Button(this).apply {
                text = label
                isAllCaps = false
                textSize = 14f
                setTextColor(Color.parseColor(colorHex))
                typeface = Typeface.DEFAULT_BOLD
                background = btnGlassBg
                elevation = 8f // Botões saltando da tela
                stateListAnimator = null // Remove sombra padrão feia do Android
                
                setOnClickListener {
                    // Animação tátil 3D de afundar o vidro
                    animate().scaleX(0.92f).scaleY(0.92f).setDuration(60).withEndAction {
                        animate().scaleX(1f).scaleY(1f).setDuration(60)
                        rodar(cmd, loadingMsg)
                    }
                }
            }
            val params = LinearLayout.LayoutParams(-1, 150)
            params.setMargins(0, 0, 0, 35)
            btnLayout.addView(b, params)
        }

        // 5. Injeção de Comandos
        addGlassBtn("🎯 Otimizar Touch (Latência)", "#00FF00", "setprop debug.sf.latch_unsignaled 1", "Otimizando touch...")
        addGlassBtn("🚀 Turbo RAM / LMK", "#BF00FF", "device_config put activity_manager max_phantom_processes 2147483647", "Injetando Turbo RAM...")
        addGlassBtn("🧹 Limpar Cache Oculto", "#00FF00", "pm trim-caches 32G", "Limpando caches...")
        addGlassBtn("⚡ Forçar 120Hz Real", "#00FF00", "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0", "Cravando 120Hz...")
        addGlassBtn("🏎️ Modo Relâmpago (Animações)", "#FF0000", "settings put global window_animation_scale 0.25 && settings put global transition_animation_scale 0.25 && settings put global animator_duration_scale 0.25", "Acelerando animações para 0.25x...")
        addGlassBtn("🛠️ Manutenção Profunda (ART/Trim)", "#FFA500", "sm fstrim && cmd package bg-dexopt-job", "Executando faxina profunda...")
        addGlassBtn("🚨 Injeção Extrema -m speed", "#FF0000", "pm compile -a -f -m speed", "Aviso: Compilação speed iniciada!")

        scrollBtn.addView(btnLayout)
        root.addView(scrollBtn)

        setContentView(root)
    }

    // 6. Motor Shizuku
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
                p.waitFor() 
                
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
