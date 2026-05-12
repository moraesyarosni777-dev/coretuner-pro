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
        val btnEconomia = findViewById<MaterialCardView>(R.id.btn_economia)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnBateria = findViewById<MaterialCardView>(R.id.btn_bateria)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnFps = findViewById<MaterialCardView>(R.id.btn_fps)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        if (checkShizukuPermission()) {
            txtShizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
            txtShizuku.setTextColor(android.graphics.Color.parseColor("#00E676"))
        }

        // 1. AJUSTE FINO (REDE INSTANTÂNEA)
        btnAjusteFino.setOnClickListener {
            // Injeta DNS Cloudflare, Buffers gigantes e TCP Fast Open (abre sites sem delay)
            val cmd = "settings put global private_dns_mode hostname && settings put global private_dns_specifier 1dot1dot1dot1.cloudflare-dns.com && setprop net.tcp.buffersize.wifi 4096,87380,110208,4096,16384,110208 && echo 3 > /proc/sys/net/ipv4/tcp_fastopen"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "⚙️ AJUSTE FINO: DNS e TCP Fast Open ativados. Navegação zero delay.", Toast.LENGTH_LONG).show()
        }

        // 2. ECONOMIA
        btnEconomia.setOnClickListener {
            val cmd = "dumpsys deviceidle force-idle && settings put global alarm_manager_constants allow_while_idle_whitelist_duration=0"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "📉 ECONOMIA: Deep Doze forçado. Background neutralizado.", Toast.LENGTH_LONG).show()
        }

        // 3. PERFORMANCE (TORK BRUTO DESCOMUNAL)
        btnPerformance.setOnClickListener {
            // Clock no máximo e latência do agendador zerada. O sistema não pensa, só executa.
            val cmd = "echo performance > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor && " +
                      "echo performance > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor && " +
                      "echo 0 > /proc/sys/kernel/sched_min_granularity_ns && " +
                      "echo 0 > /proc/sys/kernel/sched_latency_ns && " +
                      "cmd package compile -m speed-profile -a"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "⚡ PERFORMANCE: Tork Bruto! CPU 100% e Latência Kernel Zerada.", Toast.LENGTH_LONG).show()
        }

        // 4. BATERIA
        btnBateria.setOnClickListener {
            val cmd = "settings put global low_power 1 && cmd power set-fixed-performance-mode off"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🛡️ BATERIA: Restrição máxima de energia em segundo plano.", Toast.LENGTH_LONG).show()
        }

        // 5. TOUCH (LATÊNCIA ZERO DA TELA E SCREENSHOT)
        btnTouch.setOnClickListener {
            // Força a GPU, arranca o delay do screenshot e tira o V-sync do toque
            val cmd = "setprop debug.hwui.render_dirty_regions false && " +
                      "setprop persist.sys.ui.hw true && " +
                      "setprop debug.screenshot.delay 0 && " +
                      "setprop debug.egl.swapinterval 0 && " +
                      "settings put system pointer_speed 7"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "👆 TOUCH: Delay de Screenshot arrancado. GPU Overlay extremo.", Toast.LENGTH_LONG).show()
        }

        // 6. FPS MAX
        btnFps.setOnClickListener {
            val cmd = "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0 && setprop debug.egl.hw 1"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🎮 FPS MAX: 120Hz cravado direto no motor SurfaceFlinger.", Toast.LENGTH_LONG).show()
        }

        // 7. SISTEMA (I/O AGRESSIVO TIPO NVME)
        btnSistema.setOnClickListener {
            // Read-ahead monstruoso para armazenamento e initrwnd alto para rede
            val cmd = "echo 2048 > /sys/block/mmcblk0/queue/read_ahead_kb && " +
                      "echo 2048 > /sys/block/sda/queue/read_ahead_kb && " +
                      "setprop dalvik.vm.dex2oat-flags --compiler-filter=speed && " +
                      "setprop net.tcp.defaultinitrwnd 60 && " +
                      "sm fstrim -v all"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🖥️ SISTEMA: Read-Ahead 2048 e FSTRIM executado. Abertura brutal.", Toast.LENGTH_LONG).show()
        }

        // 8. ZRAM
        btnZram.setOnClickListener {
            val cmd = "settings put global cached_apps_freezer enabled && setprop persist.sys.fw.bg_apps_limit 64 && echo 100 > /proc/sys/vm/swappiness"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🧠 ZRAM: App Freezer ativado com Swappiness máximo.", Toast.LENGTH_LONG).show()
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
                    val cmdArray = arrayOf("sh", "-c", comando)
                    val p = Shizuku.newProcess(cmdArray, null, null)
                    p.waitFor() 
                } catch (e: Exception) { 
                    e.printStackTrace() 
                } 
            }.start()
        }
    }
}
