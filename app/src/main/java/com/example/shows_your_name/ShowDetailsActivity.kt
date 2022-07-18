package com.example.shows_your_name

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.ActivityShowDetailsBinding

class ShowDetailsActivity : AppCompatActivity() {
    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ShowDetailsActivity::class.java)
        }
    }

    private var reviews = listOf(
        Review(1,"renato.ruric","",3),
        Review(2,"gan.dalf","joooj pre dobrooooo",5)
    )

    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var adapter: ReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.showTitle.text = intent.extras?.getString("Title")
        binding.showDescription.text = intent.extras?.getString("Description")
        binding.showCoverImage.setImageResource(intent.extras!!.getInt("Image"))
        binding.reviewsText.text = " AVERAGE"
        binding.ratingBar.rating = getAverageRating()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initShowsRecycler()


    }

    private fun getAverageRating(): Float {
        var total = 0
        for (i in reviews){
            total += i.rating
        }

        return total.toFloat()/reviews.count()
    }

    private fun initShowsRecycler(){
        adapter = ReviewsAdapter(reviews) { review ->
            Toast.makeText(this, review.username, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        binding.recyclerView.adapter = adapter

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }
}