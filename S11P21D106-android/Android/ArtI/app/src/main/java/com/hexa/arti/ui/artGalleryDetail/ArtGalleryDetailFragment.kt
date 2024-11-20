package com.hexa.arti.ui.artGalleryDetail

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.databinding.FragmentArtGalleryDetailBinding
import com.hexa.arti.ui.MainActivity
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.artGalleryDetail.adapter.GalleryDetailViewPagerAdapter
import com.hexa.arti.ui.artGalleryDetail.adapter.GalleryThemeMenuAdapter
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "ArtGalleryDetailFragmen"

@AndroidEntryPoint
class ArtGalleryDetailFragment : BaseFragment<FragmentArtGalleryDetailBinding>(R.layout.fragment_art_gallery_detail) {

    private val args : ArtGalleryDetailFragmentArgs by navArgs()

    private val artGalleryViewModel : ArtGalleryDetailViewModel by viewModels()
    private val mainActivityViewModel : MainActivityViewModel by activityViewModels()
    private lateinit var adapter: GalleryDetailViewPagerAdapter

    private var musicStreamUrl = ""
    private var imageSize = 0
    private var isReady = false
    private var code = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false
    private var isLoading = false
    private var userGalleryId : Int = 0
    private var galleryId : Int = 0
    override fun init() {
        // galleryId를 이용해 테마 데이터 요청
        galleryId = args.galleryId
        artGalleryViewModel.getGallery(galleryId)
        artGalleryViewModel.getMusic(galleryId)
        binding.galleryMenuNameTv.text = args.galleryName
        initMedia()
        // 음악 시작
        binding.galleryBgmPlayPtn.setOnClickListener {
            if(isLoading){
                makeToast("음악을 생성하고 있습니다. 잠시만 기다려 주세요")
                return@setOnClickListener
            }
            if(isReady) {
                if(code == 200) {
                    Log.d(TAG, "init: aaaaaaaaaaa")
                    if (!isPlaying) {
                        binding.galleryBgmPlayPtn.visibility = View.GONE
                        binding.galleryBgmStopBtn.visibility = View.VISIBLE
                        isPlaying = true

                        // 코루틴을 사용하여 음악 재생
                        viewLifecycleOwner.lifecycleScope.launch {
                            playMusic()
                        }
                    }
                }
                else{
                    AlertDialog.Builder(requireContext())
                        .setTitle("음악 생성")
                        .setMessage("테마에 맞는 음악이 없습니다. \n음악을 생성하시겠습니까")
                        .setPositiveButton("확인") { dialog, _ ->
                            makeToast("음악 생성중입니다. 3분정도 시간이 소요됩니다.")
                            artGalleryViewModel.createMusic(galleryId)
                            // 확인 버튼 클릭 시 실행할 코드
                            isLoading = true
                            dialog.dismiss()
                        }
                        .setNegativeButton("취소") { dialog, _ ->
                            // 취소 버튼 클릭 시 실행할 코드
                            dialog.dismiss()  // 다이얼로그 닫기
                        }
                        .create()
                        .show()
                }
            }
            else makeToast("음악을 불러오는 중입니다 잠시만 기다려주세요")
        }


        lifecycleScope.launch {

            mainActivityViewModel.getLoginData().collect { d ->
                Log.d("확인", "onCreate: ${d?.galleryId}")
                d?.let {
                    userGalleryId = d.galleryId

                }

            }
        }


        artGalleryViewModel.musicGetStatus.observe(viewLifecycleOwner){
            isReady = true
            when(it){
                200 -> {
                    code = 200
                    Log.d(TAG, "init: aaaaaaaaaaaaa")

                    artGalleryViewModel.updateMusicGetStatus()
                }
                400 ->{
                    code = 400
                    Log.d(TAG, "init: aaaaaaaaaaaaa")

                    artGalleryViewModel.updateMusicGetStatus()
                }
                else ->{

                }
            }
        }


        // 테마 데이터를 관찰하고 업데이트
        artGalleryViewModel.galleryDetail.observe(viewLifecycleOwner) { themes ->

            Log.d(TAG, "init: ${themes}")
            
            if (themes != null) {
                // 테마와 이미지를 처리하여 ViewPager와 메뉴를 설정
                setupViewPager(themes)
                setupMenu(themes)
                // 페이지 복원은 ViewPager가 설정된 후에 한 번만 수행
                val pageNum = artGalleryViewModel.getPageNum()
                Log.d(TAG, "Restoring page in onViewCreated: $pageNum")
                binding.viewPager.setCurrentItem(pageNum, false)
            }
        }
        artGalleryViewModel.musicUrl.observe(viewLifecycleOwner){
            if(it != "") {
                musicStreamUrl = it
                makeToast("셍성 완료")
                isLoading = false
                isReady = true
                initMedia()
                artGalleryViewModel.updateUrl()
            }
        }


        with(binding){

            galleryDetailLeftIb.setOnClickListener {
                if (binding.viewPager.currentItem > 1) {
                    binding.viewPager.currentItem -= 1
                }
            }

            galleryDetailRightIb.setOnClickListener {
                if (binding.viewPager.currentItem < imageSize-1 ) {
                    binding.viewPager.currentItem += 1

                }
            }

            galleryMenuBtn.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
            galleryExitBtn.setOnClickListener {
                mainActivity.changePortrait()
                popBackStack()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = false
            requireActivity().onBackPressed()
            mainActivity.changePortrait()
        }
    }

private fun initMedia(){
    // 스트리밍 URL 설정 (예시 URL)
    musicStreamUrl = "https://just-shiner-manually.ngrok-free.app/music/${galleryId}"

    // MediaPlayer 초기화 및 스트리밍 URL 설정
    mediaPlayer = MediaPlayer().apply {
        setAudioStreamType(AudioManager.STREAM_MUSIC)  // 음악 스트림 설정
        setDataSource(musicStreamUrl)  // 스트리밍 URL 설정
        isLooping = true  // 무한 반복 재생 설정
        prepareAsync()  // 비동기 준비
    }
    // 음악 준비가 완료되면
    mediaPlayer.setOnPreparedListener {
        initEvent()
    }

}


    private fun initEvent(){
        with(binding){
            // 음악 종료
            galleryBgmStopBtn.setOnClickListener {
                stopMusic()
                binding.galleryBgmPlayPtn.visibility = View.VISIBLE
                binding.galleryBgmStopBtn.visibility = View.GONE
            }
        }

    }
    private suspend fun playMusic() {
        // 음악이 준비되면 재생
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        // 음악이 끝날 때까지 대기
        while (mediaPlayer.isPlaying) {
            delay(1000) // 1초마다 음악 상태 체크
        }

        // 음악이 끝나면 버튼 상태 변경
        isPlaying = false
        binding.galleryBgmPlayPtn.visibility = View.VISIBLE
        binding.galleryBgmStopBtn.visibility = View.GONE
    }


    // 음악 정지 함수
    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()  // 음악 일시정지
            mediaPlayer.seekTo(0)  // 음악을 처음으로 돌림
        }
        isPlaying = false
    }


    private fun handlePageSelected(position: Int) {
        when(position){
            0 -> {
                binding.galleryDetailLeftIb.visibility = View.GONE
                binding.galleryDetailRightIb.visibility = View.VISIBLE
            }
            imageSize-1 -> {
                binding.galleryDetailRightIb.visibility = View.GONE
                binding.galleryDetailLeftIb.visibility = View.VISIBLE
            }
            else->{
                binding.galleryDetailRightIb.visibility = View.VISIBLE
                binding.galleryDetailLeftIb.visibility = View.VISIBLE
            }
        }
    }

    private fun setupViewPager(themes: List<MyGalleryThemeItem>) {
        val combinedImageList = themes.flatMap { it.images } // 모든 테마의 이미지를 합침
        // 각 테마의 첫 번째 이미지 인덱스를 저장해둠
        val themeStartIndices = mutableListOf<Int>()
        var currentIndex = 0
        themes.forEach { theme ->
            themeStartIndices.add(currentIndex)
            currentIndex += theme.images.size
        }
        imageSize = currentIndex
        val adapter = GalleryDetailViewPagerAdapter(combinedImageList) { dto ->
            Log.d(TAG, "setupViewPager: $dto")
            val action = ArtGalleryDetailFragmentDirections.actionArtGalleryDetailFragmentToArtDetailFragment(imgId = dto.id, imgTitle = dto.title,imgUrl = dto.imageUrl, imgYear = dto.year ?: "2024", imgArtist = dto.artist, galleryId = args.galleryId)
            navigate(action)
        }
        binding.viewPager.adapter = adapter
        // ViewPager 어댑터가 설정된 후에 페이지 복원

        // ViewPager 페이지 변경 시 테마 제목 업데이트
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "onPageSelected: ${position}")
                artGalleryViewModel.updatePageNum(position)
                // 현재 선택된 페이지가 속한 테마를 찾아 테마 제목을 갱신
                val currentThemeIndex = themeStartIndices.indexOfLast { it <= position }
                binding.galleryThmemTv.text = "테마 : ${themes[currentThemeIndex].title}"
                handlePageSelected(position)
            }
        })
    }

    private fun setupMenu(themes: List<MyGalleryThemeItem>) {
        Log.d(TAG, "setupMenu: $themes")
        val themeTitles = themes.map { it.title }
        binding.galleryThemeRv.adapter = GalleryThemeMenuAdapter(themeTitles) { position ->
            
            if (themes[position].images.isEmpty()) {
                // 이미지가 없으면 테마 변경을 막음
                makeToast("선택된 테마는 작품이 없습니다.")
                return@GalleryThemeMenuAdapter
            }

            val firstImageIndex = themes.subList(0, position).sumOf { it.images.size } // 선택된 테마의 첫 번째 이미지 인덱스 계산

            binding.viewPager.setCurrentItem(firstImageIndex, false)
            binding.galleryThmemTv.text = "테마 : ${themeTitles[position]}"
            binding.drawerLayout.closeDrawers()
        }
    }

    // 뷰가 파괴될 때 MediaPlayer 해제
    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.release()  // MediaPlayer 리소스 해제
    }

}