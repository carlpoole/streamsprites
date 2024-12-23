package codes.carl.streamsprites.network

import codes.carl.streamsprites.model.SpriteData
import codes.carl.streamsprites.model.Test
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface SpriteService {

    @GET("test")
    fun test(): Call<Test>

    @PUT("ingest")
    fun putData(@Body requestData: SpriteData): Call<Unit>

    @GET("latestSpeed")
    fun getLatestSpeed(@Query("twitchUserId") twitchUserId: String): Call<SpriteData>
}