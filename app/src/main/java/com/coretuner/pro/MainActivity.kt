package com.coretuner.pro // VERIFIQUE: Se seu projeto estiver em outra pasta, mude isso aqui.

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

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
        val purpleButtons = listOf(
            findViewById<MaterialCardView>(R.id.btn_ajuste_fino),
            findViewById<MaterialCardView>(R.id.btn_fingerprint),
            findViewById<MaterialCardView>(R.id.btn_monitor)
        )

        val cyanButtons = listOf(
            findViewById<MaterialCardView>(R.id.btn_line_time),
            findViewById<MaterialCardView>(R.id.btn_performance),
            findViewById<MaterialCardView>(R.id.btn_zram)
        )

        val colorPurpleBase = Color.parseColor("#8A2BE2")
        val colorPurpleActive = Color.parseColor("#A044FF")
        val colorCyanBase = Color.parseColor("#22D3EE")
        val colorCyanActive = Color.parseColor("#5BF0FE")

        purpleButtons.forEach { button ->
            configureVipButtonHighlight(button, colorPurpleBase, colorPurpleActive)
        }

        cyanButtons.forEach { button ->
            configureVipButtonHighlight(button, colorCyanBase, colorCyanActive)
        }
    }

    private fun configureVipButtonHighlight(button: MaterialCardView, baseColor: Int, highlightColor: Int) {
        button.strokeColor = baseColor
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> button.strokeColor = highlightColor
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> button.strokeColor = baseColor
            }
            false
        }
    }
}
