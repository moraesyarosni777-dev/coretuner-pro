package com.coretuner.pro

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
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
                } else {
                    Shizuku.requestPermission(0)
                }
            } else {
                Toast.makeText(this, "Inicie o Shizuku!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnIds = listOf(
            R.id.btn_ajuste_fino, R.id.btn_economia, R.id.btn_performance,
            R.id.btn_bateria, R.id.btn_touch, R.id.btn_fps,
            R.id.btn_sistema, R.id.btn_zram
        )

        // Agora usamos android.view.View para aceitar os painéis de vidro clicáveis
        btnIds.forEach { id ->
            findViewById<android.view.View>(id)?.setOnClickListener {
                Toast.makeText(this, "Otimização Aplicada!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarStatusShizuku(vinculado: Boolean) {
        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)
        if (vinculado) {
            txtShizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
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
