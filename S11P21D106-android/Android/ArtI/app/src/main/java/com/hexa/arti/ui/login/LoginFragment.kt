package com.hexa.arti.ui.login

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentLoginBinding
import com.hexa.arti.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun init() {
        initObserve()
        with(binding) {
            clTotal.setOnClickListener {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
            loginBtn.setOnClickListener {
//                startActivity(Intent(requireContext(), MainActivity::class.java))
                if(!loginIdEt.text.isNullOrBlank() && !loginPwEt.text.isNullOrBlank() ){
                    loginActivity.showLoadingDialog()
                    loginViewModel.updateEmail(loginIdEt.text.toString())
                    loginViewModel.updatePass(loginPwEt.text.toString())
                    loginViewModel.login()
                }
                else{
                    makeToast("ID와 PW를 입력해주세요")
                }

            }
            signBtn.setOnClickListener {
                loginActivity.moveSignUp()
            }
        }
    }

    private fun initObserve() {
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                1 -> {
                    loginActivity.hideLoadingDialog()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    loginActivity.finish()
                }

                2 -> {
                    loginActivity.hideLoadingDialog()
                    loginViewModel.loginReset()
                    makeToast("로그인 실패")
                }
                else -> {

                }
            }
        }
    }
}

