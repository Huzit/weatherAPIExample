package com.weather.weatherapitest.retrofit

import com.weather.weatherapitest.retrofit.ApiKey.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=${API_KEY}")
    suspend fun getWeather(
        @Query("dataType") dataType: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("base_date") baseData: Int,
        @Query("base_time") baseTime: Int,
        @Query("nx") nx: String,
        @Query("ny") ny: String
    ) : Response<WeatherResponse>
}