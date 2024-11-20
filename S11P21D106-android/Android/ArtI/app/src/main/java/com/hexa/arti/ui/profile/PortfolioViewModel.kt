package com.hexa.arti.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.portfolio.PortfolioGenre
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.repository.ArtistRepository
import com.hexa.arti.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private val _resultGenres = MutableLiveData<List<PortfolioGenre>>()
    val resultGenres = _resultGenres

    private val _resultArtists = MutableLiveData<List<Artist>>()
    val resultArtist = _resultArtists


    fun getPortfolio(memberId: Int) {
        viewModelScope.launch {
            homeRepository.getPortfolio(memberId).onSuccess {
                _resultGenres.value = it
            }.onFailure {
                _resultGenres.value = emptyList()
            }
        }
    }

    fun getRepresentArtists(genre: String){
        viewModelScope.launch {
            artistRepository.getRepresentArtists(genre).onSuccess {
                _resultArtists.value = it
            }.onFailure {
                _resultGenres.value = emptyList()
            }
        }
    }


}