package com.socheanith.bookhub2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.socheanith.bookhub2.R
import com.socheanith.bookhub2.activity.BookDescription
import com.socheanith.bookhub2.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdator(val context: Context,val itemList: ArrayList<Book> ): RecyclerView.Adapter<DashboardRecyclerAdator.DashboardViewHolder>() {
    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view){
//        val textView: TextView = view.findViewById(R.id.txtRecyclerRow1)
        val txtBookName: TextView = view.findViewById((R.id.txtRecyclerRow1))
        val txtBooKAuthor: TextView = view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice: TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.img1)
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_layout_dashboard_sigle_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBooKAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookRating
        holder.txtBookRating.text = book.bookPrice
     //   holder.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)
        holder.llcontent.setOnClickListener{
            val intent = Intent(context, BookDescription::class.java)
            intent.putExtra("book_id", book.bookId)
            val intent1 = Intent(context, FavoriteRecycleAdaptor::class.java)
            intent1.putExtra("book_id", book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}