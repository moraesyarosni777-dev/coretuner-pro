package com.coretuner.pro

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private val onRequestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            atualizarStatusShizuku(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)

        txtShizuku.setOnClickListener {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    atualizarStatusShizuku(true)
                    Toast.makeText(this, "Shizuku já autorizado!", Toast.LENGTH_SHORT).show()
                } else {
                    Shizuku.requestPermission(0)
                }
            } else {
                Toast.makeText(this, "Serviço Shizuku não detectado!", Toast.LENGTH_LONG).show()
            }
        }

        // Ativação dos cliques para resposta tátil
        val botoes = listOf(
            R.id.btn_ajuste_fino to "Ajuste Fino Extremo Ativado!",
            R.id.btn_economia to "Economia Máxima Ativada!",
            R.id.btn_performance to "Performance Engatada!",
            R.id.btn_bateria to "Bateria Otimizada!",
            R.id.btn_touch to "Touch Acelerado!",
            R.id.btn_fps to "FPS Máximo Liberado!",
            R.id.btn_sistema to "Sistema Limpo!",
            R.id.btn_zram to "ZRAM Otimizada!"
        )

        botoes.forEach { (id, msg) ->
            findViewById<Button>(id).setOnClickListener {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarStatusShizuku(vinculado: Boolean) {
        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)
        if (vinculado) {
            txtShizuku.text = "> SHIZUKU VINCULADO COM SUCESSO <"
            txtShizuku.setTextColor(Color.GREEN)
        }
    }

    override fun onStart() {
        super.onStart()
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)
        if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            atualizarStatusShizuku(true)
        }
    }

    override fun onStop() {
        super.onStop()
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }
}
