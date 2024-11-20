package com.hexa.arti.ui.artwork

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentImageUploadBinding
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "ImageUploadFragment"

@AndroidEntryPoint
class ImageUploadFragment :
    BaseFragment<FragmentImageUploadBinding>(R.layout.fragment_image_upload) {

    private val args: ImageUploadFragmentArgs by navArgs()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private var animator: Animator? = null

    private lateinit var image: MultipartBody.Part
    private var uri = ""
    override fun init() {

        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        if (mainActivity.isUp) startProgressBarAnimation(60, 80)
        else startProgressBarAnimation(100, 80)
        mainActivity.isUp = false
        mainActivity.isDoubleUp = true

        with(binding) {

            artworkCreateImageTv.startAnimation(fadeInAnimation)
            sourceImg.startAnimation(fadeInAnimation)
            plusImg.startAnimation(fadeInAnimation)
            originImg.startAnimation(fadeInAnimation)
            artworkCreateImageBtn.startAnimation(fadeInAnimation)

            imageUploadViewModel.getArtWork(args.artId)
            imageUploadViewModel.artworkResult.observe(viewLifecycleOwner) {
                Glide.with(requireContext()).load(it.imageUrl).into(originImg)
            }

            artworkBackBtn.setOnClickListener {
                popBackStack()
            }
            artworkCreateImageBtn.setOnClickListener {

                if (uri == "") {
                    makeToast("합성할 사진을 골라주세요")
                    return@setOnClickListener
                } else {
                    mainActivity.showLoadingDialog()
                    Log.d(TAG, "init: ${image}")
                    imageUploadViewModel.makeImage(image, args.artId)
                }
            }
            sourceImgCv.setOnClickListener {
                openImagePicker()
            }
        }

        imageUploadViewModel.imageResponse.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mainActivity.hideLoadingDialog()
                mainActivity.isUp = true
                mainActivity.isDoubleUp = false
                val action =
                    ImageUploadFragmentDirections.actionImageUploadFragmentToArtworkResultFragment(
                        it,
                        1
                    )
                navigate(action)
                imageUploadViewModel.updateImageResponse()
            }
        }
    }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleImage(it, name = "content_image")
                binding.sourceImg.setImageURI(it)
                this.uri = it.toString()
            }
        }

    private fun openImagePicker() {
        getImageLauncher.launch("image/*")
    }

    private val allowedMimeTypes = listOf("image/jpeg", "image/png")

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

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val compressedFile = File(file.parent, "compressed_${file.name}")
        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 80% 압축 품질
        }
        return compressedFile
    }

    fun handleImage(imageUri: Uri,  name :String) {
        var file = uriToFile(requireContext(), imageUri)

        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(imageUri)
        if (!allowedMimeTypes.contains(mimeType)) {
            makeToast("지원하지 않는 파일 형식입니다. jpeg, jpg, png 형식의 파일만 허용됩니다.")
            return
        }
        val maxSize = 10 * 1024 * 1024 // 10MB
        if (file.length() > maxSize) {
            file = compressImage(file)

            if (file.length() > maxSize) {

                makeToast("파일크기 너무 큽니다 10MB 미만으로 넣어주세요.")
                return
            }
        }


        val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())

        image = MultipartBody.Part.createFormData(
            name,
            file.name,
            requestFile
        )

    }


        private fun startProgressBarAnimation(start: Int, end: Int) {
        animator = ValueAnimator.ofInt(start, end).apply {
            duration = 800
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                binding.progressBar.progress = progress
            }
        }
        animator?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animator?.cancel()
    }
}