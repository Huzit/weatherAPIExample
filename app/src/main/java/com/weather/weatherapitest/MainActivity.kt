package com.weather.weatherapitest

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.weather.weatherapitest.databinding.ActivityMainBinding
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: WeatherViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ,1
        )



        binding.weather.setOnClickListener {
            setWeatherView()
        }
    }


    override fun onStart() {
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeatherView(){
        val now = LocalDateTime.now().toString()
        val baseDate = now.split("T")[0].filter { it != '-' }.toInt()
        val baseTime = (now.split("T")[1].substring(0..1) + "00").toInt()
        var nx = 0.0 //경도 x
        var ny = 0.0 //위도 y

        val locationManager = this.getSystemService(LocationManager::class.java)
        var location: Location?

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            nx = location?.latitude ?: 129.0
            ny = location?.longitude ?: 35.0
        }
        else {
            nx = 129.0
            ny = 35.0
        }
        //todo 위도 경도를 그리드로 변경하기
        val grid = convertGRID_GPS(0, nx, ny)
        Log.d("currentLocation", grid.first + " " + grid.second)
        Log.d("기본 시간", "${baseDate} / $baseTime")

        vm.getWeather("JSON", 60, 1, baseDate, baseTime, grid.first.toString(), grid.second.toString())
        vm.weatherResponse.observe(this){
            for(i in it.body()!!.response.body.items.item.filter { it.fcstTime == baseTime + 100 })
                Log.d("weather is ", i.toString())
        }
    }

    //위도경도 -> 격자 값
    fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): Pair<String, String>{

        var gx = 0.0
        var gy = 0.0
        var glat = 0.0
        var glng = 0.0

        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        if (mode == 0) {
            glat = lat_X
            glng = lng_Y
            var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta: Double = lng_Y * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            gx = Math.floor(ra * Math.sin(theta) + XO + 0.5)
            gy = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)

            return (gx.toInt().toString() to gy.toInt().toString())
        }

        else{
            gx = lat_X
            gy = lng_Y
            val xn = lat_X - XO
            val yn: Double = ro - lng_Y + YO
            var ra = Math.sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = Math.pow(re * sf / ra, 1.0 / sn)
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5

            var theta = 0.0
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = Math.atan2(xn, yn)
            }
            val alon = theta / sn + olon
            glat = alat * RADDEG
            glng = alon * RADDEG

            return (glat.toInt().toString() to glng.toInt().toString())
        }
    }
}