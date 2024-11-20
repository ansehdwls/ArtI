package com.hexa.arti.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import com.hexa.arti.R
import com.hexa.arti.util.setFullScreen
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Size
import io.github.sceneview.node.ImageNode
import kotlinx.coroutines.launch

class ARActivity : AppCompatActivity(R.layout.activity_aractivity) {

    lateinit var sceneView: ARSceneView
    lateinit var loadingView: View
    var imageUrl: String? = ""
    private val guideTextView by lazy {
        findViewById<TextView>(R.id.tv_guide_text)
    }

    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
        }

    var anchorNode: AnchorNode? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }

    var anchorNodeView: View? = null

    var trackingFailureReason: TrackingFailureReason? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }

    var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        imageUrl = intent.getStringExtra("image")

        imageUrl?.let {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        imageBitmap = resource
//                        findViewById<ImageView>(R.id.iv_test).setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        loadingView = findViewById(R.id.loadingView)
        sceneView = findViewById<ARSceneView?>(R.id.sceneView).apply {
            lifecycle = this@ARActivity.lifecycle
            planeRenderer.isEnabled = true

            configureSession { session, config ->
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }

                config.focusMode = Config.FocusMode.AUTO
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE


                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            onSessionUpdated = { _, frame ->
                if (anchorNode == null) {
                    frame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { plane ->
                            addAnchorNode(plane.createAnchor(plane.centerPose))
                        }
                }
            }
            onTrackingFailureChanged = { reason ->
                this@ARActivity.trackingFailureReason = reason
                Log.d("확인", "추적 실패: $reason")
            }

            planeRenderer.isEnabled = false

        }


    }

    private fun addAnchorNode(anchor: Anchor) {
        guideTextView.visibility = View.GONE
        sceneView.addChildNode(
            AnchorNode(sceneView.engine, anchor)
                .apply {
                    isEditable = true
                    isRotationEditable = false
                    lifecycleScope.launch {
                        isLoading = true
                        buildImageNode()?.let { addChildNode(it) }
                        isLoading = false
                    }
                    anchorNode = this
                }
        )
    }

//    private fun buildImageNode(): ImageNode? {
//
//        imageBitmap ?: return null
//
//        val imageNode = ImageNode(
//            materialLoader = sceneView.materialLoader,
//            bitmap = imageBitmap!!,
//            size = Size(0.5f, 0.5f)
//        )
//
//        imageNode.rotation = Rotation(-90f, 0f, 0f)  // x축 기준 90도 회전
//        imageNode.isEditable = true
//        imageNode.editableScaleRange = 0.3f..5.0f
//
//        return imageNode
//    }

    private fun buildImageNode(): ImageNode? {

        imageBitmap ?: return null

        // 프레임 이미지 로드
        val frameBitmap = BitmapFactory.decodeResource(resources, R.drawable.arframe)

        // 원본 이미지와 프레임 이미지를 결합
        val combinedBitmap = combineBitmaps(imageBitmap!!, frameBitmap)

        // 결합된 Bitmap을 ImageNode에 적용
        val imageNode = ImageNode(
            materialLoader = sceneView.materialLoader,
            bitmap = combinedBitmap,
            size = Size(0.5f, 0.5f),
        )

        imageNode.rotation = Rotation(-90f, 0f, 0f)  // x축 기준 90도 회전
        imageNode.isEditable = false
        imageNode.isRotationEditable = false

        imageNode.editableScaleRange = 0.3f..5.0f

        return imageNode
    }

    private fun combineBitmaps(baseBitmap: Bitmap, frameBitmap: Bitmap): Bitmap {
        // 프레임 이미지 크기를 원본 이미지 크기에 맞게 스케일링
        val scaledFrameBitmap =
            Bitmap.createScaledBitmap(frameBitmap, baseBitmap.width, baseBitmap.height, false)

        // 두 개의 Bitmap을 결합할 새 Bitmap 생성
        val combinedBitmap =
            Bitmap.createBitmap(baseBitmap.width, baseBitmap.height, baseBitmap.config)
        val canvas = Canvas(combinedBitmap)

        // 원본 이미지 먼저 그리기
        canvas.drawBitmap(baseBitmap, 0f, 0f, null)

        // 프레임 이미지 위에 그리기
        canvas.drawBitmap(scaledFrameBitmap, 0f, 0f, null)

        return combinedBitmap
    }


}