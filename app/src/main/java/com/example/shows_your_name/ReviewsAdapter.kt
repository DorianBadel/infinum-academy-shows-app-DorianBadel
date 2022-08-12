package com.example.shows_your_name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shows_your_name.database.UserTypeConverter
import com.example.shows_your_name.databinding.ViewItemReviewBinding
import com.example.shows_your_name.models.ReviewApi

class ReviewsAdapter(
    private var items: List<ReviewApi>,
    private var onItemClickCallback: (ReviewApi) -> Unit
): RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>(){
    private val utc = UserTypeConverter()

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

    /*fun addItem(review: ReviewApi) {
        items = items + review
        notifyItemInserted(items.lastIndex)
    }*/

    inner class ReviewsViewHolder(private val binding: ViewItemReviewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ReviewApi){
            binding.reviewUsername.text = item.user.email.substringBefore("@")
            binding.reviewText.text = item.comment
            binding.reviewRating.text = item.rating.toString()

            if(item.user.imageUrl == null || item.user.imageUrl == "no_photo"){
                Glide.with(itemView)
                    .load(R.drawable.ic_new_profile)
                    .circleCrop()
                    .into(binding.reviewProfileImage)
            } else{
                Glide.with(itemView)
                    .load(item.user.imageUrl)
                    .circleCrop()
                    .into(binding.reviewProfileImage)
            }


            binding.reviewProfileImage.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }
}