# 기상청 단기예보 조회 서비스 오픈 API활용 예제입니다.

# !!주의!!

src/.../retrofit/ApiKey.kt 파일은 커밋하지 않았습니다. 기상청 단기예보 신청하신 뒤에 계정으로 발급한 APIkey를 넣어주면 됩니다. 변수명은 API_KEY 입니다.

## 의존성

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
    implementation 'androidx.activity:activity-ktx:1.7.0'
    implementation 'androidx.fragment:fragment-ktx:1.5.6'
    
## 권한
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET"/>

## 사용된 라이브러리 및 API

* Retrofit2
* 기상청 단기예보 조회

## 정의된 기능

* 단기예보 조회 CallBack
* 휴대폰 위도, 경도를 격자 x, y로 변환
* 버튼을 누르면 로그를 통해 1시간 단위로 기상청 초단기 예보가 나옴


변수명과 항목 값이 어떤 정보를 나타내는지는 활용 가이드 문서를 보면 알 수 있습니다.

[기상청 단기예보 바로가기](https://www.data.go.kr/data/15084084/openapi.do)
