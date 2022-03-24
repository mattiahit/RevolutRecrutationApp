package pl.mattiahit.revolutrecrutationapp.rest

import pl.mattiahit.revolutrecrutationapp.model.CurrencyResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/latest?access_key=3883a79985014929f6ba349ec6b5e757&format=1")
    suspend fun getRates(): CurrencyResponse
}