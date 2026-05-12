// ... (mantenha os imports e variáveis iguais)

        // 5. TOUCH (LATÊNCIA ZERO - O Anti-Delay do Screenshot está aqui)
        btnTouch.setOnClickListener {
            // Agressividade máxima na camada gráfica e de captura
            val cmd = "setprop debug.hwui.render_dirty_regions false && " +
                      "setprop persist.sys.ui.hw true && " +
                      "setprop debug.screenshot.delay 0 && " +
                      "setprop debug.egl.swapinterval 0"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "👆 TOUCH: Latência de captura de tela zerada e buffer gráfico resetado.", Toast.LENGTH_LONG).show()
        }

        // 7. SISTEMA (I/O AGRESSIVO + Buffer de Rede)
        btnSistema.setOnClickListener {
            // Adicionado prioridade máxima ao Socket de Rede e Buffer de I/O
            val cmd = "echo 1024 > /sys/block/mmcblk0/queue/read_ahead_kb && " +
                      "setprop net.tcp.defaultinitrwnd 60 && " +
                      "setprop ro.config.hw_quickpoweron true"
            executarComandoShizuku(cmd)
            Toast.makeText(this, "🖥️ SISTEMA: Read-Ahead no talo e Buffer TCP expandido para rede sem espera.", Toast.LENGTH_LONG).show()
        }

// ... (mantenha o restante da estrutura igual)
