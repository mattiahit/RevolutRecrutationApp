package pl.mattiahit.revolutrecrutationapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.mattiahit.revolutrecrutationapp.R
import pl.mattiahit.revolutrecrutationapp.model.Rates
import pl.mattiahit.revolutrecrutationapp.repository.MainRepository
import pl.mattiahit.revolutrecrutationapp.utils.DispatcherProvider
import pl.mattiahit.revolutrecrutationapp.utils.Resource
import java.lang.Math.round
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        class FailureId(val errorTextId: Int): CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun converts(
        amountStr: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if(fromAmount == null) {
            _conversion.value = CurrencyEvent.FailureId(R.string.not_a_valid_amount)
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when(val ratesResponse = repository.getRates()) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    if(!ratesResponse.data!!.success) {
                        _conversion.value = CurrencyEvent.FailureId(R.string.unexpected_error)
                    } else {
                        val rates = ratesResponse.data.rates
                        val rate = getRateForCurrency(toCurrency, rates)
                        if(rate == null) {
                            _conversion.value = CurrencyEvent.FailureId(R.string.unexpected_error)
                        } else {
                            val convertedCurrency = round(fromAmount * rate as Double * 100) / 100
                            _conversion.value = CurrencyEvent.Success(
                                "$fromAmount EUR = $convertedCurrency $toCurrency"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "PLN" -> rates.pln
        "USD" -> rates.usd
        "EUR" -> rates.eur
        else -> null
    }
}