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
import com.socheanith.bookhub2.database.BookEntity
import com.squareup.picasso.Picasso


class FavoriteRecycleAdaptor(val context: Context, val bookList: List<BookEntity>): RecyclerView.Adapter<FavoriteRecycleAdaptor.FavoriteViewHolder>() {
    class  FavoriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtBookName: TextView = view.findViewById(R.id.txtFavBookTitle)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtFavBookAuthor)
        val txBookPrice: TextView = view.findViewById(R.id.txtFavBookPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtFavBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgFavBook)
        val llcontent: LinearLayout = view.findViewById(R.id.llFavLayout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favorite_single_row, parent,false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val book = bookList[position]

        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)

        holder.llcontent.setOnClickListener{
            val intent = Intent(context, BookDescription::class.java)
            intent.putExtra("book_id", book.book_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return bookList.size
    }
}