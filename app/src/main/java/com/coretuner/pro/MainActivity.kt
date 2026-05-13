package com.coretuner.pro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Puxa o layout de vidro neon que montamos
        setContentView(R.layout.activity_main)
        
        // As chamadas de clique do Shizuku foram limpas temporariamente.
        // O app vai abrir liso para você testar o visual no Moto G60.
    }
}
