package com.coretuner.pro

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var txtShizuku: TextView
    private lateinit var txtStatus: TextView
    private lateinit var buttons: Map<String, MaterialCardView>
    
    // CORES: Azul Petróleo + Verde Robusto + Texto Laranja Vivo
    private val COR_FUNDO = Color.parseColor("#004D40")
    private val COR_VERDE_ROBUSTO = Color.parseColor("#00E676")
    private val COR_LARANJA_VIVO = Color.parseColor("#FF6D00")
    private val COR_ATIVO_BG = Color.parseColor("#3300E676")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.setBackgroundColor(COR_FUNDO)
        
        txtShizuku = findViewById(R.id.txt_shizuku)
        txtStatus = findViewById(R.id.txt_cpu_speed)

        try {
            val painelSuperior = findViewById<MaterialCardView>(R.id.container_principal)
            painelSuperior?.strokeColor = COR_VERDE_ROBUSTO
            painelSuperior?.strokeWidth = 10 
        } catch (e: Exception) { }

        buttons = mapOf(
            "ajuste" to findViewById<MaterialCardView>(R.id.btn_ajuste_fino),
            "economia" to findViewById<MaterialCardView>(R.id.btn_economia),
            "perf" to findViewById<MaterialCardView>(R.id.btn_performance),
            "bat" to findViewById<MaterialCardView>(R.id.btn_bateria),
            "touch" to findViewById<MaterialCardView>(R.id.btn_touch),
            "fps" to findViewById<MaterialCardView>(R.id.btn_fps),
            "sistema" to findViewById<MaterialCardView>(R.id.btn_sistema),
            "zram" to findViewById<MaterialCardView>(R.id.btn_zram)
        )

        val prefs = getSharedPreferences("TorkPrefs", Context.MODE_PRIVATE)
        
        buttons.forEach { (chave, btn) ->
            btn.setOnClickListener {
                val currentState = prefs.getBoolean(chave, false)
                val newState = !currentState
                
                // COMMIT = Salva no disco imediatamente. Impossível o Android apagar.
                prefs.edit().putBoolean(chave, newState).commit()
                
                gerenciarComandos(chave, newState)
                
                if (newState) {
                    btn.setCardBackgroundColor(COR_ATIVO_BG)
                    Toast.makeText(this, "${chave.uppercase()} TRAVADO: ATIVADO", Toast.LENGTH_SHORT).show()
                } else {
                    btn.setCardBackgroundColor(Color.TRANSPARENT)
                    Toast.makeText(this, "${chave.uppercase()} ORIGINAL: DESATIVADO", Toast.LENGTH_SHORT).show()
                }
                atualizarPainelInjetados(prefs)
            }
        }

        if (Shizuku.pingBinder()) {
            txtShizuku.text = "SISTEMA VINCULADO: MODO VIP"
            txtShizuku.setTextColor(COR_VERDE_ROBUSTO)
        }
    }

    // ONRESUME = Toda vez que o app aparece na tela (ao abrir), ele checa a memória
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("TorkPrefs", Context.MODE_PRIVATE)
        
        buttons.forEach { (chave, btn) ->
            val isActive = prefs.getBoolean(chave, false)
            
            btn.strokeColor = COR_VERDE_ROBUSTO
            btn.strokeWidth = 8 
            
            if (isActive) {
                btn.setCardBackgroundColor(COR_ATIVO_BG)
                // AUTO-REFORÇO: Re-injeta o comando garantindo que o Android não derrubou
                gerenciarComandos(chave, true)
            } else {
                btn.setCardBackgroundColor(Color.TRANSPARENT)
            }
        }
        atualizarPainelInjetados(prefs)
    }

    private fun gerenciarComandos(id: String, ativar: Boolean) {
        val comando = when (id) {
            "ajuste" -> if (ativar) "settings put global private_dns_mode hostname && settings put global private_dns_specifier 1dot1dot1dot1.cloudflare-dns.com" else "settings put global private_dns_mode off"
            "perf" -> if (ativar) "cmd package compile -m speed -f -a && setprop debug.sf.latch_unsignaled 1" else "setprop debug.sf.latch_unsignaled 0"
            "sistema" -> if (ativar) "settings put global window_animation_scale 0.1 && settings put global transition_animation_scale 0.1 && settings put global animator_duration_scale 0.1" else "settings put global window_animation_scale 1.0 && settings put global transition_animation_scale 1.0 && settings put global animator_duration_scale 1.0"
            "touch" -> if (ativar) "setprop persist.sys.ui.hw true && setprop debug.performance.tuning 1" else "setprop persist.sys.ui.hw false"
            "fps" -> if (ativar) "settings put system min_refresh_rate 120.0 && settings put system peak_refresh_rate 120.0" else "settings put system min_refresh_rate 60.0"
            "economia" -> if (ativar) "for pkg in \$(pm list packages -3 | cut -d: -f2); do am set-standby-bucket \$pkg restricted; done" else "for pkg in \$(pm list packages -3 | cut -d: -f2); do am set-standby-bucket \$pkg active; done"
            "zram" -> if (ativar) "settings put global cached_apps_freezer enabled" else "settings put global cached_apps_freezer disabled"
            "bat" -> if (ativar) "settings put global low_power 1" else "settings put global low_power 0"
            else -> ""
        }
        if (comando.isNotEmpty()) executarComandoShizuku(comando)
    }

    private fun atualizarPainelInjetados(prefs: android.content.SharedPreferences) {
        val ativos = mutableListOf<String>()
        if (prefs.getBoolean("ajuste", false)) ativos.add("Rede")
        if (prefs.getBoolean("perf", false)) ativos.add("Tork")
        if (prefs.getBoolean("sistema", false)) ativos.add("0.1x Speed")
        if (prefs.getBoolean("touch", false)) ativos.add("Resposta")
        if (prefs.getBoolean("economia", false)) ativos.add("Economia")
        if (prefs.getBoolean("bat", false)) ativos.add("Bateria")
        if (prefs.getBoolean("fps", false)) ativos.add("FPS")
        if (prefs.getBoolean("zram", false)) ativos.add("ZRAM")
        
        if (ativos.isEmpty()) {
            txtStatus.text = "STATUS: MODO ORIGINAL"
            txtStatus.setTextColor(Color.WHITE)
        } else {
            txtStatus.text = "INJETADO: ${ativos.joinToString(" | ")}"
            txtStatus.setTextColor(COR_LARANJA_VIVO)
        }
    }

    private fun executarComandoShizuku(comando: String) {
        if (Shizuku.pingBinder()) {
            Thread { try { Shizuku.newProcess(arrayOf("sh", "-c", comando), null, null).waitFor() } catch (e: Exception) {} }.start()
        }
    }
}
