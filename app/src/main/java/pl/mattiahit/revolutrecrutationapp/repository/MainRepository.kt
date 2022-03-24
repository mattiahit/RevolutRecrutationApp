package pl.mattiahit.revolutrecrutationapp.repository

import pl.mattiahit.revolutrecrutationapp.model.CurrencyResponse
import pl.mattiahit.revolutrecrutationapp.utils.Resource

interface MainRepository {
    suspend fun getRates(): Resource<CurrencyResponse>
}