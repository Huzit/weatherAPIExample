package com.weather.weatherapitest.retrofit

import com.google.gson.GsonBuilder
import com.weather.weatherapitest.retrofit.Constants.Companion.BASE_URL
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCall {
    var rtBuilder: Retrofit
    lateinit var provideWeatherApi: WeatherApi
    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        rtBuilder = retrofit2
            .Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()

        provideWeatherApi = rtBuilder.create(WeatherApi::class.java)
    }

    suspend fun getWeather(dataType: String, numOfRows: Int, pageNo: Int, baseDate: Int, baseTime: Int, nx: String, ny: String) : Response<WeatherResponse>{
        return provideWeatherApi.getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
    }
}