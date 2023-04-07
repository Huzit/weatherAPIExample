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

    /*
    현재 분 단위가 30분 미만일 경우 직전 시간(-1) ~ 현재 시간의 기상 조회
    현재 분 단위가 30분 이후일 경우 현재 시간 ~ 다음 시간(+1) 기상 조회
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeatherView(){
        val now = LocalDateTime.now().toString()
        var baseDate = now.split("T")[0].filter { it != '-' }
        var baseTime = now.split("T")[1].substring(0..4).filter { it != ' ' && it != ':' }
        var endTime = ""
        var nx = 0.0 //경도 x
        var ny = 0.0 //위도 y
        val locationManager = this.getSystemService(LocationManager::class.java)
        var location: Location?
        val sb = StringBuilder()

        //현재 분 단위가 30분 이전
        if(baseTime.substring(2 .. 3).toInt() < 30){
            //00:30 이전일 시 어제 날짜 23시로 변경
            if(baseTime.substring(0..1) == "00") {
                baseDate = LocalDateTime.now().minusDays(1L).toString().split("T")[0].filter { it != '-' }
                baseTime = "2300"
            }
            //1시부터 23시사이 중 30분 이전일 경우
            else{
                sb.append(baseTime.substring(0..1))
                //직전 시간
                sb[1] = sb[1].minus(1)
                //분 추가
                sb.append("00")
                baseTime = sb.toString()
            }
        }else {//이후
            baseTime = baseTime.substring(0..1)+"00"
        }

        //검색 마지노 시간
        if(baseTime == "2300") {
            endTime = "0000"
        } else{
            sb.clear().append(baseTime)
            sb[1] = sb[1].plus(1)
            endTime = sb.toString()
        }

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

        val grid = convertGRID_GPS(0, nx, ny)
//        Log.d("currentLocation", grid.first + " " + grid.second)
        Log.d("기본 시간", "${baseDate} / $baseTime / $endTime")

        vm.getWeather("JSON", 60, 1, baseDate, baseTime, grid.first.toString(), grid.second.toString())
        vm.weatherResponse.observe(this){
            for(i in it.body()!!.response.body.items.item.filter { it.fcstTime.toString() == endTime })
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