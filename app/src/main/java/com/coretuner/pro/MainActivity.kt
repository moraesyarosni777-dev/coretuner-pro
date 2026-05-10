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

        // Fundo Profissional Escuro Premium
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#09060E")) // Preto Profundo
        }

        // Título Secundário (Tamanho Menor)
        val title = TextView(this).apply {
            text = "CoreTuner Pro MAX"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#CCFFFFFF")) // Branco suave
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 10)
        }
        root.addView(title)

        // =========================================================================
        // ASSINATURA GIGANTE E NEUMÓRFICA REALISTA (by Yarosni)
        // Esse é o efeito "que dá pra pegar com a mão" (profundidade física)
        // =========================================================================
        val yarosniSignature = TextView(this).apply {
            text = "by Yarosni"
            textSize = 60f // Gigante e proeminente
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            
            // Cor principal do texto (Branco Puro para o "brilho" frontal)
            setTextColor(Color.WHITE)

            // TÉCNICA DE PROFUNDIDADE NEUMÓRFICA: Layered Shadows
            // DX/DY grandes criam a "parede" lateral do objeto 3D.
            // O raio grande suaviza a borda da projeção.
            // Cor Roxo Profundo para a sombra projetada (Extrusão).
            setShadowLayer(
                20f,             // Raio (suavidade da projeção)
                10f,            // DX (deslocamento horizontal da sombra)
                15f,            // DY (deslocamento vertical da sombra)
                Color.parseColor("#4A148C") // Roxo Profundo/Saturado
            )
            
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50) // Espaço para respirar
            elevation = 50f // Elevação máxima da View para realismo no sistema
        }
        // Adiciona a assinatura tangível no topo
        root.addView(yarosniSignature)

        // Subtítulo da Moraes Yarosni (mantido para respeito à full name)
        val fullSignature = TextView(this).apply {
            text = "Developed by Moraes Yarosni"
            textSize = 12f
            typeface = Typeface.DEFAULT
            setTextColor(Color.parseColor("#80FFFFFF")) // 50% transparente
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }
        root.addView(fullSignature)

        // Console de Saída (Visual Clássico de Terminal)
        console = TextView(this).apply {
            text = "[ CORE INSTALADO ]\nMotorola Moto G60\nAguardando injeção de código MAX..."
            setTextColor(Color.parseColor("#00FF66")) // Verde Terminal
            setBackgroundColor(Color.parseColor("#120E1C")) // Roxo Escuro Console
            setPadding(40, 40, 40, 40)
            textSize = 11f
            typeface = Typeface.MONOSPACE
            elevation = 20f
        }
        
        val scrollConsole = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 280) // Console menor para caber tudo
            setPadding(0, 0, 0, 30)
        }
        scrollConsole.addView(console)
        root.addView(scrollConsole)

        // Container para rolar os botões 3D
        val btnContainer = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, -1)
        }
        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
        }

        // Função Criadora de Menus 3D Realísticos/Vidro
        fun addBtn3D(label: String, cmd: String, colorStart: String, colorEnd: String) {
            val b = Button(this).apply {
                text = label
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                setShadowLayer(8f, 3f, 4f, Color.BLACK) // Texto em 3D sutil
                isAllCaps = false
                elevation = 25f // Profundidade extrema do botão

                // Efeito 3D Metal/Vidro: Gradiente e Reflexo
                background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(Color.parseColor(colorStart), Color.parseColor(colorEnd))
                ).apply {
                    cornerRadius = 40f
                    // Stroke branco sutil na borda superior cria o reflexo de luz
                    setStroke(4, Color.parseColor("#66FFFFFF")) 
                }

                // Animação de clique tátil
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

        // Botões Clássicos (Roxo Neumórfico)
        addBtn3D("🎯 Otimizar Touch (Latência)", "setprop debug.sf.latch_unsignaled 1", "#6200EA", "#31007A")
        addBtn3D("🚀 Turbo RAM / LMK", "device_config put activity_manager max_phantom_processes 2147483647", "#6200EA", "#31007A")
        addBtn3D("🧹 Limpar Cache Oculto", "pm trim-caches 32G", "#6200EA", "#31007A")
        
        // Botões Extremos (Cores Vibrantes Neumórficas)
        addBtn3D("⚡ Forçar 120Hz Constante", "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0", "#00C853", "#00600F") // Verde
        addBtn3D("🏎️ Modo Relâmpago (Animações 0.5x)", "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5 && settings put global animator_duration_scale 0.5", "#D50000", "#7F0000") // Vermelho
        addBtn3D("🛠️ Manutenção Profunda ART/Trim", "sm fstrim && cmd package bg-dexopt-job", "#FF6D00", "#AA4000") // Laranja

        btnContainer.addView(btnLayout)
        root.addView(btnContainer)

        setContentView(root)
    }

    private fun rodar(c: String) {
        console.append("\n\n> $c")
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
            console.append("\n[OK] Injeção concluída.")
        } catch (e: Exception) {
            console.append("\n[Erro]: ${e.cause?.message ?: e.message}")
        }
    }
}
