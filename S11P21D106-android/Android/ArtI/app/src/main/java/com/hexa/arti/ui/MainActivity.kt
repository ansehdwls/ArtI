package com.hexa.arti.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseActivity
import com.hexa.arti.data.model.login.LoginResponse
import com.hexa.arti.databinding.ActivityMainBinding
import com.hexa.arti.util.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private var isFirst = true
    private var isShowDialog = false
    lateinit var navController: NavController
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val myGalleryActivityViewModel: MyGalleryActivityViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    var userData: LoginResponse? = null
    var isUp = true
    var isDoubleUp = false

    private var mode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        showLoadingDialog()
        with(myGalleryActivityViewModel) {
            // Coroutine 내에서 값을 수집
            lifecycleScope.launch {

                mainActivityViewModel.getLoginData().collect { d ->
                    Log.d(TAG, "onCreate: ${d?.galleryId}")
                    userData = d
                    if (d?.galleryId == -1) {
                        isFirst = true
                    } else {
                        isFirst = false
                        getMyGallery(d!!.galleryId)
                        getMyGalleryTheme(d.galleryId)
                        getNickName()
                    }

                    if (isFirst) navController.navigate(R.id.surveyFragment)
                    hideLoadingDialog()
                }
            }


        }
        setBottomNavHide()


    }


    override fun setupBinding(binding: ActivityMainBinding) {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bnMenu.setupWithNavController(navController)


        binding.btnArtUpload.setOnClickListener {
            navController.navigate(R.id.artworkUploadFragment)
        }

    }

    fun hideBottomNav(isHide: Boolean) {
        if (isHide) {
            binding.bnMenu.visibility = View.GONE
            binding.btnArtUpload.visibility = View.GONE
        } else {
            binding.bnMenu.visibility = View.VISIBLE
            binding.btnArtUpload.visibility = View.VISIBLE
        }
    }


    // 가로 모드
    fun changeLandScope() {

        if (!mode) {

            mode = true
            // 가로모드로 고정
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  // 내비게이션 바 숨기기
                            or View.SYSTEM_UI_FLAG_FULLSCREEN       // 상태바 숨기기
                    )
        }
    }

    fun changePortrait() {

        if (mode) {
            mode = false
            // 기본 모드로 복원
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }
    }

    fun moveLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    /** 바텀 네비게이션 숨기는 기능 */
    private fun setBottomNavHide() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    changePortrait()
                    hideBottomNav(false)
                    Log.d(TAG, "setBottomNavHide: home")
                }

                R.id.searchFragment -> {
                    changePortrait()
                    hideBottomNav(false)
                    Log.d(TAG, "setBottomNavHide: search")
                }

                R.id.myGalleryHomeFragment -> {
                    changePortrait()
                    hideBottomNav(false)
                    mainActivityViewModel.setFragmentState(MainActivityViewModel.SUBSCRIBE_FRAGMENT)
                    Log.d(TAG, "setBottomNavHide: myGallery")
                }

                R.id.settingFragment -> {
                    hideBottomNav(false)
                    changePortrait()
                    mainActivityViewModel.setFragmentState(MainActivityViewModel.PORTFOLIO_FRAGMENT)
                    Log.d(TAG, "setBottomNavHide: setting")
                }

                R.id.artGalleryDetailFragment -> {
                    hideBottomNav(true)
                    changeLandScope()
                }

                R.id.artDetailFragment -> {
                    hideBottomNav(true)
                    changeLandScope()
                }

                else -> {
                    hideBottomNav(true)
                    changePortrait()
                    Log.d(TAG, "setBottomNavHide: etc")
                }
            }
        }
    }


    fun showLoadingDialog() {
        if (!isShowDialog) {
            isShowDialog = true
            loadingDialog = LoadingDialog()
            loadingDialog.isCancelable = false
            loadingDialog.show(supportFragmentManager, "loading")
        }
    }

    fun hideLoadingDialog() {
        if (isShowDialog) {
            isShowDialog = false
            loadingDialog.dismiss()
        }
    }
}