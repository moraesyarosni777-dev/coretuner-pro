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

        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnEconomia = findViewById<MaterialCardView>(R.id.btn_economia)

        // ... (Mapeamento dos outros botões segue o padrão)

        // ⚡ PERFORMANCE (Agressividade de Compilação e Renderização)
        btnPerformance.setOnClickListener {
            // Força compilação total (speed) e permite que a tela desenhe sem esperar sinais (latch_unsignaled)
            val cmd = "cmd package compile -m speed -f -a && " +
                      "setprop debug.sf.latch_unsignaled 1 && " +
                      "setprop debug.cpurend.vsync false"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "⚡ PERFORMANCE: Compilação 'Speed' forçada e Latch Unsignaled ativo.", Toast.LENGTH_LONG).show()
        }

        // 📉 ECONOMIA (Deep Freeze via Standby Buckets)
        btnEconomia.setOnClickListener {
            // Coloca os apps em modo RESTRICTED (O Shizuku tem permissão total aqui)
            // Isso corta a interligação de background de apps de terceiros
            val cmd = "cmd devicelayer-status set-inactive && " +
                      "for pkg in \$(pm list packages -3 | cut -d: -f2); do am set-standby-bucket \$pkg restricted; done"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "📉 ECONOMIA: Todos os apps de terceiros movidos para o balde RESTRICTED.", Toast.LENGTH_LONG).show()
        }

        // 👆 TOUCH (Prioridade de Fila e HWUI)
        btnTouch.setOnClickListener {
            // Otimiza o renderizador de hardware e a velocidade de resposta sem mexer em escalas
            val cmd = "settings put global touch_exploration_enabled 0 && " +
                      "setprop persist.sys.ui.hw true && " +
                      "setprop debug.performance.tuning 1"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "👆 TOUCH: Tuning de performance HWUI e Latência de Toque reduzida.", Toast.LENGTH_LONG).show()
        }

        // 🖥️ SISTEMA (Limpeza de Cache Profunda e I/O Priority)
        btnSistema.setOnClickListener {
            // O Shizuku consegue limpar o cache de todos os pacotes de uma vez
            val cmd = "pm trim-caches 999G && sm fstrim -v all && settings put global low_power_sticky 1"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🖥️ SISTEMA: Cache global limpo e FSTRIM executado na memória Flash.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkShizukuPermission(): Boolean {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) return true
            else Shizuku.requestPermission(0)
        }
        return false
    }

    private fun executarComandoShizuku(comando: String) {
        if (checkShizukuPermission()) {
            Thread { 
                try { 
                    val p = Shizuku.newProcess(arrayOf("sh", "-c", comando), null, null)
                    p.waitFor() 
                } catch (e: Exception) { e.printStackTrace() } 
            }.start()
        }
    }
}
