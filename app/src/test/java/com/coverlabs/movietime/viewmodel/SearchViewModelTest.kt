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
class SearchViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var storageRepository: StorageRepository

    @Mock
    private lateinit var movieListObserver: Observer<State<List<Movie>>>

    @Captor
    private lateinit var movieListCaptor: ArgumentCaptor<State<List<Movie>>>

    @Mock
    private lateinit var genreListObserver: Observer<State<List<String>>>

    @Captor
    private lateinit var genreListCaptor: ArgumentCaptor<State<List<String>>>

    @InjectMocks
    private lateinit var viewModel: SearchViewModel

    override fun setup() {
        super.setup()
        viewModel = SearchViewModel(movieRepository, storageRepository)
    }

    @Test
    fun getAllMovies_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = emptyList<Movie>()
            stubSearchMovies(expectedValue)

            viewModel.getAllMovies()

            verify(movieRepository).searchMovies()
        }
    }

    @Test
    fun getAllMovies_answersCorrectLiveData() {
        viewModel.onMovieListResult().observeForever(movieListObserver)

        testCoroutineRule.runBlockingTest {
            pauseDispatcher()
            val expectedValue = emptyList<Movie>()
            stubSearchMovies(expectedValue)

            viewModel.getAllMovies()

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

    @Test
    fun searchMovie_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = emptyList<Movie>()
            stubSearchMovies(expectedValue)

            viewModel.searchMovie()

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

            viewModel.searchMovie()

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

    @Test
    fun getGenreList_callsCorrectRepository() {
        testCoroutineRule.runBlockingTest {
            val expectedValue = emptyList<String>()
            stubGenreList(expectedValue)

            viewModel.getGenreList()

            verify(movieRepository).getGenreList()
        }
    }

    @Test
    fun getGenreList_answersCorrectLiveData() {
        viewModel.onGenreListResult().observeForever(genreListObserver)

        testCoroutineRule.runBlockingTest {
            pauseDispatcher()
            val expectedValue = emptyList<String>()
            stubGenreList(expectedValue)

            viewModel.getGenreList()

            genreListCaptor.run {
                verify(genreListObserver, times(1)).onChanged(capture())
                assert(value.status == LOADING)
                assert(value.data == null)
                assert(value.error == null)

                resumeDispatcher()

                verify(genreListObserver, times(2)).onChanged(capture())
                assert(value.status == SUCCESS)
                assert(value.data == expectedValue)
                assert(value.error == null)
            }
        }

        viewModel.onGenreListResult().removeObserver(genreListObserver)
    }

    private suspend fun stubSearchMovies(expectedValue: List<Movie>) {
        `when`(movieRepository.searchMovies()).thenReturn(expectedValue)
    }

    private suspend fun stubGenreList(expectedValue: List<String>) {
        `when`(movieRepository.getGenreList()).thenReturn(expectedValue)
    }
}