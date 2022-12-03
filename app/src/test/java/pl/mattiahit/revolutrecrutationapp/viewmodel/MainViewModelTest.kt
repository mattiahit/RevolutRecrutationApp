package pl.mattiahit.revolutrecrutationapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import pl.mattiahit.revolutrecrutationapp.model.CurrencyResponse
import pl.mattiahit.revolutrecrutationapp.model.Rates
import pl.mattiahit.revolutrecrutationapp.repository.MainRepository
import pl.mattiahit.revolutrecrutationapp.utils.DispatcherProvider
import pl.mattiahit.revolutrecrutationapp.utils.Resource

class MainViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var mainRepository = mockk<MainRepository>()
    private var dispatcherProvider = mockk<DispatcherProvider>()

    private var SUT: MainViewModel = MainViewModel(
        mainRepository,
        dispatcherProvider
    )


    @Before
    fun setup() {
        every { dispatcherProvider.io } answers {
            Dispatchers.Unconfined
        }
    }

    @Test
    fun convertTest_emptyValue_failureResponse() {
        runBlocking {
            SUT.converts("", "PLN")
        }
        Assert.assertTrue(SUT.conversion.value is MainViewModel.CurrencyEvent.FailureId)
    }

    @Test
    fun convertTest_repositoryReturnsError_failureResponse() {
        coEvery { mainRepository.getRates() } answers {
            Resource.Error("error")
        }
        runBlocking {
            SUT.converts("1", "PLN")
        }
        Assert.assertTrue(SUT.conversion.value is MainViewModel.CurrencyEvent.Failure)
    }

    @Test
    fun convertTest_repositoryReturnsSuccess_successResponseWithValue() {
        coEvery { mainRepository.getRates() } answers {
            Resource.Success(getSuccessCurrencyResponse())
        }
        runBlocking {
            SUT.converts("1", "PLN")
        }
        Assert.assertTrue(SUT.conversion.value is MainViewModel.CurrencyEvent.Success)
        Assert.assertTrue((SUT.conversion.value as MainViewModel.CurrencyEvent.Success).resultText.isNotEmpty())
    }

    private fun getSuccessCurrencyResponse(): CurrencyResponse {
        return CurrencyResponse(
            "EUR",
            "2022-12-03",
            Rates(1.054085, 1, 4.690377),
            true,
            1670100994
        )
    }
}
