<!-- Sua arquitetura principal de ScrollView/LinearLayout permanece. -->

    <!-- FRAME DO BOTÃO AJUSTE FINO COMPLEXO -->
    <FrameLayout
        android:layout_width="160dp" 
        android:layout_height="100dp" 
        android:layout_margin="8dp">

        <!-- A VIEW QUE CARREGA O VISUAL 3D VETORIAL (O novo Drawable) -->
        <View
            android:id="@+id/btn_ajuste_fino_stack"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@drawable/bg_btn_vip_ajuste_stack"
            android:clickable="true"
            android:focusable="true" />

        <!-- A CAMADA DE ÍCONE E TEXTO (Mantém os IDs existentes para as ações no Kotlin) -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="8dp">
            <ImageView
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:src="@android:drawable/ic_menu_preferences"
                app:tint="#A044FF" />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="AJUSTE FINO"
                android:textColor="#C084FC"
                android:layout_marginTop="8dp"
                android:textSize="12sp"
                android:textStyle="bold" />
        </LinearLayout>
    </FrameLayout>
