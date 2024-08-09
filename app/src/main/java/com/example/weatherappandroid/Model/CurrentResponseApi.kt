package com.example.weatherappandroid.Model


import com.google.gson.annotations.SerializedName

data class CurrentResponseApi(
    @SerializedName("base")
    val base: String?,
    @SerializedName("clouds")
    val clouds: Clouds?,
    @SerializedName("cod")
    val cod: Int?,
    @SerializedName("coord")
    val coord: Coord?,
    @SerializedName("dt")
    val dt: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("main")
    val main: Main?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("rain")
    val rain: Rain?,
    @SerializedName("sys")
    val sys: Sys?,
    @SerializedName("timezone")
    val timezone: Int?,
    @SerializedName("visibility")
    val visibility: Int?,
    @SerializedName("weather")
    val weather: List<Weather?>?,
    @SerializedName("wind")
    val wind: Wind?
) {
    data class Clouds(
        @SerializedName("all")
        val all: Int?
    )

    data class Coord(
        @SerializedName("lat")
        val lat: Double?,
        @SerializedName("lon")
        val lon: Double?
    )

    data class Main(
        @SerializedName("feels_like")
        val feelsLike: Double?,
        @SerializedName("grnd_level")
        val grndLevel: Int?,
        @SerializedName("humidity")
        val humidity: Int?,
        @SerializedName("pressure")
        val pressure: Int?,
        @SerializedName("sea_level")
        val seaLevel: Int?,
        @SerializedName("temp")
        val temp: Double?,
        @SerializedName("temp_max")
        val tempMax: Double?,
        @SerializedName("temp_min")
        val tempMin: Double?
    )

    data class Rain(
        @SerializedName("1h")
        val h: Double?
    )

    data class Sys(
        @SerializedName("country")
        val country: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("sunrise")
        val sunrise: Int?,
        @SerializedName("sunset")
        val sunset: Int?,
        @SerializedName("type")
        val type: Int?
    )

    data class Weather(
        @SerializedName("description")
        val description: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("main")
        val main: String?
    )

    data class Wind(
        @SerializedName("deg")
        val deg: Int?,
        @SerializedName("gust")
        val gust: Double?,
        @SerializedName("speed")
        val speed: Double?
    )
}


/*
- **coord**: Tọa độ vị trí
    - **coord.lon**: Kinh độ của vị trí
    - **coord.lat**: Vĩ độ của vị trí

- **weather**: Thời tiết
    - **weather.id**: Điều kiện thời tiết ID
    - **weather.main**: Nhóm các thông số thời tiết (Mưa, Tuyết, Mây, v.v.)
    - **weather.description**: Tình hình thời tiết trong nhóm
    - **weather.icon**: Biểu tượng thời tiết ID

- **base**: Tham số nội bộ

- **main**: Thông tin thời tiết chính
    - **main.temp**: Nhiệt độ (Kelvin, Celsius, Fahrenheit)
    - **main.feels_like**: Nhiệt độ cảm nhận (Kelvin, Celsius, Fahrenheit)
    - **main.pressure**: Áp suất khí quyển trên mực nước biển (hPa)
    - **main.humidity**: Độ ẩm (%)
    - **main.temp_min**: Nhiệt độ tối thiểu hiện tại (Kelvin, Celsius, Fahrenheit)
    - **main.temp_max**: Nhiệt độ tối đa hiện tại (Kelvin, Celsius, Fahrenheit)
    - **main.sea_level**: Áp suất khí quyển trên mực nước biển (hPa)
    - **main.grnd_level**: Áp suất khí quyển ở mặt đất (hPa)

- **visibility**: Tầm nhìn (mét)

- **wind**: Thông tin về gió
    - **wind.speed**: Tốc độ gió (mét/giây, dặm/giờ)
    - **wind.deg**: Hướng gió (độ)
    - **wind.gust**: Gió giật (mét/giây, dặm/giờ)

- **clouds**: Thông tin về mây
    - **clouds.all**: Độ mây (%)

- **rain**: Thông tin về mưa (nếu có)
    - **rain.1h**: Lượng mưa trong 1 giờ qua (mm)
    - **rain.3h**: Lượng mưa trong 3 giờ qua (mm)

- **snow**: Thông tin về tuyết (nếu có)
    - **snow.1h**: Lượng tuyết trong 1 giờ qua (mm)
    - **snow.3h**: Lượng tuyết trong 3 giờ qua (mm)

- **dt**: Thời gian tính toán dữ liệu (Unix, UTC)

- **sys**: Thông tin hệ thống
    - **sys.type**: Tham số nội bộ
    - **sys.id**: Tham số nội bộ
    - **sys.message**: Tham số nội bộ
    - **sys.country**: Mã quốc gia (GB, JP, v.v.)
    - **sys.sunrise**: Giờ mặt trời mọc (Unix, UTC)
    - **sys.sunset**: Thời gian hoàng hôn (Unix, UTC)

- **timezone**: Chuyển đổi theo giây từ UTC

- **id**: ID thành phố

- **name**: Tên thành phố

- **cod**: Tham số nội bộ


*/
