package com.hexa.arti.ui.signup

import android.net.Uri
import android.transition.TransitionManager
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentSignUpBinding
import com.hexa.arti.util.handleImage
import com.hexa.arti.util.isPasswordValid
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private var isCheckCode = false
    private var isSendEmail = false
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
    private val signUpViewModel: SignUpViewModel by viewModels()


    private fun Animation(isDown: Boolean) {
        val animDown = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_slide_down)
        val animUp = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_slide_up)

        TransitionManager.beginDelayedTransition(binding.scrollView2)

        with(binding) {
            if (isDown) {
                signCertificationEt.startAnimation(animDown)
                signCertificationBtn.startAnimation(animDown)
                signCertificationTv.startAnimation(animDown)
                signCertificationTv.visibility = View.GONE
                signCertificationEt.visibility = View.GONE
                signCertificationBtn.visibility = View.GONE
            } else {
                signCertificationEt.startAnimation(animUp)
                signCertificationBtn.startAnimation(animUp)
                signCertificationTv.startAnimation(animUp)
                signCertificationTv.visibility = View.VISIBLE
                signCertificationEt.visibility = View.VISIBLE
                signCertificationBtn.visibility = View.VISIBLE
            }
        }

    }

    private fun initObserve() {
        with(signUpViewModel){
            signStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    1 -> {
                        makeToast("회원가입 완료")
                        loginActivity.moveLogin()
                    }

                    2 -> {
                        makeToast("회원가입이 실패하였습니다.")
                        signUpViewModel.updateStatus()
                    }
                    else -> {

                    }
                }
            }

            codeStatus.observe(viewLifecycleOwner){ status->
                when(status){
                    1->{
                        isCheckCode = true
                        binding.signCertificationEt.isEnabled = false
                        binding.signCertificationBtn.isEnabled = false
                        binding.signCertificationBtn.text = "확인"
                        makeToast("인증 되었습니다")
                    }
                    2->{
                        makeToast("인증번호를 다시 확인해주세요")
                        signUpViewModel.updateCode()
                    }
                    else->{

                    }
                }


            }

            emailVerify.observe(viewLifecycleOwner){
                binding.signEmailBtn.isEnabled = true
                when(it){
                    1 -> {
                        with(binding) {
                            Animation(false)
                            signEmailBtn.text = "재전송"
                            signEmailEt.isEnabled = false
                            signUpViewModel.updateEmail(signEmailEt.text.toString())
                            signCertificationEt.isEnabled = true
                            signCertificationBtn.isEnabled = true
                            isSendEmail = true
                        }
                    }
                    2 -> {
                        makeToast("${errorMessage.value}")
                    }
                }
            }
        }


    }

    override fun init() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView2) { view, insets ->
            val systemWindowInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 하단 패딩 추가 (네비게이션 바 높이만큼)
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemWindowInsets.bottom // 네비게이션 바 높이만큼 패딩 추가
            )

            insets
        }
        initObserve()
        with(binding) {

            /* 이메일 인증 */
            signEmailBtn.setOnClickListener {
                signEmailBtn.isEnabled = false
                if(emailPattern.matcher(signEmailEt.text.toString()).matches()){
                    if (!isSendEmail) {
                        signUpViewModel.sendEmail(signEmailEt.text.toString())
                    } else {
                        isCheckCode = false
                        signEmailBtn.text = "전송"
                        signEmailEt.isEnabled = true
                        Animation(true)
                        signCertificationEt.setText("")
                        isSendEmail = false
                        binding.signCertificationBtn.text = "인증"
                    }
                }
                else {
                    makeToast("이메일 형식을 맞춰주세요")
                    signEmailBtn.isEnabled = true
                }


            }

            /* 인증 번호 확인 */
            signCertificationBtn.setOnClickListener {
                signUpViewModel.checkEmailCode(signEmailEt.text.toString(),signCertificationEt.text.toString())
            }

            /* 뒤로 가기 */
            signBackBtn.setOnClickListener {
                loginActivity.moveLogin()
            }
            /* 취소 */
            signCancelBtn.setOnClickListener {
                loginActivity.moveLogin()
            }
            /* 회원 가입 완료 */
            signBtn.setOnClickListener {
                if (!isCheckCode) {
                    makeToast("이메일 인증을 완료해 주세요")
                } else if (signNickEt.text.isNullOrBlank()) {
                    makeToast("닉네임을 설정해주세요")
                } else if (signPwEt.text.isNullOrBlank()) {
                    makeToast("비밀번호를 설정해주세요")
                } else if (!isPasswordValid(signPwEt.text.toString())) {
                    makeToast("비밀번호는 대소문자 + 특수기호!@#$ 9~16자리로 입력해주세요 ")
                } else if (signPwEt.text.equals(signPwVerifyEt.text)) {
                    makeToast("비밀번호 확인 값이 다릅니다.")
                } else {
                    signUpViewModel.updateNickName(signNickEt.text.toString())
                    signUpViewModel.updatePassword(signPwEt.text.toString())
                    signUpViewModel.signUp()
                }
            }


        }

    }

}