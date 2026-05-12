package com.coretuner.pro

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var txtShizuku: TextView
    private lateinit var txtCpuSpeed: TextView // Monitor de Status
    
    // MEMÓRIA DOS BOTÕES
    private var isAjusteFinoActive = false
    private var isEconomiaActive = false
    private var isPerformanceActive = false
    private var isBateriaActive = false
    private var isTouchActive = false
    private var isFpsActive = false
    private var isSistemaActive = false
    private var isZramActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtShizuku = findViewById(R.id.txt_shizuku)
        txtCpuSpeed = findViewById(R.id.txt_cpu_speed)

        val btnAjusteFino = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnEconomia = findViewById<MaterialCardView>(R.id.btn_economia)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnBateria = findViewById<MaterialCardView>(R.id.btn_bateria)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnFps = findViewById<MaterialCardView>(R.id.btn_fps)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        if (checkShizukuPermission()) {
            txtShizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
            txtShizuku.setTextColor(Color.parseColor("#00E676"))
        }

        // Função interna para atualizar as luzes dos botões e o texto
        fun atualizarPainel() {
            val ativos = mutableListOf<String>()
            
            // Checa cada botão e acende/apaga o fundo (4D = brilhante, 0A = escuro)
            if (isAjusteFinoActive) { ativos.add("Ajuste Fino"); btnAjusteFino.setCardBackgroundColor(Color.parseColor("#4D00E5FF")) } 
            else { btnAjusteFino.setCardBackgroundColor(Color.parseColor("#0A00E5FF")) }

            if (isEconomiaActive) { ativos.add("Economia"); btnEconomia.setCardBackgroundColor(Color.parseColor("#4D00E5FF")) } 
            else { btnEconomia.setCardBackgroundColor(Color.parseColor("#0A00E5FF")) }

            if (isPerformanceActive) { ativos.add("Performance"); btnPerformance.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnPerformance.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            if (isBateriaActive) { ativos.add("Bateria"); btnBateria.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnBateria.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            if (isTouchActive) { ativos.add("Touch"); btnTouch.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnTouch.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            if (isFpsActive) { ativos.add("FPS Max"); btnFps.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnFps.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            if (isSistemaActive) { ativos.add("Sistema"); btnSistema.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnSistema.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            if (isZramActive) { ativos.add("ZRAM"); btnZram.setCardBackgroundColor(Color.parseColor("#4D00E676")) } 
            else { btnZram.setCardBackgroundColor(Color.parseColor("#0A00E676")) }

            // Atualiza o painel de texto
            if (ativos.isEmpty()) {
                txtCpuSpeed.text = "MOTO G60: MODO ORIGINAL (PADRÃO)\nAGUARDANDO INJEÇÃO DE COMANDOS..."
                txtCpuSpeed.setTextColor(Color.parseColor("#00E5FF"))
            } else {
                txtCpuSpeed.text = "🔥 MÓDULOS DE TORK INJETADOS:\n" + ativos.joinToString(" | ")
                txtCpuSpeed.setTextColor(Color.parseColor("#FFEA00")) // Amarelo/Dourado avisando que está agressivo
            }
        }

        // Estado inicial
        atualizarPainel()

        // EVENTOS DE CLIQUE (Comandos Reais + Atualização do Painel)
        btnAjusteFino.setOnClickListener {
            if (!isAjusteFinoActive) {
                executarComandoShizuku("settings put global private_dns_mode hostname && settings put global private_dns_specifier 1dot1dot1dot1.cloudflare-dns.com && setprop net.tcp.buffersize.wifi 4096,87380,110208,4096,16384,110208")
                isAjusteFinoActive = true
            } else {
                executarComandoShizuku("settings put global private_dns_mode off")
                isAjusteFinoActive = false
            }
            atualizarPainel()
        }

        btnEconomia.setOnClickListener {
            if (!isEconomiaActive) {
                executarComandoShizuku("for pkg in \$(pm list packages -3 | cut -d: -f2); do am set-standby-bucket \$pkg restricted; done")
                isEconomiaActive = true
            } else {
                executarComandoShizuku("for pkg in \$(pm list packages -3 | cut -d: -f2); do am set-standby-bucket \$pkg active; done")
                isEconomiaActive = false
            }
            atualizarPainel()
        }

        btnPerformance.setOnClickListener {
            if (!isPerformanceActive) {
                executarComandoShizuku("cmd package compile -m speed -f -a && setprop debug.sf.latch_unsignaled 1 && setprop debug.cpurend.vsync false")
                isPerformanceActive = true
            } else {
                executarComandoShizuku("setprop debug.sf.latch_unsignaled 0 && setprop debug.cpurend.vsync true")
                isPerformanceActive = false
            }
            atualizarPainel()
        }

        btnBateria.setOnClickListener {
            if (!isBateriaActive) {
                executarComandoShizuku("settings put global low_power 1 && cmd power set-fixed-performance-mode off")
                isBateriaActive = true
            } else {
                executarComandoShizuku("settings put global low_power 0")
                isBateriaActive = false
            }
            atualizarPainel()
        }

        btnTouch.setOnClickListener {
            if (!isTouchActive) {
                executarComandoShizuku("settings put global touch_exploration_enabled 0 && setprop persist.sys.ui.hw true && setprop debug.performance.tuning 1")
                isTouchActive = true
            } else {
                executarComandoShizuku("setprop persist.sys.ui.hw false && setprop debug.performance.tuning 0")
                isTouchActive = false
            }
            atualizarPainel()
        }

        btnFps.setOnClickListener {
            if (!isFpsActive) {
                executarComandoShizuku("settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0 && setprop debug.egl.hw 1")
                isFpsActive = true
            } else {
                executarComandoShizuku("settings put system min_refresh_rate 60.0 && setprop debug.egl.hw 0")
                isFpsActive = false
            }
            atualizarPainel()
        }

        btnSistema.setOnClickListener {
            if (!isSistemaActive) {
                executarComandoShizuku("pm trim-caches 999G && sm fstrim -v all && settings put global low_power_sticky 1")
                isSistemaActive = true
            } else {
                executarComandoShizuku("settings put global low_power_sticky 0")
                isSistemaActive = false
            }
            atualizarPainel()
        }

        btnZram.setOnClickListener {
            if (!isZramActive) {
                executarComandoShizuku("settings put global cached_apps_freezer enabled && setprop persist.sys.fw.bg_apps_limit 64")
                isZramActive = true
            } else {
                executarComandoShizuku("settings put global cached_apps_freezer disabled")
                isZramActive = false
            }
            atualizarPainel()
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
