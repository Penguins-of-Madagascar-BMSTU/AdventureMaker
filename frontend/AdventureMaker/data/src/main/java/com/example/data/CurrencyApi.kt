package com.example.data

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {

    @GET("v6/latest/{base}")
    suspend fun getRates(
        @Path("base") base: String
    ): RatesResponse
}