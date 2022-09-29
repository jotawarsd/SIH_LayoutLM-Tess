package edu.stanford.rkpandey.cameraintegration


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.*

interface JsonHolderApi {
   /* @Multipart
    @POST("detect")
    fun haveUserData(
        @Body Payload_request_body : payload_request_body
    ):Call<payload_response_body>*/
    @Multipart
    @POST("detect")
    fun uploadImage( @Part files: MultipartBody.Part): Call<payload_response_body>
}