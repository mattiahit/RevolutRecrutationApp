package pl.mattiahit.revolutrecrutationapp.repository

import android.util.Log
import pl.mattiahit.revolutrecrutationapp.model.CurrencyResponse
import pl.mattiahit.revolutrecrutationapp.rest.CurrencyApi
import pl.mattiahit.revolutrecrutationapp.utils.Resource
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {
    override suspend fun getRates(): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates()
            if(response.success) {
                Resource.Success(response)
            } else {
                Resource.Error("An error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}