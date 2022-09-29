package edu.stanford.rkpandey.cameraintegration

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val FILE_NAME = "something"
private const val REQUEST_CODE = 42
private lateinit var photoFile: File
public lateinit var keywords : List<String>
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            // This DOESN'T work for API >= 24 (starting 2016)
            // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)

            val fileProvider = FileProvider.getUriForFile(this, "edu.stanford.rkpandey.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
        val btn = findViewById<Button>(R.id.send_request)
        btn.setOnClickListener{
            fun onClick(v: View?) {

            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("image2", storageDirectory.toString())
        return File.createTempFile(fileName, ".jpg", storageDirectory)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            Log.d("image3", photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
            var FilePath: String = photoFile.absolutePath;
            val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("http://192.168.50.223:5000/")
                .client(client)
                .build()

            val jsonPlaceholderApi = retrofitBuilder.create(JsonHolderApi::class.java)

            var requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile)
            Log.d("requestfile", photoFile.absolutePath)
// MultipartBody.Part is used to send also the actual file name
            var body: MultipartBody.Part = MultipartBody.Part.createFormData("files", photoFile.getName(), requestFile);
            jsonPlaceholderApi.uploadImage(body).enqueue(object : Callback<payload_response_body>{
                override fun onResponse(call: Call<payload_response_body>, response: Response<payload_response_body>) {

                    if (response.isSuccessful){
                        Log.d("success", response.body().toString())
                        var keyword_arr : Array<String> = emptyArray()
                        keywords = response.body()!!.keywords as ArrayList<String>
                        val send_request: Button = findViewById(R.id.send_request)

                        send_request.setOnClickListener{
                            val intent =  Intent(this@MainActivity, MainActivity2::class.java)

                            var b = Bundle()

                            b.putStringArrayList("keyword_arr", keywords as ArrayList<String>)
                            intent.putExtras(b)
                            startActivity(intent)
                        }
                    }
                    else{
                        Log.e("error", response.raw().message())
                    }
                }

                override fun onFailure(call: Call<payload_response_body>, t: Throwable) {
                    Log.e("failed", t.stackTraceToString())
                }

            })
            val userPost = payload_request_body(files = FilePath)
            /* val call = jsonPlaceholderApi.haveUserData(userPost)

             call.enqueue(object : Callback<payload_response_body?> {
                 override fun onResponse(
                     call: Call<payload_response_body?>,
                     response: Response<payload_response_body?>
                 ) {if (response.body()?.status==201){
                     response.body()?.keywords
                     Log.d("Success", response.body()!!.keywords.toString())
                 }else{
                     Log.d("Error", response.body().toString())
                 }}

                 override fun onFailure(call: Call<payload_response_body?>, t: Throwable) {
                    Log.e("err", t.stackTraceToString())
                 }
             })*/
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }


    }


}
