package com.coretuner.pro

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
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
            setPadding(50, 60, 50, 50)
            setBackgroundColor(Color.parseColor("#0F111A"))
        }

        val title = TextView(this).apply {
            text = "CoreTuner Pro"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        root.addView(title)

        val signature = TextView(this).apply {
            text = "Developed by Moraes Yarosni"
            textSize = 12f
            setTextColor(Color.parseColor("#4DFFFFFF"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50)
        }
        root.addView(signature)

        console = TextView(this).apply {
            text = "Console pronto..."
            setTextColor(Color.parseColor("#00FF66"))
            setBackgroundColor(Color.BLACK)
            setPadding(30, 30, 30, 30)
            textSize = 13f
            typeface = Typeface.MONOSPACE
        }
        
        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(-1, 500)
        }
        scroll.addView(console)
        root.addView(scroll)

        fun addBtn(label: String, cmd: String) {
            val b = Button(this).apply {
                text = label
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#161622"))
                setOnClickListener { rodar(cmd) }
            }
            val p = LinearLayout.LayoutParams(-1, -2)
            p.setMargins(0, 30, 0, 0)
            root.addView(b, p)
        }

        addBtn("Otimizar Touch", "setprop debug.sf.latch_unsignaled 1")
        addBtn("Turbo RAM", "device_config put activity_manager max_phantom_processes 2147483647")
        addBtn("Limpar Cache", "pm trim-caches 32G")

        setContentView(root)
    }

    private fun rodar(c: String) {
        console.append("\n> $c")
        try {
            val method = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, String::class.java, Int::class.javaPrimitiveType)
            method.isAccessible = true
            val p = method.invoke(null, arrayOf("sh", "-c", c), null, 0) as Process
            p.waitFor()
            console.append("\n[OK]")
        } catch (e: Exception) {
            console.append("\n[Erro]: ${e.message}")
        }
    }
}
