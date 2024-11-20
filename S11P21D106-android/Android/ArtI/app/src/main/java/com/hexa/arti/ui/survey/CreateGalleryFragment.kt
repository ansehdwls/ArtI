package com.hexa.arti.ui.survey

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.survey.SurveyListDto
import com.hexa.arti.databinding.FragmentCreateGalleryBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.MyGalleryActivityViewModel
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "CreateGalleryFragment"

@AndroidEntryPoint
class CreateGalleryFragment :
    BaseFragment<FragmentCreateGalleryBinding>(R.layout.fragment_create_gallery) {

    private val createGalleryViewModel: CreateGalleryViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val myGalleryActivityViewModel: MyGalleryActivityViewModel by activityViewModels()
    private val args: CreateGalleryFragmentArgs by navArgs()
    private lateinit var surveyList: String
    private var userId = 0
    override fun init() {


        Log.d(
            TAG,
            "init: ${args.surveyList[0]} ${args.surveyList[1]} ${args.surveyList[2]} ${args.surveyList[3]} ${args.surveyList[4]}"
        )
        surveyList =
            "[${args.surveyList[0]},${args.surveyList[1]},${args.surveyList[2]},${args.surveyList[3]},${args.surveyList[4]}]"
        lifecycleScope.launch {
            mainActivityViewModel.getLoginData().collect { d ->
                Log.d(TAG, "onCreate: $d")
                userId = d?.memberId!!
            }
        }

        with(createGalleryViewModel) {
            galleryResponse.observe(viewLifecycleOwner) {
                Log.d(TAG, "init: $it")
                myGalleryActivityViewModel.getMyGallery(it.id)
                createGalleryViewModel.saveSurvey(SurveyListDto(surveyList, userId))
                createGalleryViewModel.createTheme(
                    it.id,
                    binding.createGalleryThemeEt.text.toString()
                )

            }

            themeDto.observe(viewLifecycleOwner) {
                if(it.id != 0) {
                    Log.d(TAG, "init2: $it")
                    mainActivity.hideLoadingDialog()
                    navigate(R.id.action_createGalleryFragment_to_instagramSurveyFragment)
                    updateId()
                }
            }
        }


        with(binding)
        {
            createGalleryBtn.setOnClickListener {
                Log.d(TAG, "init: $userId")
                if (createGalleryNameEt.text.toString().isEmpty()) {
                    makeToast("미술관 이름을 작성해주세요")
                } else if (createGalleryInfoEt.text.toString().isEmpty()) {
                    makeToast("미술관 소개글을 작성해주세요")
                } else if (createGalleryThemeEt.text.toString().isEmpty()) {
                    makeToast("미술관 태마를 작성해주세요")
                } else if (!createGalleryViewModel.thumbnail.isInitialized) {
                    makeToast("썸네일을 등록해주세요")
                } else {
                    createGalleryViewModel.createGallery(
                        GalleryRequest(
                            description = createGalleryInfoEt.text.toString(),
                            name = createGalleryNameEt.text.toString(),
                            ownerId = userId
                        )
                    )
                    mainActivity.showLoadingDialog()
                }
            }
            createGalleryThumbnailCv.setOnClickListener {
                openImagePicker()
            }
        }
    }

    private fun openImagePicker() {
        getImageLauncher.launch("image/*")
    }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImage(it) }
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

    private fun compressImage(file: File, quality : Int): File {
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
        Log.d(TAG, "handleImage: ${file.length()}")

        val maxSize = 1024 * 1024 // 10MB
        if (file.length() > maxSize) {
            file = compressImage(file, 80)
            Log.d(TAG, "handleImage: ${file.length()}")
            if (file.length() > maxSize) {
                file = compressImage(file, 50)
                Log.d(TAG, "handleImage 1: ${file.length()}")
                if (file.length() > maxSize) {
                    file = compressImage(file, 30)
                    Log.d(TAG, "handleImage 1: ${file.length()}")
                    if (file.length() > maxSize) {
                        makeToast("File size still exceeds limit after compression")
                        return
                    }
                }
            }
        }

        Log.d(TAG, "handleImage 2: ${file.length()}")
        binding.createGalleryThumbnailIv.setImageURI(imageUri)
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        createGalleryViewModel.updateThumbnail(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestFile
            )
        )
    }
}