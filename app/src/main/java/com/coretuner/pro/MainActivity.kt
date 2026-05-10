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

        // Fundo Profissional Escuro
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 60, 50, 50)
            setBackgroundColor(Color.parseColor("#0B0812"))
        }

        // Título 3D Premium
        val title = TextView(this).apply {
            text = "CoreTuner Pro"
            textSize = 34f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            // Efeito 3D Roxo nas letras
            setShadowLayer(10f, 5f, 5f, Color.parseColor("#6200EA"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // Assinatura 3D
        val signature = TextView(this).apply {
            text = "Developed by Moraes Yarosni"
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            setTextColor(Color.WHITE)
            setShadowLayer(8f, 4f, 4f, Color.parseColor("#8A2BE2"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 50)
        }
        root.addView(signature)

        // Console de Saída
        console = TextView(this).apply {
            text = "[ SISTEMA PRONTO ]\nAguardando comandos..."
            setTextColor(Color.parseColor("#00FF66"))
            setBackgroundColor(Color.parseColor("#120E1C"))
            setPadding(40, 40, 40, 40)
            textSize = 12f
            typeface = Typeface.MONOSPACE
            elevation = 10f
        }
        
        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 350)
            setPadding(0, 0, 0, 40)
        }
        scroll.addView(console)
        root.addView(scroll)

        // Função Criadora de Menus 3D Profissionais
        fun addBtn(label: String, cmd: String) {
            val b = Button(this).apply {
                text = label
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                setShadowLayer(5f, 2f, 2f, Color.BLACK) // Texto em 3D
                isAllCaps = false
                elevation = 15f // Profundidade 3D do botão

                // Gradiente Roxo e Bordas Arredondadas
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 30f
                    colors = intArrayOf(Color.parseColor("#6200EA"), Color.parseColor("#3700B3"))
                }

                // Efeito ao clicar
                setOnClickListener {
                    animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                        animate().scaleX(1f).scaleY(1f).setDuration(100)
                        rodar(cmd)
                    }
                }
            }
            val p = LinearLayout.LayoutParams(-1, 140)
            p.setMargins(0, 0, 0, 35)
            root.addView(b, p)
        }

        addBtn("⚡ Otimizar Touch", "setprop debug.sf.latch_unsignaled 1")
        addBtn("🚀 Turbo RAM", "device_config put activity_manager max_phantom_processes 2147483647")
        addBtn("🧹 Limpar Cache", "pm trim-caches 32G")

        setContentView(root)
    }

    private fun rodar(c: String) {
        console.append("\n\n> $c")
        try {
            // Correção da assinatura do método para o Shizuku
            val method = Shizuku::class.java.getDeclaredMethod(
                "newProcess", 
                Array<String>::class.java, 
                Array<String>::class.java, 
                String::class.java
            )
            method.isAccessible = true
            val p = method.invoke(null, arrayOf("sh", "-c", c), null, null) as Process
            p.waitFor()
            console.append("\n[OK] Sucesso.")
        } catch (e: Exception) {
            console.append("\n[Erro]: ${e.cause?.message ?: e.message}")
        }
    }
}
