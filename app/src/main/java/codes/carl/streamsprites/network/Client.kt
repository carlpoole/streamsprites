package codes.carl.streamsprites.network

import android.util.Log
import codes.carl.streamsprites.model.SpriteData
import codes.carl.streamsprites.model.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object Client {
    //private const val BASE_URL = "http://137.184.18.115:3000/"
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: SpriteService by lazy {
        Client.retrofit.create(SpriteService::class.java)
    }

    fun fetchTestData() {
        apiService.test().enqueue(object : Callback<Test> {
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val testResponse = response.body()
                    Log.d("ApiClient", "Response: ${testResponse?.message}")
                } else {
                    // Handle request errors depending on status code
                    Log.d("ApiClient", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Test>, t: Throwable) {
                // Handle failure to execute the request, e.g., no internet, server down
                Log.d("ApiClient", "Error: ${t.message}")
            }
        })
    }

    fun putData(dataType: String, data: String, twitchUserId: String) {
        val requestData = SpriteData(dataType, data, twitchUserId)
        apiService.putData(requestData).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Log.d("ApiClient", "Data submitted successfully")
                } else {
                    Log.d("ApiClient", "Submission failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("ApiClient", "Error: ${t.message}")
            }
        })
    }

    fun getLatestSpeed(twitchUserId: String) {
        apiService.getLatestSpeed(twitchUserId).enqueue(object : Callback<SpriteData> {
            override fun onResponse(call: Call<SpriteData>, response: Response<SpriteData>) {
                if (response.isSuccessful) {
                    val spriteData = response.body()
                    Log.d("ApiClient", "Received data: $spriteData")
                } else {
                    Log.d("ApiClient", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SpriteData>, t: Throwable) {
                Log.d("ApiClient", "Error: ${t.message}")
            }
        })
    }
}