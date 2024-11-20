package com.hexa.arti.ui.profile

import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.hexa.arti.BuildConfig
import com.hexa.arti.R
import com.hexa.arti.config.ApplicationClass
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.portfolio.PortfolioGenre
import com.hexa.arti.databinding.FragmentPortfolioBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.MyGalleryActivityViewModel
import com.hexa.arti.ui.setting.SettingFragmentDirections
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PortfolioFragment : BaseFragment<FragmentPortfolioBinding>(R.layout.fragment_portfolio) {

    private val myGalleryActivityViewModel: MyGalleryActivityViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val portfolioViewModel: PortfolioViewModel by viewModels()
    private var userId: Int = -1
    private var clickedGenre = ""

    override fun init() {
        binding.pcChart.setNoDataText("")
        initUserData()
        initObserve()
        initViews()
    }

    private fun initObserve() {
        myGalleryActivityViewModel.nickname.observe(viewLifecycleOwner) {
            binding.tvPortfolioTitle.text = "${it}님의 포트폴리오"
        }


        portfolioViewModel.resultGenres.observe(viewLifecycleOwner) { genres ->
            if (genres.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.tvNoData.visibility = View.GONE
                initChart(genres)
            }
        }

        mainActivityViewModel.fragmentState.observe(viewLifecycleOwner) { state ->
            if (state == MainActivityViewModel.PORTFOLIO_FRAGMENT) {
                portfolioViewModel.resultGenres.value?.let {
                    initChart(it)
                }
            }
        }

        portfolioViewModel.resultArtist.observe(viewLifecycleOwner) { artists ->
            val fadeInAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.artist_fade_in_slide_left)

            binding.tvRepresentArtist.text = "대표 화가 : ${clickedGenre}"

            Glide.with(requireContext())
                .load(artists[0].imageUrl)
                .error(R.drawable.temp_represent_artist)
                .override(200, 200)
                .circleCrop()
                .into(binding.ivRepresentArtist1)

            Glide.with(requireContext())
                .load(artists[1].imageUrl)
                .error(R.drawable.temp_represent_artist)
                .override(200, 200)
                .circleCrop()
                .into(binding.ivRepresentArtist2)

            Glide.with(requireContext())
                .load(artists[2].imageUrl)
                .error(R.drawable.temp_represent_artist)
                .override(200, 200)
                .circleCrop()
                .into(binding.ivRepresentArtist3)

            binding.tvRepresentArtist1.text = artists[0].korName
            binding.tvRepresentArtist2.text = artists[1].korName
            binding.tvRepresentArtist3.text = artists[2].korName

            binding.tvRepresentArtist.visibility = View.VISIBLE

            binding.ivRepresentArtist1.visibility = View.VISIBLE
            binding.ivRepresentArtist2.visibility = View.VISIBLE
            binding.ivRepresentArtist3.visibility = View.VISIBLE

            binding.tvRepresentArtist1.visibility = View.VISIBLE
            binding.tvRepresentArtist2.visibility = View.VISIBLE
            binding.tvRepresentArtist3.visibility = View.VISIBLE

            binding.ivRepresentArtist1.setOnClickListener {
                val action =
                    SettingFragmentDirections.actionSettingFragmentToArtistDetailFragment(
                        artists[0]
                    )
                navigate(action)
            }
            binding.ivRepresentArtist2.setOnClickListener {
                val action =
                    SettingFragmentDirections.actionSettingFragmentToArtistDetailFragment(
                        artists[1]
                    )
                navigate(action)
            }
            binding.ivRepresentArtist3.setOnClickListener {
                val action =
                    SettingFragmentDirections.actionSettingFragmentToArtistDetailFragment(
                        artists[2]
                    )
                navigate(action)
            }


            binding.tvRepresentArtist.startAnimation(fadeInAnimation)

            binding.tvRepresentArtist1.startAnimation(fadeInAnimation)
            binding.tvRepresentArtist2.startAnimation(fadeInAnimation)
            binding.tvRepresentArtist3.startAnimation(fadeInAnimation)

            binding.ivRepresentArtist1.startAnimation(fadeInAnimation)
            binding.ivRepresentArtist2.startAnimation(fadeInAnimation)
            binding.ivRepresentArtist3.startAnimation(fadeInAnimation)

            binding.tvRepresentArtist1.startAnimation(fadeInAnimation)
            binding.tvRepresentArtist2.startAnimation(fadeInAnimation)
            binding.tvRepresentArtist3.startAnimation(fadeInAnimation)


        }
    }

    private fun initViews() {

        binding.tvRepresentGenre1.setOnClickListener {
            clickedGenre = binding.tvRepresentGenre1.text.toString()
            val englishName =
                ApplicationClass.KOREAN_TO_ENGLISH_MAP[binding.tvRepresentGenre1.text.toString()]
            englishName?.let {
                getRepresentArtists(it)
            }
        }

        binding.tvRepresentGenre2.setOnClickListener {
            clickedGenre = binding.tvRepresentGenre2.text.toString()
            val englishName =
                ApplicationClass.KOREAN_TO_ENGLISH_MAP[binding.tvRepresentGenre2.text.toString()]
            englishName?.let {
                getRepresentArtists(it)
            }
        }

        binding.tvRepresentGenre3.setOnClickListener {
            clickedGenre = binding.tvRepresentGenre3.text.toString()
            val englishName =
                ApplicationClass.KOREAN_TO_ENGLISH_MAP[binding.tvRepresentGenre3.text.toString()]
            englishName?.let {
                getRepresentArtists(it)
            }
        }

        binding.tvRepresentGenre4.setOnClickListener {
            clickedGenre = binding.tvRepresentGenre4.text.toString()
            val englishName =
                ApplicationClass.KOREAN_TO_ENGLISH_MAP[binding.tvRepresentGenre4.text.toString()]
            englishName?.let {
                getRepresentArtists(it)
            }
        }
    }

    private fun initChart(genres: List<PortfolioGenre>) {
        if (genres.isEmpty()) return
        updateGenreViews(genres)
        val englishName = ApplicationClass.KOREAN_TO_ENGLISH_MAP[genres[0].genre]
        englishName?.let {
            getRepresentArtists(it)
        }

        binding.pcChart.visibility = View.VISIBLE

        clickedGenre = genres[0].genre

        val dataList = ArrayList<PieEntry>()

        val sortedGenres = genres.sortedByDescending { it.count }
        val totalCount = genres.sumOf { it.count }

        if (sortedGenres.size <= 4) {
            sortedGenres.forEachIndexed() { index, genre ->
                dataList.add(PieEntry((genre.count.toFloat() / totalCount) * 100, genre.genre))
            }
        } else {
            sortedGenres.take(4).forEach { genre ->
                dataList.add(PieEntry((genre.count.toFloat() / totalCount) * 100, genre.genre))
            }

            val otherCount = sortedGenres.drop(4).sumOf { it.count }
            dataList.add(PieEntry((otherCount.toFloat() / totalCount) * 100, "그 외"))
        }

        val dataSet = PieDataSet(dataList, "")
        with(dataSet) {
            sliceSpace = 3f
            setColors(*ColorTemplate.JOYFUL_COLORS)
        }
        dataSet.setDrawValues(false)

        var data = PieData(dataSet)

        binding.pcChart.data = data
        binding.pcChart.description.isEnabled = false
        binding.pcChart.setTouchEnabled(false)
        binding.pcChart.isRotationEnabled = false
        binding.pcChart.legend.isEnabled = false
        binding.pcChart.isDrawHoleEnabled = true
        binding.pcChart.animateY(500)
        binding.pcChart.invalidate()

    }

    private fun updateGenreViews(genres: List<PortfolioGenre>) {
        // 장르를 4개까지 사용, 그 외는 GONE 처리
        val genreTextViews = listOf(
            binding.tvRepresentGenre1,
            binding.tvRepresentGenre2,
            binding.tvRepresentGenre3,
            binding.tvRepresentGenre4
        )

        val genrePercentageTextViews = listOf(
            binding.tvPercentage1,
            binding.tvPercentage2,
            binding.tvPercentage3,
            binding.tvPercentage4
        )

        val genreImageViews = listOf(
            binding.ivRepresentGenre1,
            binding.ivRepresentGenre2,
            binding.ivRepresentGenre3,
            binding.ivRepresentGenre4
        )

        val circleImageViews = listOf(
            binding.ivChartCircle1,
            binding.ivChartCircle2,
            binding.ivChartCircle3,
            binding.ivChartCircle4
        )

        for (i in genreTextViews.indices) {
            if (i < genres.size) {
                genreTextViews[i].text = genres[i].genre
                genrePercentageTextViews[i].text =
                    "${(genres[i].count.toFloat() / genres.sumOf { it.count } * 100).toInt()}%"
                Glide.with(requireContext())
                    .load("${BuildConfig.SERVER_URL}static/genre/${ApplicationClass.KOREAN_TO_ENGLISH_MAP[genres[i].genre]}.jpg")
                    .error(R.drawable.temp_represent1)
                    .circleCrop()
                    .into(genreImageViews[i])
                genreTextViews[i].visibility = View.VISIBLE
                genrePercentageTextViews[i].visibility = View.VISIBLE
                genreImageViews[i].visibility = View.VISIBLE
                circleImageViews[i].visibility = View.VISIBLE
            } else {
                genreTextViews[i].visibility = View.GONE
                genrePercentageTextViews[i].visibility = View.GONE
                genreImageViews[i].visibility = View.GONE
                circleImageViews[i].visibility = View.GONE
            }
        }
    }

    private fun getRepresentArtists(genre: String) {
        portfolioViewModel.getRepresentArtists(genre)
    }

    private fun initUserData() {
        CoroutineScope(Dispatchers.Main).launch {
            mainActivityViewModel.getLoginData().collect { userData ->
                userData?.let {
                    portfolioViewModel.getPortfolio(it.memberId)
                    userId = it.memberId
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userId >= 0) {
            portfolioViewModel.getPortfolio(userId)
        }
    }

}