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

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#09060E"))
        }

        val title = TextView(this).apply {
            text = "CoreTuner Pro MAX"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#80FFFFFF"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 5)
        }
        root.addView(title)

        val yarosniSignature = TextView(this).apply {
            text = "by Yarosni"
            textSize = 42f 
            typeface = Typeface.create("cursive", Typeface.BOLD) 
            setTextColor(Color.parseColor("#F5F5F5")) 
            setShadowLayer(8f, 4f, 6f, Color.parseColor("#AA000000"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }
        root.addView(yarosniSignature)

        val fullSignature = TextView(this).apply {
            text = "Developed by Moraes Yarosni"
            textSize = 11f
            typeface = Typeface.DEFAULT
            setTextColor(Color.parseColor("#4DFFFFFF"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        }
        root.addView(fullSignature)

        console = TextView(this).apply {
            text = "[ CORE MAX ]\nMotorola Moto G60\nInterface fluida ativada."
            setTextColor(Color.parseColor("#00FF66"))
            setBackgroundColor(Color.parseColor("#120E1C"))
            setPadding(40, 40, 40, 40)
            textSize = 11f
            typeface = Typeface.MONOSPACE
            elevation = 15f
        }
        
        val scrollConsole = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 260)
            setPadding(0, 0, 0, 30)
        }
        scrollConsole.addView(console)
        root.addView(scrollConsole)

        val btnContainer = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, -1)
        }
        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
        }

        fun addBtn3D(label: String, cmd: String, colorStart: String, colorEnd: String) {
            val b = Button(this).apply {
                text = label
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                setShadowLayer(5f, 2f, 3f, Color.BLACK)
                isAllCaps = false
                elevation = 20f

                background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(Color.parseColor(colorStart), Color.parseColor(colorEnd))
                ).apply {
                    cornerRadius = 40f
                    setStroke(3, Color.parseColor("#4DFFFFFF")) 
                }

                setOnClickListener {
                    animate().scaleX(0.92f).scaleY(0.92f).setDuration(80).withEndAction {
                        animate().scaleX(1f).scaleY(1f).setDuration(80)
                        rodar(cmd)
                    }
                }
            }
            val p = LinearLayout.LayoutParams(-1, 150)
            p.setMargins(0, 0, 0, 40)
            btnLayout.addView(b, p)
        }

        addBtn3D("🎯 Otimizar Touch (Latência)", "setprop debug.sf.latch_unsignaled 1", "#6200EA", "#31007A")
        addBtn3D("🚀 Turbo RAM / LMK", "device_config put activity_manager max_phantom_processes 2147483647", "#6200EA", "#31007A")
        addBtn3D("🧹 Limpar Cache Oculto", "pm trim-caches 32G", "#6200EA", "#31007A")
        addBtn3D("⚡ Forçar 120Hz Constante", "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0", "#00C853", "#00600F")
        addBtn3D("🏎️ Modo Relâmpago (Animações)", "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5 && settings put global animator_duration_scale 0.5", "#D50000", "#7F0000")
        
        // Aqui mantemos o modo background, não trocamos pro speed pra não fritar o celular.
        addBtn3D("🛠️ Manutenção Profunda ART/Trim", "sm fstrim && cmd package bg-dexopt-job", "#FF6D00", "#AA4000")

        btnContainer.addView(btnLayout)
        root.addView(btnContainer)

        setContentView(root)
    }

    private fun rodar(c: String) {
        // Escreve na tela imediatamente
        console.append("\n\n> $c\n[Aguarde... processando em background]")
        
        // Abre uma Thread paralela para não travar o aplicativo
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
                p.waitFor() // Trava apenas a thread paralela, não a tela
                
                // Volta para a tela principal para dar o OK
                runOnUiThread {
                    console.append("\n[OK] Injeção concluída.")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    console.append("\n[Erro]: ${e.cause?.message ?: e.message}")
                }
            }
        }.start()
    }
}
