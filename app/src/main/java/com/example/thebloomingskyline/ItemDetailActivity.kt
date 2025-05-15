package com.example.thebloomingskyline

import Item
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val item = intent.getSerializableExtra("item") as? Item

        if (item != null) {
            val nameView: TextView = findViewById(R.id.detailName)
            val descView: TextView = findViewById(R.id.detailDesc)
            val textView: TextView = findViewById(R.id.detailText)
            val priceView: TextView = findViewById(R.id.detailPrice)
            val imageView: ImageView = findViewById(R.id.detailImage)

            nameView.text = item.charack.name
            descView.text = item.charack.desc
            textView.text = item.charack.text
            priceView.text = "${item.price} ₽"

            Glide.with(this)
                .load(item.image)
                .into(imageView)
        }
    }
}
