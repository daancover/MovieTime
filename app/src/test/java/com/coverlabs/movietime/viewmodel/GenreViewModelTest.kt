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
class GenreViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var storageRepository: StorageRepository

    @Mock
    private lateinit var movieListObserver: Observer<State<List<Movie>>>

    @Captor
    private lateinit var movieListCaptor: ArgumentCaptor<State<List<Movie>>>

    @InjectMocks
    private lateinit var viewModel: GenreViewModel

    override fun setup() {
        super.setup()
        viewModel = GenreViewModel(movieRepository, storageRepository)
    }

    @Test
    fun searchMovie_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = emptyList<Movie>()
            stubSearchMovies(expectedValue)

            viewModel.searchMovie("")

            verify(movieRepository).searchMovies()
        }
    }

    @Test
    fun searchMovie_answersCorrectLiveData() {
        viewModel.onMovieListResult().observeForever(movieListObserver)

        testCoroutineRule.runBlockingTest {
            pauseDispatcher()
            val expectedValue = emptyList<Movie>()
            stubSearchMovies(expectedValue)

            viewModel.searchMovie("")

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

    private suspend fun stubSearchMovies(expectedValue: List<Movie>) {
        `when`(movieRepository.searchMovies()).thenReturn(expectedValue)
    }
}