package com.hexa.arti.ui.artmuseum

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.databinding.FragmentMyGalleryBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.MyGalleryActivityViewModel
import com.hexa.arti.ui.artmuseum.adpater.MyGalleryThemeAdapter
import com.hexa.arti.ui.artmuseum.util.showAddThemeDialog
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "MyGalleryFragment"

@AndroidEntryPoint
class MyGalleryFragment : BaseFragment<FragmentMyGalleryBinding>(R.layout.fragment_my_gallery) {

    private val myGalleryActivityViewModel: MyGalleryActivityViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val myGalleryViewModel: MyGalleryViewModel by viewModels()
    private var galleryId = 0
    private var userId = 0
    override fun init() {

        lifecycleScope.launch {

            mainActivityViewModel.getLoginData().collect { d ->
                Log.d(TAG, "onCreate: ${d?.galleryId}")
                d?.let {
                    userId = d.memberId
                    galleryId = d.galleryId
                }

            }
        }


        with(binding) {
            adapter =
                MyGalleryThemeAdapter(requireContext(), onArtWorkDelete = { themeId, artWorkId ->

                    Log.d(TAG, "init: ${themeId} $artWorkId")
                    // 테마 내부 이미지 삭제
                    myGalleryViewModel.deleteThemeDelete(themeId, artWorkId)
                }, onTextClick = {
                    val scrollAmount = dpToPx(250, requireContext())
                    binding.scrollView.post {
                        ObjectAnimator.ofInt(
                            binding.scrollView,
                            "scrollY",
                            binding.scrollView.scrollY + scrollAmount
                        ).apply {
                            duration = 400L
                            start()
                        }
                    }
                }, onThemeDelete = { themeId ->
                    Log.d(TAG, "init: ${themeId} galleryId $galleryId")
                    // 갤러리 아이디 받아오기 필요
                    myGalleryViewModel.deleteTheme(galleryId, themeId)

                })
            myGalleryThemeRv.adapter = adapter


            with(myGalleryActivityViewModel) {

                // state가 바뀌면 미술관 get 호출로 update
                with(myGalleryViewModel) {
                    updateThemeDto.observe(viewLifecycleOwner) {
                        Log.d(TAG, "init: aaaaa $it")
                        myGalleryActivityViewModel.getMyGalleryTheme(galleryId)
                    }
                }

                // 나의 미술관 이름. 썸네일, 소개
                myGallery.observe(viewLifecycleOwner) {
                    Log.d(TAG, "init 11: $it")
                    myGalleryNameTv.setText(it.name)
                    Log.d("확인", "썸넬 이미지 ${it.image}")

                    Glide.with(requireContext())
                        .load(it.image)
                        .into(myGalleryThumbnailIv)

                    myGalleryInfoEt.setText(it.description)

                    myGalleryViewModel.getGalleryDto(
                        updateGalleryDto = GalleryRequest(it.description, it.name, it.ownerId)
                    )

                }
                // 테마
                myGalleryTheme.observe(viewLifecycleOwner) {

                    Log.d(TAG, "init 22: $it")
                    adapter.submitList(it)
                }
            }

            myGalleryThemeAddBtn.setOnClickListener {
                showAddThemeDialog(requireContext(), galleryId, myGalleryViewModel)
            }
        }

        initEvent()
    }

    private lateinit var adapter: MyGalleryThemeAdapter
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }


    private var initName = ""
    private var initInfo = ""

    private fun initEvent() {
        with(binding) {
            // 미술관 이름 변경
            myGalleryNameModifyBtn.setOnClickListener {
                myGalleryNameModifyBtn.visibility = View.GONE
                myGalleryNameCheckBtn.visibility = View.VISIBLE
                myGalleryNameCancelBtn.visibility = View.VISIBLE
                myGalleryNameTv.apply {
                    initName = this.text.toString()
                    isClickable = true
                    isFocusableInTouchMode = true
                    isEnabled = true
                    requestFocus() // 포커스를 EditText로 이동
                    setSelection(text.length) // 커서를 텍스트 끝으로 이동
                }
            }
            // 미술관 제목 변경 체크 버튼
            myGalleryNameCheckBtn.setOnClickListener {
                myGalleryNameModifyBtn.visibility = View.VISIBLE
                myGalleryNameCheckBtn.visibility = View.GONE
                myGalleryNameCancelBtn.visibility = View.GONE
                myGalleryNameTv.apply {
                    isClickable = false
                    isFocusable = false
                    isEnabled = false
                }
                myGalleryViewModel.updateGalleryName(myGalleryNameTv.text.toString(), galleryId)

            }
            // 미술관 이름 변경 취소 버튼
            myGalleryNameCancelBtn.setOnClickListener {
                myGalleryNameModifyBtn.visibility = View.VISIBLE
                myGalleryNameCheckBtn.visibility = View.GONE
                myGalleryNameCancelBtn.visibility = View.GONE
                myGalleryNameTv.apply {
                    isClickable = false
                    isFocusable = false
                    isEnabled = false
                    setText(initName)
                }
            }

            // 미술관 소개 버튼
            myGalleryInfoModifyBtn.setOnClickListener {
                initInfo = myGalleryInfoEt.text.toString()
                myGalleryInfoCheckBtn.visibility = View.VISIBLE
                myGalleryInfoCancelBtn.visibility = View.VISIBLE
                myGalleryInfoModifyBtn.visibility = View.GONE

                with(myGalleryInfoEt) {
                    isClickable = true
                    isFocusableInTouchMode = true
                    isEnabled = true
                    requestFocus() // 포커스를 EditText로 이동
                    setSelection(text.length) // 커서를 텍스트 끝으로 이동
                }
            }

            // 소개글 수정 체크 버튼
            myGalleryInfoCheckBtn.setOnClickListener {
                myGalleryInfoCheckBtn.visibility = View.GONE
                myGalleryInfoCancelBtn.visibility = View.GONE
                myGalleryInfoModifyBtn.visibility = View.VISIBLE

                with(myGalleryInfoEt) {
                    isClickable = false
                    isFocusableInTouchMode = false
                    isEnabled = false
                }

                myGalleryViewModel.updateGalleryDescription(
                    myGalleryInfoEt.text.toString(),
                    galleryId
                )

            }

            // 소개글 수정 취소 버튼
            myGalleryInfoCancelBtn.setOnClickListener {
                myGalleryInfoCheckBtn.visibility = View.GONE
                myGalleryInfoCancelBtn.visibility = View.GONE
                myGalleryInfoModifyBtn.visibility = View.VISIBLE

                with(myGalleryInfoEt) {
                    isClickable = false
                    isFocusableInTouchMode = false
                    isEnabled = false
                    setText(initInfo)
                }
            }

            // 미술관 실행 버튼
            myGalleryPlayBtn.setOnClickListener {
                val action =
                    MyGalleryHomeFragmentDirections.actionMyGalleryHomeFragmentToArtGalleryDetailFragment(
                        galleryId = galleryId, galleryName =  myGalleryNameTv.text.toString()
                    )
                navigate(action)
            }
            // 썸네일 이미지 클릭
            myGalleryThumbnailIv.setOnClickListener {
                openImagePicker()
            }
        }
    }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleImage(it)
                binding.myGalleryThumbnailIv.setImageURI(it)
            }
        }

    private fun openImagePicker() {
        getImageLauncher.launch("image/*")
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image${System.currentTimeMillis()}.jpg")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
        return file
    }

    private fun compressImage(file: File, quality: Int): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val compressedFile = File(file.parent, "compressed_${file.name}")
        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream) // 80% 압축 품질
        }
        return compressedFile
    }

    private val allowedMimeTypes = listOf("image/jpeg", "image/png")

    private fun handleImage(imageUri: Uri) {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(imageUri)

        if (!allowedMimeTypes.contains(mimeType)) {
            makeToast("지원하지 않는 파일 형식입니다. jpeg, jpg, png 형식의 파일만 허용됩니다.")
            return
        }

        var file = uriToFile(requireContext(), imageUri)

        val maxSize = 1024 * 1024 // 10MB
        if (file.length() > maxSize) {
            file = compressImage(file, 80)
            if (file.length() > maxSize) {
                file = compressImage(file, 50)
                if (file.length() > maxSize) {
                    file = compressImage(file, 30)
                    if (file.length() > maxSize) {
                        makeToast("File size still exceeds limit after compression")
                        return
                    }
                }
            }
        }

        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        myGalleryViewModel.updateThumbnail(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestFile
            ), galleryId
        )
    }

}