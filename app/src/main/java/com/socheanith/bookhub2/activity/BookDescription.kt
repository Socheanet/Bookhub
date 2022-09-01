package com.socheanith.bookhub2.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.socheanith.bookhub2.R
import com.socheanith.bookhub2.database.BookDatabase
import com.socheanith.bookhub2.database.BookEntity
import com.socheanith.bookhub2.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class BookDescription : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRate: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDec: TextView
    lateinit var btnAddtoFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRate = findViewById(R.id.txtBookRating)
        txtBookDec = findViewById(R.id.txtBookDes)
        imgBookImage = findViewById(R.id.imgBook)

        btnAddtoFav = findViewById(R.id.btnAddtoFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        if(intent !=null){
            bookId = intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@BookDescription, "Unexpected Errors Occured!!",Toast.LENGTH_SHORT).show()
        }
        if(bookId == "100"){
            finish()
            Toast.makeText(this@BookDescription, "Some unexpected errors occurred!!", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@BookDescription)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)

        if(ConnectionManager().checkConnectivity(this@BookDescription)){
            val jsonRequest = object: JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try{
                    val success = it.getBoolean("success")
                    if(success){
                        val bookJsonObject = it.getJSONObject("book_data")
                        progressLayout.visibility = View.GONE

                        val bookImageUrl = bookJsonObject.getString("image")
                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                        txtBookName.text = bookJsonObject.getString("name")
                        txtBookAuthor.text = bookJsonObject.getString("author")
                        txtBookPrice.text = bookJsonObject.getString("price")
                        txtBookRate.text  = bookJsonObject.getString("rating")
                        txtBookDec.text = bookJsonObject.getString("description")

                        val bookEntity = BookEntity(
                            bookId?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRate.text.toString(),
                            txtBookDec.text.toString(),
                            bookImageUrl
                        )
                        val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                        val isFav = checkFav.get()
                        if(isFav){
                            btnAddtoFav.text = "Remove From Favorite"
                            val favColor = ContextCompat.getColor(applicationContext, R.color.color_favorite)
                            btnAddtoFav.setBackgroundColor(favColor)
                        }else{
                            btnAddtoFav.text = "Add To Favorite"
                            val noFavColor = ContextCompat.getColor(applicationContext, R.color.color_primary)
                            btnAddtoFav.setBackgroundColor(noFavColor)

                        }

                        btnAddtoFav.setOnClickListener{
                            if(!DBAsyncTask(applicationContext,bookEntity, 1).execute().get()){
                                val async = DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val result = async.get()
                                if(result){
                                    Toast.makeText(this@BookDescription,"Book added to Favorite",Toast.LENGTH_SHORT).show()
                                    btnAddtoFav.text = "Remove From Favorite"
                                    val favColor = ContextCompat.getColor(applicationContext,R.color.color_favorite)
                                    btnAddtoFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(this@BookDescription,"Some Error Occured",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                val async = DBAsyncTask(applicationContext,bookEntity, 3).execute()
                                val result = async.get()
                                if(result){
                                    Toast.makeText(this@BookDescription,"Book removed to Favorite",Toast.LENGTH_SHORT).show()
                                    btnAddtoFav.text = "Add to Favorite"
                                    val noFavColor = ContextCompat.getColor(applicationContext, R.color.color_primary)
                                    btnAddtoFav.setBackgroundColor(noFavColor)
                                }else{
                                    Toast.makeText(this@BookDescription,"Some Error Occured",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this@BookDescription,"some error occurred!!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@BookDescription,"some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@BookDescription,"Volley Error $it", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "1e40f6d8964e6b"
                    return headers
                }
            }
            queue.add(jsonRequest)

        } else{
            val dialog = AlertDialog.Builder(this@BookDescription)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found!!")
            dialog.setPositiveButton("Open Setting"){text , listener ->
                val settingIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(this@BookDescription)
            }
            dialog.create()
            dialog.show()
        }
        }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){
        /*
        Mode 1: Check if the book is in the favorite or not
        Mode 2: If not add to the favorite
        Mode 3: If the book is there ask to remove form the favorite
         */
        val db= Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode){
                1-> {
                    //Check DB if the book is in the favorite or not
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!= null
                }
                2-> {
                    //Save book to the favorite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->{
                    //Remove the book from the favorite
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }

            }
          return false
        }

    }


}