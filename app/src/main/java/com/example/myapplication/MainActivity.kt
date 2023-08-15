package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Meerut")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Implement your logic for query text submission here.
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implement your logic for query text change here.
                return true
            }
        })
    }


    private fun fetchWeatherData(cityName:String){
        val retrofit=Retrofit.Builder().
        addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"71c0508e0453f809e013f99a31a9eceb","metric" )

        response.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.sea_level
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val windSpeed=responseBody.wind.speed
                    val maxtemp=responseBody.main.temp_max
                    val mintemp=responseBody.main.temp_min

                    binding.tempId.text="$temperature °C"
                    binding.maxtempId.text="Max-temp: $maxtemp °C"
                    binding.mintempId.text="Min-temp: $mintemp °C"
                    binding.condId.text="$condition"
                    binding.animecondId.text="$condition"
                    binding.humidityId.text="$humidity %"
                    binding.windId.text="$windSpeed m/s"
                    binding.seaId.text="$seaLevel hPa"
                    binding.sunriseId.text="${time(sunRise)} AM"
                    binding.sunsetId.text="${time(sunSet)} PM"
                    binding.dayId.text=dayName(System.currentTimeMillis())
                    binding.dateId.text=date()
                    binding.locaionId.text="$cityName"

                    changeImage(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImage(conditions: String) {
        when(conditions){
            "Clear Sky","Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource (R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}


