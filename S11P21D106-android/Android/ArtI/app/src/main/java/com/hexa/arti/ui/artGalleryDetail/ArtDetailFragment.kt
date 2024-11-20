package com.hexa.arti.ui.artGalleryDetail

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.databinding.FragmentArtDetailBinding
import com.hexa.arti.ui.ARActivity
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "ArtDetailFragment"

@AndroidEntryPoint
class ArtDetailFragment : BaseFragment<FragmentArtDetailBinding>(R.layout.fragment_art_detail) {

    private val args: ArtDetailFragmentArgs by navArgs()
    private val artDetailViewModel: ArtDetailViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private var selectedThemeId: Int = 1
    private var selectDescription: String = ""
    override fun init() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.getLoginData().collect { d ->
                    d?.let {
                        if (it.galleryId == args.galleryId) {
                            binding.artDetailSaveBtn.visibility = View.GONE
                        }
                        artDetailViewModel.getMyGalleryTheme(it.galleryId)
                    }
                }
            }
        }

        artDetailViewModel.galleryTheme.observe(viewLifecycleOwner) {
            Log.d(TAG, "init: $it")
            setupRadioButtons(it)
        }
        with(binding) {
            Log.d(TAG, "init: ${args.imgUrl}")
            Glide.with(requireContext())
                .load(args.imgUrl)
                .into(artDetailIv)
            artDetailTitleTv.text = args.imgTitle
            artDetailAuthorTv.text = args.imgArtist
            artDetailCreateTv.text = args.imgYear

            artDetailCancelBtn.setOnClickListener {

                popBackStack()
            }
            artDetailBackBtn.setOnClickListener {
                artDetailCl.visibility = View.VISIBLE
                artDetailThemeCl.visibility = View.GONE
            }

            artDetailArBtn.setOnClickListener {
                val intent = Intent(requireContext(), ARActivity::class.java)
                intent.putExtra("image", args.imgUrl)
                startActivity(intent)
            }

            artDetailSubmitBtn.setOnClickListener {
                Log.d("Selected Theme ID", "Selected Theme ID: $selectedThemeId")

                val inflater = LayoutInflater.from(requireContext())
                val dialogView = inflater.inflate(R.layout.custom_edit_text, null) // 커스텀 뷰 인플레이트
                val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription) // EditText 참조

                AlertDialog.Builder(requireContext())
                    .setTitle("설명")
                    .setMessage("작품에 대해 설명해주세요")
                    .setView(dialogView) // 인플레이트한 커스텀 뷰 설정
                    .setPositiveButton("확인") { dialog, which ->
                        // descriptionEditText에서 입력된 텍스트 가져오기
                        val description = descriptionEditText.text.toString()
                        artDetailViewModel.postArtwork(
                            themeId = selectedThemeId,
                            args.imgId,
                            description
                        )
                        makeToast("작품이 등록되었습니다")
                        artDetailCl.visibility = View.VISIBLE
                        artDetailThemeCl.visibility = View.GONE
                    }
                    .setNegativeButton("취소") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }
            artDetailSaveBtn.setOnClickListener {
                artDetailCl.visibility = View.GONE
                artDetailThemeCl.visibility = View.VISIBLE
            }

        }

    }

    private fun setupRadioButtons(l: List<MyGalleryThemeItem>) {
        val gridLayout = binding.gridLayout

        // GridLayout 내 모든 RadioButton 찾기
        val radioButtonList = mutableListOf<RadioButton>()
        for (i in 0 until gridLayout.childCount) {
            val view = gridLayout.getChildAt(i)
            if (view is RadioButton) {
                radioButtonList.add(view)
            }
        }

        // 각 RadioButton에 테마 할당 (필요한 개수만큼 설정하고 나머지는 GONE 처리)
        radioButtonList.forEachIndexed { index, radioButton ->
            if (index < l.size) {
                val (id, title) = l[index]
                radioButton.apply {
                    text = title
                    this.id = id  // 테마의 ID를 RadioButton의 id로 설정
                    visibility = View.VISIBLE
                    isChecked = false  // 초기 상태에서 선택되지 않도록 설정
                }
            } else {
                radioButton.visibility = View.GONE  // 사용되지 않는 RadioButton 숨기기
            }
        }

        // 각 RadioButton 클릭 시 다른 버튼들 해제 및 선택된 RadioButton 정보 로그 출력
        radioButtonList.forEach { radioButton ->
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 다른 모든 RadioButton 해제
                    radioButtonList.forEach { btn ->
                        if (btn != buttonView) {
                            btn.isChecked = false
                        }
                    }

                    // 선택된 RadioButton의 ID와 텍스트 가져오기
                    val selectedId = buttonView.id
                    val selectedText = buttonView.text.toString()
                    selectedThemeId = selectedId
                    Log.d("RadioButton", "Selected: $selectedId, Text: $selectedText $l")
                }
            }
        }
    }
}