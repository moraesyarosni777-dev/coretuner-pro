package com.coretuner.pro

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    // Escuta a resposta do sistema quando você clicar em "Permitir"
    private val onRequestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)
            txtShizuku.text = "> SHIZUKU VINCULADO COM SUCESSO <"
            txtShizuku.setTextColor(Color.GREEN)
            Toast.makeText(this, "Permissão Concedida!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)

        txtShizuku.setOnClickListener {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    txtShizuku.text = "> SHIZUKU VINCULADO COM SUCESSO <"
                    txtShizuku.setTextColor(Color.GREEN)
                    Toast.makeText(this, "O Shizuku já está autorizado!", Toast.LENGTH_SHORT).show()
                } else {
                    // Pede a permissão pro sistema
                    Shizuku.requestPermission(0)
                }
            } else {
                Toast.makeText(this, "Erro: O serviço do Shizuku não está rodando!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    override fun onStop() {
        super.onStop()
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }
}
