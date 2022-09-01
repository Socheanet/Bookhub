package com.socheanith.bookhub2.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.socheanith.bookhub2.R
import com.socheanith.bookhub2.adapter.DashboardRecyclerAdator
import com.socheanith.bookhub2.model.Book
import com.socheanith.bookhub2.util.ConnectionManager
import org.json.JSONException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdator: DashboardRecyclerAdator
    lateinit var progrssLayout: RelativeLayout
    lateinit var progrssBar: ProgressBar
    val bookInfoList = arrayListOf<Book>()

    var ratingComparator = Comparator<Book>{book1, book2 ->
       if( book1.bookPrice.compareTo(book2.bookPrice, true) ==0){
           book1.bookName.compareTo(book2.bookName,true)
       }else{
           book1.bookPrice.compareTo(book2.bookPrice, true)
       }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progrssLayout = view.findViewById(R.id.progressLayout)
        progrssBar = view.findViewById(R.id.progressBar)
        progrssLayout.visibility = View.VISIBLE


        // Inflate the layout for this fragment

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
               try{
                   progrssLayout.visibility = View.GONE
                   val success = it.getBoolean("success")
                   if(success){
                       val data = it.getJSONArray("data")
                       for(i in 0 until data.length()){
                           val bookJsonObject = data.getJSONObject(i)
                           val bookObject = Book(
                               bookJsonObject.getString("book_id"),
                               bookJsonObject.getString("name"),
                               bookJsonObject.getString("author"),
                               bookJsonObject.getString("rating"),
                               bookJsonObject.getString("price"),
                               bookJsonObject.getString("image")
                           )
                           bookInfoList.add(bookObject)
                           recyclerAdator = DashboardRecyclerAdator(activity as Context, bookInfoList)
                           recyclerDashboard.adapter = recyclerAdator
                           recyclerDashboard.layoutManager = layoutManager
                       }
                   }else{
                       Toast.makeText(activity as Context, "Some Error has occured!!", Toast.LENGTH_SHORT).show()
                   }



               }catch(e: JSONException){
                   Toast.makeText(activity as Context, "some unexpected error occured!!",Toast.LENGTH_SHORT).show()
               }

            }, Response.ErrorListener {
                Toast.makeText(activity as Context, "Volley Error Occured!! ", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "1e40f6d8964e6b"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found!!")
            dialog.setPositiveButton("Open Setting"){text , listener ->
                val settingIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if(id==R.id.action_sort){
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()

        }

        recyclerAdator.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}