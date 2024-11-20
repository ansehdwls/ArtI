package com.hexa.arti.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hexa.arti.R
import com.hexa.arti.config.BaseActivity
import com.hexa.arti.databinding.ActivityMainBinding
import com.hexa.arti.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val splashTimeOut: Long = 2000 // 3초 (3000 밀리초)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 3초 뒤에 로그인 페이지로 이동
        CoroutineScope(Dispatchers.IO).launch {
            delay(splashTimeOut)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            navigateToLogin()
        }
    }

    override fun setupBinding(binding: ActivitySplashBinding) {

    }

    private fun navigateToLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish() // Splash 화면을 종료하여 뒤로가기 버튼을 눌러도 다시 돌아오지 않도록 함.
    }
}