package com.example.shows_your_name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_your_name.databinding.ViewItemReviewBinding
import com.example.shows_your_name.models.ReviewApi

class ReviewsAdapter(
    private var items: List<ReviewApi>,
    private var onItemClickCallback: (ReviewApi) -> Unit
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

    fun addItem(review: ReviewApi) {
        items = items + review
        notifyItemInserted(items.lastIndex)
    }

    inner class ReviewsViewHolder(private val binding: ViewItemReviewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ReviewApi){
            binding.reviewUsername.text = item.user.email.substringBefore("@")
            binding.reviewText.text = item.comment
            binding.reviewRating.text = item.rating.toString()

            binding.reviewProfileImage.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }
}