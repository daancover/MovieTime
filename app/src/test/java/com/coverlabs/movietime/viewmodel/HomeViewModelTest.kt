package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Observer
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.core.BaseCoroutineTest
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.LOADING
import com.coverlabs.movietime.viewmodel.base.State.Status.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class HomeViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var storageRepository: StorageRepository

    @Mock
    private lateinit var movieListObserver: Observer<State<List<Movie>>>

    @Captor
    private lateinit var movieListCaptor: ArgumentCaptor<State<List<Movie>>>

    @InjectMocks
    private lateinit var viewModel: HomeViewModel

    override fun setup() {
        super.setup()
        viewModel = HomeViewModel(movieRepository, storageRepository)
    }

    @Test
    fun getTopFiveMovies_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = emptyList<Movie>()
            stubGetTopFiveMovies(expectedValue)

            viewModel.getTopFiveMovies()

            verify(movieRepository).getTopFiveMovies()
        }
    }

    @Test
    fun getTopFiveMovies_answersCorrectLiveData() {
        viewModel.onMovieListResult().observeForever(movieListObserver)

        testCoroutineRule.runBlockingTest {
            pauseDispatcher()
            val expectedValue = emptyList<Movie>()
            stubGetTopFiveMovies(expectedValue)

            viewModel.getTopFiveMovies()

            movieListCaptor.run {
                verify(movieListObserver, times(1)).onChanged(capture())
                assert(value.status == LOADING)
                assert(value.data == null)
                assert(value.error == null)

                resumeDispatcher()

                verify(movieListObserver, times(2)).onChanged(capture())
                assert(value.status == SUCCESS)
                assert(value.data == expectedValue)
                assert(value.error == null)
            }
        }

        viewModel.onMovieListResult().removeObserver(movieListObserver)
    }

    private suspend fun stubGetTopFiveMovies(expectedValue: List<Movie>) {
        `when`(movieRepository.getTopFiveMovies()).thenReturn(expectedValue)
    }
}