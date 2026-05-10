package com.coretuner.pro

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import rikka.shizuku.Shizuku

class MainActivity : Activity() {

    private lateinit var console: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Fundo Profundo (Gradiente #050505 a #121212)
        val bgGradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor("#050505"), Color.parseColor("#121212"))
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 70, 40, 40)
            background = bgGradient
        }

        // Títulos (Tipografia Moderna, Sans-Serif, Branco Puro)
        root.addView(TextView(this).apply {
            text = "CoreTuner Pro MAX"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            letterSpacing = 0.1f
        })

        root.addView(TextView(this).apply {
            text = "by Yarosni"
            textSize = 42f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
        })

        // Textos de Suporte (Verde Glacial / Menta Premium)
        val glacialGreen = Color.parseColor("#A7FFEB")

        root.addView(TextView(this).apply {
            text = "HARDCORE TUNING • BRUTAL PERFORMANCE"
            textSize = 10f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setTextColor(glacialGreen)
            gravity = Gravity.CENTER
            letterSpacing = 0.15f
            setPadding(0, 5, 0, 40)
        })

        // 2. Terminal - Efeito Vidro Lapidado
        val glassTerminalBg = GradientDrawable().apply {
            setColor(Color.parseColor("#08FFFFFF")) // rgba(255, 255, 255, 0.03)
            setStroke(2, Color.parseColor("#26A7FFEB")) // rgba(167, 255, 235, 0.15)
            cornerRadius = 35f
        }

        val terminalBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            background = glassTerminalBg
        }
        
        terminalBox.addView(TextView(this).apply {
            text = "[ STATUS DO SISTEMA ]"
            textSize = 12f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setTextColor(glacialGreen)
            gravity = Gravity.CENTER
            letterSpacing = 0.1f
        })

        console = TextView(this).apply {
            text = "Aguardando injeção de código MAX..."
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 0)
        }
        terminalBox.addView(console)
        root.addView(terminalBox)

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(-1, -1) }
        val btnLayout = LinearLayout(this).apply { 
            orientation = LinearLayout.VERTICAL
            setPadding(0, 50, 0, 0)
        }

        // 3. Injeção de Botões Dark Glassmorphism Premium
        btnLayout.addView(PremiumGlassButton(this, "Otimizar Touch (Latência)", "setprop debug.sf.latch_unsignaled 1"))
        btnLayout.addView(PremiumGlassButton(this, "Turbo RAM / LMK", "device_config put activity_manager max_phantom_processes 2147483647"))
        btnLayout.addView(PremiumGlassButton(this, "Limpar Cache Oculto", "pm trim-caches 32G"))
        btnLayout.addView(PremiumGlassButton(this, "Forçar 120Hz Real", "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0"))
        btnLayout.addView(PremiumGlassButton(this, "Modo Relâmpago (Animações)", "settings put global window_animation_scale 0.25 && settings put global transition_animation_scale 0.25"))
        btnLayout.addView(PremiumGlassButton(this, "Manutenção Profunda ART", "sm fstrim && cmd package bg-dexopt-job"))
        btnLayout.addView(PremiumGlassButton(this, "Injeção Extrema -m speed", "pm compile -a -f -m speed"))

        scroll.addView(btnLayout)
        root.addView(scroll)
        setContentView(root)
    }

    private fun rodar(c: String) {
        console.text = "Processando..."
        console.setTextColor(Color.WHITE)
        Thread {
            try {
                val method = Shizuku::class.java.getDeclaredMethod(
                    "newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java
                )
                method.isAccessible = true
                val p = method.invoke(null, arrayOf("sh", "-c", c), null, null) as Process
                p.waitFor()
                runOnUiThread { 
                    console.text = "Injeção concluída com sucesso."
                    console.setTextColor(Color.parseColor("#A7FFEB"))
                }
            } catch (e: Exception) {
                runOnUiThread { 
                    console.text = "Erro: Sem permissão do Shizuku."
                    console.setTextColor(Color.parseColor("#FF5252"))
                }
            }
        }.start()
    }

    // ========================================================
    // MOTOR DE RENDERIZAÇÃO: DARK GLASSMORPHISM PREMIUM
    // ========================================================
    inner class PremiumGlassButton(context: Context, private val btnText: String, private val cmd: String) : View(context) {
        
        private var isPressedState = false

        // Fundo translucido 3% (Blur simulado)
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#08FFFFFF") 
        }

        // Borda fina Vidro Lapidado 15% opacidade verde glacial
        private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.parseColor("#26A7FFEB")
        }

        // Tipografia
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 38f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.04f // Equivalente a 1.2px
        }

        // Inner Glow Suave (Substituindo sombra pesada)
        private val innerGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            val params = LinearLayout.LayoutParams(-1, 150)
            params.setMargins(0, 0, 0, 35)
            layoutParams = params
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            // Brilho holográfico sutil de cima para baixo
            innerGlowPaint.shader = LinearGradient(
                0f, 0f, 0f, h.toFloat() / 2,
                Color.parseColor("#15A7FFEB"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }

        override fun onDraw(canvas: Canvas) {
            val rect = RectF(5f, 5f, width - 5f, height - 5f)
            val corner = 25f

            if (isPressedState) {
                canvas.scale(0.97f, 0.97f, width / 2f, height / 2f)
                fillPaint.color = Color.parseColor("#15FFFFFF") // Fica mais claro ao toque
            } else {
                fillPaint.color = Color.parseColor("#08FFFFFF")
            }

            // Sem drop shadow pesada, apenas o fill, inner glow e a borda cirúrgica
            canvas.drawRoundRect(rect, corner, corner, fillPaint)
            canvas.drawRoundRect(rect, corner, corner, innerGlowPaint)
            canvas.drawRoundRect(rect, corner, corner, borderPaint)

            val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas.drawText(btnText, width / 2f, textY, textPaint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isPressedState = true
                    invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isPressedState = false
                    invalidate()
                    if (event.action == MotionEvent.ACTION_UP) {
                        rodar(cmd)
                    }
                }
            }
            return true
        }
    }
}
