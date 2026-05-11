package com.coretuner.pro

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import moe.shizuku.api.Shizuku

class MainActivity : AppCompatActivity() {

    // Definição do listener para capturar a resposta da permissão
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

        // Referência ao texto vermelho (certifique-se que o ID no XML é txt_shizuku)
        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)

        txtShizuku.setOnClickListener {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    txtShizuku.text = "> SHIZUKU VINCULADO COM SUCESSO <"
                    txtShizuku.setTextColor(Color.GREEN)
                    Toast.makeText(this, "O Shizuku já está autorizado!", Toast.LENGTH_SHORT).show()
                } else {
                    // Abre o pedido de permissão oficial do Shizuku
                    Shizuku.requestPermission(0)
                }
            } else {
                Toast.makeText(this, "Erro: O serviço do Shizuku não está rodando!", Toast.LENGTH_LONG).show()
            }
        }
        
        // Adicione aqui abaixo os cliques dos seus outros botões (Performance, Bateria, etc)
    }

    override fun onStart() {
        super.onStart()
        // Registra o ouvinte de permissão ao iniciar
        Shizuku.addRequestPermissionResultListener(onRequestPermissionResultListener)
    }

    override fun onStop() {
        super.onStop()
        // Remove o ouvinte ao fechar para economizar memória
        Shizuku.removeRequestPermissionResultListener(onRequestPermissionResultListener)
    }
}
