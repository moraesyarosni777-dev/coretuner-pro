package com.coretuner.pro

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    // 1. DECLARAÇÃO GLOBAL: O arquivo inteiro agora enxerga essas variáveis (Resolve o erro da linha 70)
    private lateinit var txt_shizuku: TextView
    private lateinit var txt_cpu_speed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. A PONTE MANUAL: Conectando o Kotlin com os IDs do XML (Resolve o erro da linha 23)
        txt_shizuku = findViewById(R.id.txt_shizuku)
        txt_cpu_speed = findViewById(R.id.txt_cpu_speed)

        // Mapeamento dos botões (Pronto para receber os clicks)
        val btnAjusteFino = findViewById<MaterialCardView>(R.id.btn_ajuste_fino)
        val btnEconomia = findViewById<MaterialCardView>(R.id.btn_economia)
        val btnPerformance = findViewById<MaterialCardView>(R.id.btn_performance)
        val btnBateria = findViewById<MaterialCardView>(R.id.btn_bateria)
        val btnTouch = findViewById<MaterialCardView>(R.id.btn_touch)
        val btnFps = findViewById<MaterialCardView>(R.id.btn_fps)
        val btnSistema = findViewById<MaterialCardView>(R.id.btn_sistema)
        val btnZram = findViewById<MaterialCardView>(R.id.btn_zram)

        // Verificação inicial do Shizuku
        if (checkShizukuPermission()) {
            txt_shizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
            txt_shizuku.setTextColor(android.graphics.Color.parseColor("#00E676"))
        } else {
            txt_shizuku.text = "SHIZUKU NÃO VINCULADO"
            txt_shizuku.setTextColor(android.graphics.Color.parseColor("#FF1744"))
        }

        // =========================================================
        // COLE AQUI PARA BAIXO A SUA LÓGICA DE CLIQUES E COMANDOS SHELL
        // Exemplo:
        // btnPerformance.setOnClickListener {
        //     executarComandoShell("su -c 'seu_codigo_aqui'")
        // }
        // =========================================================
    }

    // Função padrão de checagem do Shizuku
    private fun checkShizukuPermission(): Boolean {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return true
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                // Usuário negou antes
                return false
            } else {
                // Pede permissão
                Shizuku.requestPermission(0)
                return false
            }
        }
        return false
    }

    // =========================================================
    // COLE AQUI SUAS FUNÇÕES EXTRAS (COMO O EXECUTOR DE SHELL)
    // =========================================================
}
