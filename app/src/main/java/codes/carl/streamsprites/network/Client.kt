package codes.carl.streamsprites.network

import android.util.Log
import codes.carl.streamsprites.model.SpriteData
import codes.carl.streamsprites.model.Test
import codes.carl.streamsprites.viewmodels.ConnectionViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object Client {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient : KoinComponent {
    private val connectionViewModel: ConnectionViewModel by inject()

    val apiService: SpriteService by lazy {
        Client.retrofit.create(SpriteService::class.java)
    }

    fun fetchTestData() {
        apiService.test().enqueue(object : Callback<Test> {
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                if (response.isSuccessful) {
                    connectionViewModel.setConnectionStatus(true)

                    // Handle successful response
                    val testResponse = response.body()
                    Log.d("ApiClient", "Response: ${testResponse?.message}")
                } else {
                    // Handle request errors depending on status code
                    Log.d("ApiClient", "Error: ${response.code()}")
                    connectionViewModel.setConnectionStatus(false)
                }
            }

            override fun onFailure(call: Call<Test>, t: Throwable) {
                // Handle failure to execute the request, e.g., no internet, server down
                Log.d("ApiClient", "Error: ${t.message}")
                connectionViewModel.setConnectionStatus(false)
            }
        })
    }

    fun putData(dataType: String, data: String, twitchUserId: String) {
        val requestData = SpriteData(dataType, data, twitchUserId)
        apiService.putData(requestData).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    connectionViewModel.setConnectionStatus(true)

                    Log.d("ApiClient", "Data submitted successfully")
                } else {
                    Log.d("ApiClient", "Submission failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("ApiClient", "Error: ${t.message}")
                connectionViewModel.setConnectionStatus(false)
            }
        })
    }

    fun getLatestSpeed(twitchUserId: String) {
        apiService.getLatestSpeed(twitchUserId).enqueue(object : Callback<SpriteData> {
            override fun onResponse(call: Call<SpriteData>, response: Response<SpriteData>) {
                if (response.isSuccessful) {
                    connectionViewModel.setConnectionStatus(true)

                    val spriteData = response.body()
                    Log.d("ApiClient", "Received data: $spriteData")
                } else {
                    Log.d("ApiClient", "Error: ${response.code()}")
                    connectionViewModel.setConnectionStatus(false)
                }
            }

            override fun onFailure(call: Call<SpriteData>, t: Throwable) {
                Log.d("ApiClient", "Error: ${t.message}")
                connectionViewModel.setConnectionStatus(false)
            }
        })
    }
}