package com.example.shows_your_name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_your_name.databinding.ViewItemReviewBinding

class ReviewsAdapter(
    private var items: List<Review>,
    private var onItemClickCallback: (Review) -> Unit
): RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val binding = ViewItemReviewBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    inner class ReviewsViewHolder(private val binding: ViewItemReviewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Review){
            binding.reviewUsername.text = item.username
            binding.reviewText.text = item.comment
            binding.reviewRating.text = item.rating.toString()

            binding.reviewProfileImage.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }
}