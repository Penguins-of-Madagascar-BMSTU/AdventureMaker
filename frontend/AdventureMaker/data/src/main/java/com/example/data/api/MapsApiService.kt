package com.example.data.api

import com.example.data.api.dto.RegionsResponse
import com.example.data.api.dto.SearchPlacesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApiService {

    @GET("3.0/items")
    suspend fun loadPlaces(
        @Query("q") query: String,
        @Query("locale") locale: String,
        @Query("type") categoryAlias: String,
        @Query("city_id") cityId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("fields") extraFields: String = "items.description,items.name,items.address,items.point,items.rubrics,items.photos",
    ): SearchPlacesResponseDto

    @GET("2.0/region/search")
    suspend fun searchCity(
        @Query("q") query: String
    ): RegionsResponse
}