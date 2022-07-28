package com.example.shows_your_name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_your_name.databinding.ViewShowCardBinding
import com.example.shows_your_name.models.ShowApi

class ShowsAdapter (
    private var items: List<ShowApi>,
    private var onItemClickCallback: (ShowApi) -> Unit
): RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowCardBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    inner class ShowViewHolder(private val binding: ViewShowCardBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ShowApi){
            binding.cardTitle.text = item.title
            binding.cardDesc.text = item.description
            binding.cardImage.setImageURI(item.imageUrl.toUri())

            binding.cardContainer.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }

}