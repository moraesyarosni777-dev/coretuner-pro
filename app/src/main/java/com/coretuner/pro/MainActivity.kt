package com.coretuner.pro

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    
    private lateinit var performanceButton: Button
    private lateinit var batteryButton: Button
    private lateinit var cacheButton: Button
    private lateinit var touchButton: Button
    private lateinit var statusText: TextView
    private lateinit var coreValueText: TextView
    private lateinit var dtcValueText: TextView
    private lateinit var ramValueText: TextView
    private lateinit var mainCard: CardView
    private lateinit var tabLayout: TabLayout
    private lateinit var spectrumView: ImageView
    private lateinit var signature3D: TextView
    
    private var isPerformanceMode = false
    private var isBatteryMode = false
    private var animator: ValueAnimator? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var shizukuPermissionGranted = false
    private val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
    
    // Listener para permissões do Shizuku
    private val shizukuPermissionListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                shizukuPermissionGranted = grantResult == 0
                if (shizukuPermissionGranted) {
                    Toast.makeText(this@MainActivity, "Permissão Shizuku concedida!", Toast.LENGTH_SHORT).show()
                    statusText.text = "PRONTO PARA TUNNING!"
                } else {
                    Toast.makeText(this@MainActivity, "Permissão Shizuku negada!", Toast.LENGTH_SHORT).show()
                    statusText.text = "PERMISSÕES INSUFICIENTES"
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Configurar a interface como premium
        setupPremiumInterface()
        
        // Inicializar componentes UI
        performanceButton = findViewById(R.id.performanceButton)
        batteryButton = findViewById(R.id.batteryButton)
        cacheButton = findViewById(R.id.cacheButton)
        touchButton = findViewById(R.id.touchButton)
        statusText = findViewById(R.id.statusText)
        coreValueText = findViewById(R.id.coreValueText)
        dtcValueText = findViewById(R.id.dtcValueText)
        ramValueText = findViewById(R.id.ramValueText)
        mainCard = findViewById(R.id.mainCard)
        tabLayout = findViewById(R.id.tabLayout)
        spectrumView = findViewById(R.id.spectrumView)
        signature3D = findViewById(R.id.signature3D)
        
        // Configurar abas
        setupTabs()
        
        // Configurar assinatura 3D
        setupSignature()
        
        // Configurar botões principais
        setupButtons()
        
        // Iniciar simulação de leitura de valores
        startValueSimulation()
        
        // Configurar Shizuku
        setupShizuku()
    }
    
    override fun onResume() {
        super.onResume()
        // Verificar o status do Shizuku sempre que retornar à atividade
        checkShizukuStatus()
    }
    
    private fun setupShizuku() {
        try {
            // Registrar listener para permissões
            Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
            
            // Verificar se o Shizuku está instalado
            if (!isShizukuAvailable()) {
                statusText.text = "SHIZUKU NÃO INSTALADO"
                Toast.makeText(this, "Shizuku não instalado! Instale para ativar todos os recursos.", Toast.LENGTH_LONG).show()
                
                // Abrir página do Shizuku na Play Store
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=moe.shizuku.privileged.api"))
                startActivity(intent)
                return
            }
            
            checkShizukuStatus()
            
        } catch (e: Exception) {
            e.printStackTrace()
            statusText.text = "ERRO SHIZUKU: ${e.message}"
        }
    }
    
    private fun checkShizukuStatus() {
        try {
            if (Shizuku.pingBinder()) {
                // Verificar permissão
                if (Shizuku.checkSelfPermission() == 0) {
                    shizukuPermissionGranted = true
                    statusText.text = "PRONTO PARA TUNNING!"
                } else {
                    // Solicitar permissão se não tivermos
                    requestShizukuPermission()
                }
            } else {
                statusText.text = "SHIZUKU NÃO ATIVADO"
                Toast.makeText(this, "Shizuku instalado, mas não ativado. Ative nas configurações.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            statusText.text = "ERRO SHIZUKU: ${e.message}"
        }
    }
    
    private fun requestShizukuPermission() {
        try {
            if (Shizuku.shouldShowRequestPermissionRationale()) {
                // Explicar por que precisamos da permissão
                Toast.makeText(this, "CoreTuner precisa de permissão Shizuku para otimizar o sistema", Toast.LENGTH_LONG).show()
            }
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun isShizukuAvailable(): Boolean {
        return try {
            applicationContext.packageManager.resolveContentProvider(ShizukuProvider.AUTHORITY, 0) != null
        } catch (e: Exception) {
            false
        }
    }
    
    private fun setupPremiumInterface() {
        // Configurar janela para aparência premium com transparência
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = ColorUtils.setAlphaComponent(Color.BLACK, 180)
        
        // Deixar o tema escuro e sofisticado
        setTheme(R.style.Theme_CoreTunerPro_Premium)
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("PRINCIPAL"))
        tabLayout.addTab(tabLayout.newTab().setText("PERFORMANCE"))
        tabLayout.addTab(tabLayout.newTab().setText("BATERIA"))
        tabLayout.addTab(tabLayout.newTab().setText("AVANÇADO"))
        tabLayout.addTab(tabLayout.newTab().setText("SOBRE"))
        
        // Estilizar as tabs para parecerem vidro fumê
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.view?.background = ContextCompat.getDrawable(this, R.drawable.tab_background_glass)
        }
    }
    
    private fun setupSignature() {
        // Aplicar efeito 3D à assinatura
        signature3D.text = "Moraes Yarosni"
        signature3D.setTextColor(Color.parseColor("#32CD32"))
        
        // Animar a assinatura para dar efeito 3D
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                signature3D.translationX = value * 5
                signature3D.translationY = value * 3
                signature3D.elevation = 15f + (value * 10)
            }
            start()
        }
    }
    
    private fun setupButtons() {
        // Botão de Performance Extrema (Raio)
        performanceButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                Toast.makeText(this, "Permissão Shizuku necessária para esta função", Toast.LENGTH_SHORT).show()
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            if (isBatteryMode) {
                Toast.makeText(this, "Desativando modo economia primeiro...", Toast.LENGTH_SHORT).show()
                toggleBatteryMode(false)
                Handler(Looper.getMainLooper()).postDelayed({
                    togglePerformanceMode(!isPerformanceMode)
                }, 1000)
            } else {
                togglePerformanceMode(!isPerformanceMode)
            }
        }
        
        // Botão de Economia de Bateria (Gelo/Bateria)
        batteryButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                Toast.makeText(this, "Permissão Shizuku necessária para esta função", Toast.LENGTH_SHORT).show()
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            if (isPerformanceMode) {
                Toast.makeText(this, "Desativando modo performance primeiro...", Toast.LENGTH_SHORT).show()
                togglePerformanceMode(false)
                Handler(Looper.getMainLooper()).postDelayed({
                    toggleBatteryMode(!isBatteryMode)
                }, 1000)
            } else {
                toggleBatteryMode(!isBatteryMode)
            }
        }
        
        // Botão de limpar cache
        cacheButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                Toast.makeText(this, "Permissão Shizuku necessária para esta função", Toast.LENGTH_SHORT).show()
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            coroutineScope.launch {
                cleanCache()
            }
        }
        
        // Botão de otimizar touch
        touchButton.setOnClickListener {
            if (!shizukuPermissionGranted) {
                Toast.makeText(this, "Permissão Shizuku necessária para esta função", Toast.LENGTH_SHORT).show()
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            coroutineScope.launch {
                optimizeTouch()
            }
        }
    }
    
    private fun togglePerformanceMode(enable: Boolean) {
        isPerformanceMode = enable
        
        if (enable) {
            performanceButton.setBackgroundResource(R.drawable.button_performance_active)
            mainCard.setCardBackgroundColor(Color.parseColor("#2C3E50"))
            statusText.text = "MODO EXTREMO ATIVADO"
            statusText.setTextColor(Color.parseColor("#FF5722"))
            
            // Executar otimizações em segundo plano
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    applyPerformanceTweaks()
                }
            }
        } else {
            performanceButton.setBackgroundResource(R.drawable.button_performance_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#1A2530"))
            statusText.text = "AGUARDANDO INJEÇÃO..."
            statusText.setTextColor(Color.parseColor("#32CD32"))
            
            // Reverter otimizações
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    revertPerformanceTweaks()
                }
            }
        }
    }
    
    private fun toggleBatteryMode(enable: Boolean) {
        isBatteryMode = enable
        
        if (enable) {
            batteryButton.setBackgroundResource(R.drawable.button_battery_active)
            mainCard.setCardBackgroundColor(Color.parseColor("#0D2C3F"))
            statusText.text = "MODO ECONOMIA ATIVADO"
            statusText.setTextColor(Color.parseColor("#00BCD4"))
            
            // Executar otimizações de bateria em segundo plano
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    applyBatteryTweaks()
                }
            }
        } else {
            batteryButton.setBackgroundResource(R.drawable.button_battery_inactive)
            mainCard.setCardBackgroundColor(Color.parseColor("#1A2530"))
            statusText.text = "AGUARDANDO INJEÇÃO..."
            statusText.setTextColor(Color.parseColor("#32CD32"))
            
            // Reverter otimizações de bateria
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    revertBatteryTweaks()
                }
            }
        }
    }
    
    private suspend fun applyPerformanceTweaks() {
        try {
            // Mostrar feedback ao usuário
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Aplicando tweaks de performance...", Toast.LENGTH_SHORT).show()
            }
            
            // Definir animações para 0.0.3 (usando Shizuku)
            setAnimationScales(0.3f)
            
            // Limpar cache de aplicativos
            withContext(Dispatchers.IO) {
                executeShizukuCommand("pm trim-caches 999999999")
                executeShizukuCommand("am kill-all")
            }
            
            // Otimizar taxa de atualização (usar ADB via Shizuku)
            setRefreshRate(120)
            
            // Ajustar configurações de janela (sem necessidade de root)
            try {
                withContext(Dispatchers.Main) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Otimizar uso de RAM
            optimizeRAM()
            
            // Ajustar modo de renderização
            setRenderMode("MAXIMUM")
            
            // Aplicar tweaks adicionais
            updateSystemProps()
            
            updateUIValues(3.5f, 0.0f, 95f)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun revertPerformanceTweaks() {
        try {
            // Mostrar feedback ao usuário
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Revertendo tweaks de performance...", Toast.LENGTH_SHORT).show()
            }
            
            // Restaurar animações
            setAnimationScales(1.0f)
            
            // Restaurar taxa de atualização
            setRefreshRate(60)
            
            // Restaurar modo de renderização
            setRenderMode("NORMAL")
            
            // Restaurar configurações do sistema
            restoreSystemProps()
            
            updateUIValues(1.0f, 0.5f, 60f)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun applyBatteryTweaks() {
        try {
            // Mostrar feedback ao usuário
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Aplicando tweaks de economia...", Toast.LENGTH_SHORT).show()
            }
            
            // Ativar modo de economia de bateria
            executeShizukuCommand("settings put global low_power 1")
            
            // Reduzir brilho
            withContext(Dispatchers.Main) {
                val window = window
                val layoutParams = window.attributes
                layoutParams.screenBrightness = 0.3f
                window.attributes = layoutParams
            }
            
            // Definir tempo de tela para 2 minutos
            executeShizukuCommand("settings put system screen_off_timeout 120000")
            
            // Reduzir taxa de atualização
            setRefreshRate(60)
            
            // Aumentar animações para economizar processamento
            setAnimationScales(1.5f)
            
            // Aplicar outros tweaks
            setBatteryOptimizedMode()
            
            updateUIValues(0.8f, 1.5f, 30f)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun revertBatteryTweaks() {
        try {
            // Mostrar feedback ao usuário
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Revertendo tweaks de economia...", Toast.LENGTH_SHORT).show()
            }
            
            // Desativar modo de economia
            executeShizukuCommand("settings put global low_power 0")
            
            // Restaurar brilho
            withContext(Dispatchers.Main) {
                val window = window
                val layoutParams = window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = layoutParams
            }
            
            // Restaurar timeout da tela (10 minutos)
            executeShizukuCommand("settings put system screen_off_timeout 600000")
            
            // Restaurar taxa de atualização
            setRefreshRate(90)
            
            // Restaurar animações
            setAnimationScales(1.0f)
            
            // Restaurar modo normal
            setNormalMode()
            
            updateUIValues(1.0f, 0.5f, 60f)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun cleanCache() {
        try {
            withContext(Dispatchers.Main) {
                statusText.text = "LIMPANDO CACHE..."
            }
            
            executeShizukuCommand("pm trim-caches 999999999")
            executeShizukuCommand("am kill-all")
            
            // Simular limpeza profunda
            delay(2000)
            
            withContext(Dispatchers.Main) {
                statusText.text = "CACHE LIMPO!"
                Toast.makeText(this@MainActivity, "Cache limpo com sucesso!", Toast.LENGTH_SHORT).show()
                
                // Retornar ao status normal após alguns segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isPerformanceMode && !isBatteryMode) {
                        statusText.text = "AGUARDANDO INJEÇÃO..."
                    }
                }, 3000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                statusText.text = "ERRO AO LIMPAR CACHE"
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun optimizeTouch() {
        try {
            withContext(Dispatchers.Main) {
                statusText.text = "OTIMIZANDO TOUCH..."
                Toast.makeText(this@MainActivity, "Otimizando resposta de touch...", Toast.LENGTH_SHORT).show()
            }
            
            // Reduzir latência de touch
            executeShizukuCommand("settings put global touch_sensitivity_level 10")
            
            // Simular otimização mais complexa
            delay(1500)
            
            withContext(Dispatchers.Main) {
                statusText.text = "TOUCH OTIMIZADO!"
                Toast.makeText(this@MainActivity, "Touch otimizado!", Toast.LENGTH_SHORT).show()
                
                // Retornar ao status normal após alguns segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isPerformanceMode && !isBatteryMode) {
                        statusText.text = "AGUARDANDO INJEÇÃO..."
                    }
                }, 3000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                statusText.text = "ERRO NA OTIMIZAÇÃO"
                Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun setAnimationScales(scale: Float) {
        try {
            executeShizukuCommand("settings put global animator_duration_scale $scale")
            executeShizukuCommand("settings put global transition_animation_scale $scale")
            executeShizukuCommand("settings put global window_animation_scale $scale")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun setRefreshRate(rate: Int) {
        try {
            // Atualizar taxa via Shizuku
            executeShizukuCommand("settings put system peak_refresh_rate $rate.0")
            executeShizukuCommand("settings put system min_refresh_rate $rate.0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun setRenderMode(mode: String) {
        try {
            when (mode) {
                "MAXIMUM" -> {
                    executeShizukuCommand("settings put global force_hw_ui 1")
                    executeShizukuCommand("settings put global debug.sf.hw 1")
                }
                "NORMAL" -> {
                    executeShizukuCommand("settings put global force_hw_ui 0")
                    executeShizukuCommand("settings put global debug.sf.hw 0")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun optimizeRAM() {
        try {
            // Otimizar RAM usando Shizuku
            executeShizukuCommand("am kill-all")
            executeShizukuCommand("dumpsys deviceidle force-idle")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun updateSystemProps() {
        try {
            // Otimizações de sistema via Shizuku
            executeShizukuCommand("settings put global sem_enhanced_cpu_responsiveness 1")
            executeShizukuCommand("settings put global zram_enabled 0")
            executeShizukuCommand("settings put global cached_apps_freezer enabled")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun restoreSystemProps() {
        try {
            // Reverter otimizações
            executeShizukuCommand("settings put global sem_enhanced_cpu_responsiveness 0")
            executeShizukuCommand("settings put global zram_enabled 1")
            executeShizukuCommand("settings put global cached_apps_freezer default")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun setBatteryOptimizedMode() {
        try {
            // Otimizações para economia de bateria
            executeShizukuCommand("settings put global always_finish_activities 1")
            executeShizukuCommand("settings put global wifi_scan_throttle_enabled 1")
            executeShizukuCommand("settings put global activity_manager_constants max_cached_processes=24")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun setNormalMode() {
        try {
            // Reverter otimizações de economia
            executeShizukuCommand("settings put global always_finish_activities 0")
            executeShizukuCommand("settings put global wifi_scan_throttle_enabled 0")
            executeShizukuCommand("settings put global activity_manager_constants max_cached_processes=32")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun executeShizukuCommand(command: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!Shizuku.pingBinder()) {
                    return@withContext "Shizuku não está disponível"
                }

                val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                val stdout = process.inputStream.bufferedReader().use { it.readText() }
                val stderr = process.errorStream.bufferedReader().use { it.readText() }
                process.waitFor()
                
                return@withContext stdout.ifEmpty { stderr }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Erro: ${e.message}"
            }
        }
    }
    
    private fun updateUIValues(core: Float, dtc: Float, ram: Float) {
        runOnUiThread {
            coreValueText.text = String.format("%.2f", core)
            dtcValueText.text = String.format("%.2f", dtc)
            ramValueText.text = "${ram.roundToInt()}%"
            
            // Animar os valores para efeito visual
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                addUpdateListener {
                    spectrumView.alpha = it.animatedValue as Float
                }
                start()
            }
        }
    }
    
    private fun startValueSimulation() {
        // Simular leitura de valores do sistema para UI
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val date = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault()).format(Date())
                findViewById<TextView>(R.id.dateText).text = date
                
                if (!isPerformanceMode && !isBatteryMode) {
                    val core = (Math.random() * 2 + 1).toFloat()
                    val dtc = (Math.random() * 0.5).toFloat()
                    val ram = (Math.random() * 20 + 50).toFloat()
                    
                    updateUIValues(core, dtc, ram)
                }
                
                handler.postDelayed(this, 3000)
            }
        }
        
        handler.post(runnable)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        animator?.cancel()
        
        // Remover o listener de permissão
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
    }
}
