package com.weather.weatherapitest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapitest.retrofit.RetrofitCall
import com.weather.weatherapitest.retrofit.WeatherResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel() : ViewModel() {
    var repository = RetrofitCall()
    private var _weatherResponseResponse: MutableLiveData<Response<WeatherResponse>> = MutableLiveData()
    val weatherResponse
        get() = _weatherResponseResponse

    fun getWeather(
        dataType: String, numOfRows: Int, pageNo: Int,
        baseDate: Int, baseTime: Int, nx: String, ny: String
    ) {
        viewModelScope.launch {
            val response =
                repository.getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
            if(response.isSuccessful) {
                Log.d("성", "공")
                _weatherResponseResponse.value = response
                _weatherResponseResponse = MutableLiveData<Response<WeatherResponse>>()
            }
            else
                Log.d("dㅔ러", response.message())
        }
    }
}