package com.coretuner.pro

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
// ESTA É A LINHA CRÍTICA QUE ESTAVA FALTANDO E CAUSANDO O ERRO NO LOG 67646.PNG
// Verifique se o pacote 'com.coretuner.pro' corresponde ao applicationID no seu build.gradle
import com.coretuner.pro.R

class MainActivity : AppCompatActivity() {

    private lateinit var txtDataVal: TextView
    private lateinit var txtAtuVal: TextView
    private lateinit var txtUpiVal: TextView
    private lateinit var txtBpgVal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtDataVal = findViewById(R.id.txt_data_val)
        txtAtuVal = findViewById(R.id.txt_atu_val)
        txtUpiVal = findViewById(R.id.txt_upi_val)
        txtBpgVal = findViewById(R.id.txt_bpg_val)

        // Inicializa dados e módulos
        loadVipTelemetryData()
        configureVipModules()
    }

    private fun loadVipTelemetryData() {
        txtDataVal.text = "DATA      1.30"
        txtAtuVal.text = "ATU:   53.57 MHz"
        txtUpiVal.text = "UPI:      25.03"
        txtBpgVal.text = "BPG:   38.05 MHs"
    }

    private fun configureVipModules() {
        // Encontra os botões no layout usando findViewById. Agora o R.id deve funcionar.
        // Verifique se estes IDs existem no seu activity_main.xml
        val btnAjuste = findViewById<MaterialCardView>(R.id.btn_ajuste)
        val btnLineTime = findViewById<MaterialCardView>(R.id.btn_line_time)
        val btnFingerprint = findViewById<MaterialCardView>(R.id.btn_fingerprint)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnMonitor = findViewById<MaterialCardView>(R.id.btn_monitor)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        // Cria listas para agrupar os botões por cor base
        val purpleButtons = listOf(btnAjuste, btnFingerprint, btnMonitor)
        val cyanButtons = listOf(btnLineTime, btnPerformance, btnZram)

        // Define as cores (Base e Ativa/Neon) para cada grupo
        val colorPurpleBase = Color.parseColor("#8A2BE2") // Roxo Base
        val colorPurpleActive = Color.parseColor("#A044FF") // Roxo Neon
        val colorCyanBase = Color.parseColor("#22D3EE") // Ciano Base
        val colorCyanActive = Color.parseColor("#5BF0FE") // Ciano Neon

        // Configura as ações de toque para cada grupo de botões
        purpleButtons.forEach { button ->
            setupButtonAction(button, colorPurpleBase, colorPurpleActive)
        }

        cyanButtons.forEach { button ->
            setupButtonAction(button, colorCyanBase, colorCyanActive)
        }
    }

    /**
     * Configura o listener de toque para alterar a cor da borda (stroke) do CardView.
     * @param button O CardView a ser configurado.
     * @param baseColor A cor padrão da borda.
     * @param highlightColor A cor da borda quando pressionado (efeito neon).
     */
    private fun setupButtonAction(button: MaterialCardView?, baseColor: Int, highlightColor: Int) {
        // Verificação de segurança: se o botão for nulo (não encontrado), encerra a função
        if (button == null) return

        // Define a cor base inicial da borda
        button.strokeColor = baseColor

        // Configura o ouvinte de eventos de toque (Touch Listener)
        button.setOnTouchListener { _, event ->
            when (event.action) {
                // Quando o usuário pressiona o botão
                MotionEvent.ACTION_DOWN -> button.strokeColor = highlightColor
                // Quando o usuário solta o botão ou cancela o toque
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> button.strokeColor = baseColor
                else -> {}
            }
            // Retorna 'false' para permitir que o evento de clique (OnClickListener) ainda funcione
            false
        }
    }
}
