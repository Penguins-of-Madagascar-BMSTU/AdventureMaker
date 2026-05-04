package com.example.data.api

import com.example.data.api.dto.RatesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {

    @GET("v6/latest/{base}")
    suspend fun getRates(
        @Path("base") base: String
    ): RatesResponse
}