package com.example.shows_your_name

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.ActivityShowsBinding

class ShowsActivity : AppCompatActivity() {
    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ShowsActivity::class.java)
        }
    }

    private var shows = listOf(
        Show(1,"Office","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_office),
        Show(2,"Stranger Things","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_stranger_things),
        Show(3,"Krv nije voda","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_krv_nije_voda)
    )

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.showHideShows.setOnClickListener{
            if(binding.showHideShows.text == "Hide"){
                binding.noShowsIco.isVisible = true
                binding.noShowsText.isVisible = true
                binding.showsRecycler.isVisible = false
                binding.showHideShows.text = "Show"
            } else if (binding.showHideShows.text == "Show"){
                binding.noShowsIco.isVisible = false
                binding.noShowsText.isVisible = false
                binding.showsRecycler.isVisible = true
                initShowsRecycler()
                binding.showHideShows.text = "Hide"
            }
        }


        if(shows.isNullOrEmpty()){
            binding.noShowsIco.isVisible = true
            binding.noShowsText.isVisible = true
        } else{
            binding.noShowsIco.isVisible = false
            binding.noShowsText.isVisible = false
            initShowsRecycler()
        }
        initLoadItemsButton()
        initAddShowButton()
    }

    private fun initShowsRecycler(){
        adapter = ShowsAdapter(shows) { show ->
            //Toast.makeText(this, show.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(this,ShowDetailsActivity::class.java)
            intent.putExtra("Title",show.title)
            startActivity(intent)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.showsRecycler.adapter = adapter

        binding.showsRecycler.addItemDecoration(
            DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        )
    }

    private fun initLoadItemsButton(){

    }

    private fun initAddShowButton(){

    }
}