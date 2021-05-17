package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Observer
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
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
class GenreListViewModelTest : BaseCoroutineTest() {

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var genreListObserver: Observer<State<List<String>>>

    @Captor
    private lateinit var genreListCaptor: ArgumentCaptor<State<List<String>>>

    @InjectMocks
    private lateinit var viewModel: GenreListViewModel

    override fun setup() {
        super.setup()
        viewModel = GenreListViewModel(movieRepository)
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

    private suspend fun stubGenreList(expectedValue: List<String>) {
        `when`(movieRepository.getGenreList()).thenReturn(expectedValue)
    }
}