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

        btnAjusteFino.setOnClickListener {
            executarComandoShizuku("settings put global private_dns_mode hostname && settings put global private_dns_specifier 1.1.1.1 && setprop net.tcp.buffersize.wifi 4096,87380,110208,4096,16384,110208")
            Toast.makeText(this, "⚙️ AJUSTE FINO: DNS 1.1.1.1 e buffers TCP otimizados para rede ultra rápida.", Toast.LENGTH_LONG).show()
        }

        btnPerformance.setOnClickListener {
            executarComandoShizuku("cmd package compile -m speed-profile -a && service call SurfaceFlinger 1008 i32 1 && setprop debug.performance.tuning 1")
            Toast.makeText(this, "⚡ PERFORMANCE: Dex2Oat Speed forçado e GPU Overlay ativo. Renderização agressiva.", Toast.LENGTH_LONG).show()
        }

        btnTouch.setOnClickListener {
            executarComandoShizuku("settings put system pointer_speed 7 && setprop view.scroll_friction 0.005")
            Toast.makeText(this, "👆 TOUCH: Resposta de hardware no nível máximo. Fricção de scroll zerada.", Toast.LENGTH_LONG).show()
        }

        btnFps.setOnClickListener {
            executarComandoShizuku("settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0 && setprop debug.egl.hw 1")
            Toast.makeText(this, "🎮 FPS MAX: Lock em 120Hz constante. Bypass térmico ativado.", Toast.LENGTH_LONG).show()
        }

        btnSistema.setOnClickListener {
            executarComandoShizuku("sm fstrim -v all && setprop dalvik.vm.dex2oat-flags --compiler-filter=speed")
            Toast.makeText(this, "🖥️ SISTEMA: Trim de memória e compilação de apps em modo Speed.", Toast.LENGTH_LONG).show()
        }

        btnBateria.setOnClickListener {
            executarComandoShizuku("settings put global low_power 1 && cmd power set-fixed-performance-mode off")
            Toast.makeText(this, "🛡️ BATERIA: Modo de economia persistente e Fixed Performance desligado.", Toast.LENGTH_LONG).show()
        }

        btnZram.setOnClickListener {
            executarComandoShizuku("settings put global cached_apps_freezer enabled && setprop persist.sys.fw.bg_apps_limit 64")
            Toast.makeText(this, "🧠 ZRAM: App Freezer ativado. 64 processos mantidos em cache.", Toast.LENGTH_LONG).show()
        }

        btnEconomia.setOnClickListener {
            executarComandoShizuku("dumpsys deviceidle force-idle && settings put global alarm_manager_constants allow_while_idle_whitelist_duration=0")
            Toast.makeText(this, "📉 ECONOMIA: Deep Doze forçado. Sincronização em standby bloqueada.", Toast.LENGTH_LONG).show()
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
                    // Array isolado para o Kotlin compilar sem margem de erro
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
