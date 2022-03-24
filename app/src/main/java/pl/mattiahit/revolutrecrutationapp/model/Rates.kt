package pl.mattiahit.revolutrecrutationapp.model

import com.google.gson.annotations.SerializedName

data class Rates(@SerializedName("USD")
                 val usd: Double = 0.0,
                 @SerializedName("EUR")
                 val eur: Int = 0,
                 @SerializedName("PLN")
                 val pln: Double = 0.0,
)
