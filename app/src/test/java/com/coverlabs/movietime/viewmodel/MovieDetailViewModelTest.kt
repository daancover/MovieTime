package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Observer
import com.coverlabs.domain.model.MovieDetails
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
class MovieDetailViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var storageRepository: StorageRepository

    @Mock
    private lateinit var movieListObserver: Observer<State<MovieDetails>>

    @Captor
    private lateinit var movieListCaptor: ArgumentCaptor<State<MovieDetails>>

    @InjectMocks
    private lateinit var viewModel: MovieDetailViewModel

    override fun setup() {
        super.setup()
        viewModel = MovieDetailViewModel(movieRepository, storageRepository)
    }

    @Test
    fun getMovieDetails_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = mock(MovieDetails::class.java)
            stubGetMovieDetails(expectedValue)

            viewModel.getMovieDetails(1)

            verify(movieRepository).getMovieDetails(anyInt())
        }
    }

    @Test
    fun searchMovie_answersCorrectLiveData() {
        viewModel.onMovieDetailsResult().observeForever(movieListObserver)

        testCoroutineRule.runBlockingTest {
            pauseDispatcher()
            val expectedValue = mock(MovieDetails::class.java)
            stubGetMovieDetails(expectedValue)

            viewModel.getMovieDetails(1)

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

        viewModel.onMovieDetailsResult().removeObserver(movieListObserver)
    }

    private suspend fun stubGetMovieDetails(expectedValue: MovieDetails) {
        `when`(movieRepository.getMovieDetails(anyInt())).thenReturn(expectedValue)
    }
}