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
        baseDate: String, baseTime: String, nx: String, ny: String
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
                if (response.isSuccessful) {
                    Log.d("성공", response.message())
                    _weatherResponseResponse.value = response
                    _weatherResponseResponse = MutableLiveData<Response<WeatherResponse>>()
                } else
                    Log.d("에러", response.message())
            } catch (e: java.lang.NullPointerException){
                Log.e(javaClass.simpleName, "현재 시간의 기상정보를 찾을 수 없습니다. 시간을 다시 확인해주세요")
            }
        }
    }
}