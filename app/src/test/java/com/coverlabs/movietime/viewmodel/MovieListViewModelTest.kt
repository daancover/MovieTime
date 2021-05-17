package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Observer
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.core.BaseCoroutineTest
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class MovieListViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var storageRepository: StorageRepository

    @Mock
    private lateinit var favoriteMovieListObserver: Observer<State<List<Movie>>>

    @Captor
    private lateinit var favoriteMovieListCaptor: ArgumentCaptor<State<List<Movie>>>

    @InjectMocks
    private lateinit var viewModel: MovieListViewModel

    override fun setup() {
        super.setup()
        viewModel = MovieListViewModel(storageRepository)
    }

    @Test
    fun getFavorites_callsCorrectRepository() {
        val expectedValue = emptyList<Movie>()
        stubGetFavorites(expectedValue)

        viewModel.getFavorites()

        verify(storageRepository).getFavorites()
    }

    @Test
    fun getFavorites_answersCorrectLiveData() {
        viewModel.onFavoriteMovieListResult().observeForever(favoriteMovieListObserver)

        val expectedValue = emptyList<Movie>()
        stubGetFavorites(expectedValue)

        viewModel.getFavorites()

        favoriteMovieListCaptor.run {
            verify(favoriteMovieListObserver, times(2)).onChanged(capture())
            assert(value.status == SUCCESS)
            assert(value.data == expectedValue)
            assert(value.error == null)
        }

        viewModel.onFavoriteMovieListResult().removeObserver(favoriteMovieListObserver)
    }

    @Test
    fun changeFavoriteStatus_withIsFavoriteTrue_callsAddFavorite() {
        stubAddFavorites()

        viewModel.changeFavoriteStatus(true, mock(Movie::class.java))

        verify(storageRepository).addFavorite(any())
    }

    @Test
    fun changeFavoriteStatus_withIsFavoriteFalse_callsRemoveFavorite() {
        stubRemoveFavorites()

        viewModel.changeFavoriteStatus(false, mock(Movie::class.java))

        verify(storageRepository).removeFavorite(any())
    }

    @Test
    fun isFavorite_callsCorrectRepository() {
        stubIsFavorite(true)

        viewModel.isFavorite(mock(Movie::class.java))

        verify(storageRepository).isFavorite(any())
    }

    @Test
    fun isFavoriteList_callsCorrectRepository() {
        stubIsFavorite(true)
        val movieList = listOf(
            mock(Movie::class.java),
            mock(Movie::class.java),
            mock(Movie::class.java)
        )

        viewModel.isFavoriteList(movieList)

        verify(storageRepository, times(3)).isFavorite(any())
    }

    private fun stubGetFavorites(expectedValue: List<Movie>) {
        `when`(storageRepository.getFavorites()).thenReturn(expectedValue)
    }

    private fun stubAddFavorites() {
        `when`(storageRepository.addFavorite(any())).then {}
    }

    private fun stubRemoveFavorites() {
        `when`(storageRepository.removeFavorite(any())).then {}
    }

    private fun stubIsFavorite(expectedValue: Boolean) {
        `when`(storageRepository.isFavorite(any())).thenReturn(expectedValue)
    }
}