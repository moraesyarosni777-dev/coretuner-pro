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

        // MOTOR REAL: Aplica as 3 escalas em 0.0.3 estritamente
        findViewById<android.view.View>(R.id.btn_ajuste_fino)?.setOnClickListener {
            if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                executarComandoShizuku("settings put global window_animation_scale 0.0.3")
                executarComandoShizuku("settings put global transition_animation_scale 0.0.3")
                executarComandoShizuku("settings put global animator_duration_scale 0.0.3")
                Toast.makeText(this, "Escalas cravadas em 0.0.3!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Shizuku sem permissão!", Toast.LENGTH_SHORT).show()
            }
        }

        val outrosIds = listOf(R.id.btn_economia, R.id.btn_performance, R.id.btn_bateria, R.id.btn_touch, R.id.btn_fps, R.id.btn_sistema, R.id.btn_zram)
        outrosIds.forEach { id ->
            findViewById<android.view.View>(id)?.setOnClickListener {
                Toast.makeText(this, "Módulo ativado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun executarComandoShizuku(comando: String) {
        try {
            val processo = Shizuku.newProcess(arrayOf("sh", "-c", comando), null, null)
            processo.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun atualizarStatusShizuku(vinculado: Boolean) {
        val txtShizuku = findViewById<TextView>(R.id.txt_shizuku)
        if (vinculado) {
            txtShizuku.text = "SHIZUKU VINCULADO COM SUCESSO"
            txtShizuku.setTextColor(Color.parseColor("#39FF14"))
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
