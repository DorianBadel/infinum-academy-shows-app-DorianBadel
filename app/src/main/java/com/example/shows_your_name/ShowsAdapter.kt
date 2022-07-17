package com.example.shows_your_name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_your_name.databinding.ViewShowCardBinding

class ShowsAdapter (
    private var items: List<Show>,
    private var onItemClickCallback: (Show) -> Unit
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

        fun bind(item: Show){
            binding.cardTitle.text = item.title
            binding.cardDesc.text = item.desc
            binding.cardImage.setImageResource(item.imageResourceId)

            binding.cardContainer.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }

}