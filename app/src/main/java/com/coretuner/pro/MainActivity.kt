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
    
    // DEFINIÇÃO DAS CORES EXATAS
    private val COR_FUNDO_AZUL_PETROLEO = Color.parseColor("#004D40")
    private val COR_VERDE_GAIOLA = Color.parseColor("#00E676")
    private val COR_AMARELO_STATUS = Color.parseColor("#FFFF00")
    private val COR_ATIVO_BG = Color.parseColor("#3300E676")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FORÇA O AZUL PETRÓLEO EM TODA A JANELA
        window.decorView.setBackgroundColor(COR_FUNDO_AZUL_PETROLEO)
        findViewById<android.view.View>(android.R.id.content).setBackgroundColor(COR_FUNDO_AZUL_PETROLEO)
        
        txtShizuku = findViewById(R.id.txt_shizuku)
        txtStatus = findViewById(R.id.txt_cpu_speed)

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
                val newState = !prefs.getBoolean(chave, false)
                prefs.edit().putBoolean(chave, newState).commit()
                gerenciarComandos(chave, newState)
                
                if (newState) {
                    btn.setCardBackgroundColor(COR_ATIVO_BG)
                } else {
                    btn.setCardBackgroundColor(Color.TRANSPARENT)
                }
                atualizarPainelInjetados(prefs)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("TorkPrefs", Context.MODE_PRIVATE)
        
        buttons.forEach { (chave, btn) ->
            val isActive = prefs.getBoolean(chave, false)
            
            // Bordas VERDES E GROSSAS (12dp)
            btn.strokeColor = COR_VERDE_GAIOLA
            btn.strokeWidth = 12 
            
            if (isActive) {
                btn.setCardBackgroundColor(COR_ATIVO_BG)
                gerenciarComandos(chave, true)
            } else {
                btn.setCardBackgroundColor(Color.TRANSPARENT)
            }
        }
        atualizarPainelInjetados(prefs)
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
            // TEXTO STATUS AMARELO PURO
            txtStatus.text = "INJETADO: ${ativos.joinToString(" | ")}"
            txtStatus.setTextColor(COR_AMARELO_STATUS)
        }
    }
    
    // ... (restante do código: gerenciarComandos e executarComandoShizuku permanecem os mesmos)
}
