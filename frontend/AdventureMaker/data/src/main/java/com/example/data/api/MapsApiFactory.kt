package com.example.data.api

import com.example.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Locale

object MapsApiFactory {

    private const val BASE_URL = "https://catalog.api.2gis.com/"
    private const val KEY_PARAM = "key"
    private const val LOCALE_PARAM = "locale"
    private const val DEFAULT_LOCALE = "en_RU"

    private val AVAILABLE_LOCALES = listOf(
        "az_AZ", "ru_AZ", "hy_AM", "ru_AM", "ar_BH", "en_BH", "ru_BY", "ka_GE", "ru_GE", "en_EG",
        "ar_EG", "it_IT", "en_IQ", "ar_IQ", "kk_KZ", "ru_KZ", "en_QA", "ar_QA", "en_CY", "es_CL",
        "ky_KG", "ru_KG", "en_CN", "zh_CN", "ru_CN", "en_KW", "ar_KW", "en_MA", "ar_MA", "mn_MN",
        "en_MN", "en_AE", "ar_AE", "en_OM", "ar_OM", "en_RU", "ar_RU", "es_RU", "it_RU", "ru_RU",
        "uk_RU", "cs_RU", "en_SA", "ar_SA", "ru_TJ", "tg_TJ", "ru_UZ", "uz_UZ", "ru_UA", "uk_UA",
        "cs_CZ"
    )

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalReq = chain.request()
            val newUrl = originalReq.url.newBuilder()
                .addQueryParameter(KEY_PARAM, BuildConfig.API_KEY_2GIS)
                .addQueryParameter(LOCALE_PARAM, getLocale())
                .build()
            val newReq = originalReq.newBuilder().url(newUrl).build()
            chain.proceed(newReq)
        }.addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        ).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: MapsApiService = retrofit.create()

    private fun getLocale(): String {
        val locale = "${Locale.getDefault().language}_${Locale.getDefault().country}"
        return if (locale in AVAILABLE_LOCALES)
            locale
        else
            DEFAULT_LOCALE
    }
}