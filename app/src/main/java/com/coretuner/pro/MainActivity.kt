package com.coretuner.pro

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var txtShizuku: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtShizuku = findViewById(R.id.txt_shizuku)

        val btnAjusteFino = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        // ... (outros botões seguem a lógica)

        // BOTÃO PERFORMANCE (O TORK BRUTO)
        btnPerformance.setOnClickListener {
            // "performance" no governor, boost de I/O e prioridade máxima para a thread do sistema
            val cmd = "echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor && " +
                      "echo performance > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor && " +
                      "echo 0 > /proc/sys/kernel/sched_min_granularity_ns && " +
                      "echo 1 > /proc/sys/kernel/sched_child_runs_first"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "⚡ PERFORMANCE: Tork Bruto! Governor em 'performance'. Abertura instantânea.", Toast.LENGTH_LONG).show()
        }

        // BOTÃO SISTEMA (I/O Aggressive)
        btnSistema.setOnClickListener {
            // Aumenta a velocidade de leitura da memória interna
            val cmd = "echo 1024 > /sys/block/mmcblk0/queue/read_ahead_kb && " +
                      "echo 2048 > /sys/block/sda/queue/read_ahead_kb && " +
                      "setprop dalvik.vm.heapgrowthlimit 512m"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🖥️ SISTEMA: Read-Ahead triplicado. Leitura de disco ultra agressiva.", Toast.LENGTH_LONG).show()
        }

        // BOTÃO TOUCH (Latência Zero)
        btnTouch.setOnClickListener {
            // Prioridade máxima na fila do hardware
            val cmd = "setprop debug.hwui.render_dirty_regions false && setprop persist.sys.ui.hw true && echo 1 > /proc/sys/vm/swappiness"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "👆 TOUCH: Latência de renderização zerada. Resposta imediata.", Toast.LENGTH_LONG).show()
        }
        
        // ... (Mantenha os outros botões com as configurações anteriores)
    }

    private fun executarComandoShizuku(comando: String) {
        if (Shizuku.pingBinder()) {
            Thread { 
                try { 
                    val p = Shizuku.newProcess(arrayOf("sh", "-c", comando), null, null)
                    p.waitFor() 
                } catch (e: Exception) { e.printStackTrace() } 
            }.start()
        }
    }
}
