package com.hexa.arti.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.hexa.arti.R
import com.hexa.arti.config.BaseActivity
import com.hexa.arti.databinding.ActivityLoginBinding
import com.hexa.arti.ui.login.LoginFragment
import com.hexa.arti.ui.signup.SignUpFragment
import com.hexa.arti.util.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initView()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_ft, LoginFragment())
            .commit()
    }

    override fun setupBinding(binding: ActivityLoginBinding) {

    }

    fun moveSignUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_ft, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }
    fun moveLogin(){
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_ft, LoginFragment())
            .commit()
    }

    fun showLoadingDialog() {
        loadingDialog = LoadingDialog()
        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, "loading")
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
}