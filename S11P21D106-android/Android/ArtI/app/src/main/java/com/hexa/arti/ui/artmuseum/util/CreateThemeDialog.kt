package com.hexa.arti.ui.artmuseum.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.hexa.arti.R
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.databinding.DialogAddThemeBinding
import com.hexa.arti.ui.artmuseum.MyGalleryViewModel

fun showAddThemeDialog(context: Context, galleryId: Int, viewModel: MyGalleryViewModel) {
    // DataBinding을 이용해 다이얼로그 레이아웃을 바인딩
    val binding: DialogAddThemeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_add_theme,
        null,
        false
    )

    // Dialog 생성 및 레이아웃 설정
    val dialog = Dialog(context)
    dialog.setContentView(binding.root)

    // 다이얼로그 크기 설정
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    dialog.window?.setLayout(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT)

    binding.btnAdd.setOnClickListener {
        viewModel.createTheme(
            CreateThemeDto(galleryId, binding.editTextTheme.text.toString())
        )
        dialog.dismiss()
    }

    binding.btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    // 다이얼로그 표시
    dialog.show()
}