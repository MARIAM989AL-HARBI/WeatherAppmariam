package com.example.mariam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*                   // .. هناك خطا في ملف app-src-main>>drawble:(
                                     //
                                               //"name":"Garden City"
                                                 //zip code ="94040"
                                          //Apl key bb77a60b6095ce599cabedc35c46e26f
class MainActivity : AppCompatActivity() {

    private lateinit var descriptiontext:TextView    // TextView
    private lateinit var countrytext:TextView          // TextView
    private lateinit var datetext:TextView             // TextView
    private lateinit var temptext:TextView             // TextView
    private lateinit var temp_mintext:TextView         // TextView
    private lateinit var temp_maxtext:TextView        // TextView
    private lateinit var zipcode:EditText   // هنا كود تبع المنطقة ..editText
    private lateinit var weatherbtn:Button    // Boutton
    private lateinit var backbutton:Button      // Boutton
    private lateinit var showerror:TextView        // TextView
    private lateinit var pressuretext:TextView     // TextView
    private lateinit var humiditytext:TextView   // TextView
    private lateinit var refreshtext:TextView   // TextView
    private lateinit var sunrisetext:TextView   // TextView
    private lateinit var sunsettext:TextView   // TextView
    private lateinit var windtext:TextView   // TextView


    private var zip: String ="11599"   //zip code

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backbutton=findViewById(R.id.btn)   // id تبع كل واحد فيهم
        showerror=findViewById(R.id.textView)
        countrytext=findViewById(R.id.countryid)
        pressuretext=findViewById(R.id.pressureid)
        humiditytext=findViewById(R.id.humidity)
        refreshtext=findViewById(R.id.refreshid)
        sunrisetext=findViewById(R.id.sunriseid)
        sunsettext=findViewById(R.id.sunsetid)
        windtext=findViewById(R.id.windid)
        descriptiontext = findViewById(R.id.descriptionid)
        datetext= findViewById(R.id.dateid)
        temptext=findViewById(R.id.tempid)
        temp_maxtext=findViewById(R.id.maxid)
        temp_mintext=findViewById(R.id.minid)
        zipcode=findViewById(R.id.zipcode)
        weatherbtn = findViewById(R.id.button)



        backbutton.setVisibility(View.GONE);
        showerror.setVisibility(View.GONE);
        weatherbtn.setVisibility(View.GONE);
        zipcode.setVisibility(View.GONE);
        requestApi()

        countrytext.setOnClickListener(){
            visibility()
            weatherbtn.setVisibility(View.VISIBLE);
            zipcode.setVisibility(View.VISIBLE);}

        weatherbtn.setOnClickListener {

            zip=zipcode.text.toString()                  // zip >>String
            if(zip.isEmpty()){                                // zip >> is Empty
                backbutton.setVisibility(View.VISIBLE);
                showerror.setVisibility(View.VISIBLE);
                weatherbtn.setVisibility(View.GONE);
                zipcode.setVisibility(View.GONE);

                backbutton.setOnClickListener {
                    backbutton.setVisibility(View.GONE);
                    showerror.setVisibility(View.GONE);
                    visibility()
                    weatherbtn.setVisibility(View.VISIBLE);
                    zipcode.setVisibility(View.VISIBLE);
                }
            }
            else{requestApi()}

        }




        refreshtext.setOnClickListener {
            requestApi()
        }

    }

    private fun requestApi()                      //  API request allows you to retrieve data
    {                                                  // from a data source, or to send data.

        CoroutineScope(Dispatchers.IO).launch {

            val data = async {

                fetchWeather()



            }.await()

            if (data.isNotEmpty())
            {

                updateWeather(data)
            }

        }

    }

    private suspend fun fetchWeather():String {
        updateStatus(0)
        var response = ""
        try {
            updateStatus(1)
            response =
                URL("https://api.openweathermap.org/data/2.5/weather?zip=$zip,us&appid=bb77a60b6095ce599cabedc35c46e26f").readText(
                    Charsets.UTF_8
                )
            println(response)

        } catch (e: Exception) {
            updateStatus(-1)
            println("Error:$e")
        }
        return response
    }
    private suspend fun updateWeather(data: String) {
        withContext(Dispatchers.Main)
        {

            val jsonObject = JSONObject(data)
            var country = jsonObject.getJSONObject("sys")
            var tem = jsonObject.getJSONObject("main")
            var date = jsonObject.getLong("dt")
            var wind = jsonObject.getJSONObject("wind")
            var windview = wind.getInt("speed")
            var name = jsonObject.getString("name")
            val convertdate = SimpleDateFormat("M/ d/y , hh:mm a", Locale.US).format(date*1000)
            var countryview = country.getString("country")
            val sunrise = country.getLong("sunrise")
            val sunriseview = SimpleDateFormat(" hh:mm a", Locale.US).format(sunrise*1000)
            val sunset = country.getLong("sunset")
            val sunsetview = SimpleDateFormat(" hh:mm a", Locale.US).format(sunset*1000)
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
            val description = weather.getString("description")
            val currentTemp = tem.getInt("temp")
            val humidityview = tem.getInt("humidity")
            val temp_min = tem.getInt("temp_min")
            val pressure = tem.getString("pressure")
            val temp_max = tem.getInt("temp_max")
            countrytext.text = "$name, $countryview"
            descriptiontext.text = description
            datetext.text = "Updated at :$convertdate"
            temptext.text = "${(currentTemp - 273.15).toInt()}°C"
            temp_maxtext.text = "High:${(temp_max - 273.15).toInt()}°C"
            temp_mintext.text = "Low:${(temp_min - 273.15).toInt()}°C"
            pressuretext.text = "Pressur \n $pressure"
            sunrisetext.text = "Sunrise \n $sunriseview"
            sunsettext.text = "Sunrise \n $sunsetview"
            windtext.text = "Wind \n $windview"
            humiditytext.text = "Humidity\n $humidityview"
            refreshtext.text = "Refresh\nData"

        }

    }

    private suspend fun updateStatus(state: Int){
//        states: -1 = loading, 0 = loaded, 1 = error
        withContext(Dispatchers.Main){
            when{
                state < 0 -> {
                    backbutton.setVisibility(View.VISIBLE);
                    showerror.setVisibility(View.VISIBLE);
                    weatherbtn.setVisibility(View.GONE);
                    zipcode.setVisibility(View.GONE);
                    visibility()



                }
                state == 0 -> {
                    gone()
                    weatherbtn.setVisibility(View.GONE);
                    zipcode.setVisibility(View.GONE);
                    backbutton.setVisibility(View.GONE);
                    showerror.setVisibility(View.GONE);
                }
                state > 0 -> {
                    gone()
                    weatherbtn.setVisibility(View.GONE);
                    zipcode.setVisibility(View.GONE);
                    backbutton.setVisibility(View.GONE);
                    showerror.setVisibility(View.GONE);

                }
            }
        }
    }
    private fun visibility(){
        countrytext.setVisibility(View.GONE);
        datetext.setVisibility(View.GONE);
        descriptiontext.setVisibility(View.GONE);
        temptext.setVisibility(View.GONE);
        temp_mintext.setVisibility(View.GONE);
        temp_maxtext.setVisibility(View.GONE);
        pressuretext.setVisibility(View.GONE);
        humiditytext.setVisibility(View.GONE);
        refreshtext.setVisibility(View.GONE);
        sunsettext.setVisibility(View.GONE);
        sunrisetext.setVisibility(View.GONE);
        windtext.setVisibility(View.GONE);
    }
    fun gone(){
        countrytext.setVisibility(View.VISIBLE);
        datetext.setVisibility(View.VISIBLE);
        descriptiontext.setVisibility(View.VISIBLE);
        temptext.setVisibility(View.VISIBLE);
        temp_mintext.setVisibility(View.VISIBLE);
        temp_maxtext.setVisibility(View.VISIBLE);
        pressuretext.setVisibility(View.VISIBLE);
        humiditytext.setVisibility(View.VISIBLE);
        refreshtext.setVisibility(View.VISIBLE);
        sunsettext.setVisibility(View.VISIBLE);
        sunrisetext.setVisibility(View.VISIBLE);
        windtext.setVisibility(View.VISIBLE);

    }
}


