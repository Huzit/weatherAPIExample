package com.weather.weatherapitest.retrofit

import retrofit2.http.Header
import java.util.Date


data class WeatherResponse(val response: Response) {
    data class Response(
        val header: Header,
        val body: Body
    )

    data class Header(
        val resultCode: Int,
        val resultMsg: String
    )

    data class Body(
        val dataType: String,
        val items: Items
    )

    data class Items(
        val item: List<Item>
    )

    data class Item(
        val baseData: Int,
        val baseTime: Int,
        val category: String,
        val fcstDate: Int,
        val fcstTime: String,
        val fcstValue: String,
        val nx: Int,
        val ny: Int
    )
}