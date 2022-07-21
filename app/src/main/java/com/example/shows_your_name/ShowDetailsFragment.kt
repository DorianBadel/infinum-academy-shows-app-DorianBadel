package com.example.shows_your_name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShowDetailsFragment : Fragment() {
    companion object {
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ShowDetailsFragment::class.java)
        }
    }

    lateinit var reviews: List<Review>
    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ReviewsAdapter


    private var reviews1 = listOf(
        Review(1, "renato.ruric", "", 3),
        Review(2, "gan.dalf", "joooj pre dobrooooo", 5)
    )

    private var reviews2 = listOf(
        Review(1, "renato.ruric", "", 5),
        Review(2, "gan.dalf", "joooj pre dobrooooo", 5),
        Review(3, "mark.twain", "Sve u svemu savrseno", 5)
    )
    private var reviews3 = listOf(
        Review(1, "plenky", "meh", 1)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = arguments?.getInt("ID")
        val title = arguments?.getString("Title").toString()
        val description = arguments?.getString("Description").toString()
        val imageId = arguments?.getInt("Image")
        var username = arguments?.getString("Username")



        if (id == 2) {
            reviews = reviews2

        } else if (id == 3) {
            reviews = reviews3
        } else {
            reviews = reviews1
        }

        binding.showTitle.text = title
        binding.showDescription.text = description
        if (imageId != null) {
            binding.showCoverImage.setImageResource(imageId)
        }

        binding.toolbarBackBtn.setOnClickListener {
            val bundle = bundleOf("Username" to username)
            findNavController().navigate(R.id.to_showsFragment, bundle)
        }
        initShowsRecycler()

        binding.addReviewBtn.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun getAverageRating(): Float {
        var total = 0
        for (i in reviews) {
            total += i.rating
        }

        return total.toFloat() / reviews.count()
    }

    private fun initShowsRecycler() {
        adapter = ReviewsAdapter(reviews) { review ->
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireView().context,
            LinearLayoutManager.VERTICAL, false
        )

        binding.reviewsText.text =
            reviews.count().toString() + " REVIEWS, " + getAverageRating() + " AVERAGE"
        binding.ratingBar.rating = getAverageRating()

        binding.recyclerView.adapter = adapter

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireView().context, DividerItemDecoration.VERTICAL)
        )
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(requireView().context)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.submitReviewButton.setOnClickListener {
            addShowToList(
                bottomSheetBinding.reviewSetRating.rating.toInt(),
                bottomSheetBinding.commentText.text.toString()
            )
            dialog.dismiss()
        }

        bottomSheetBinding.closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addShowToList(rating: Int, comment: String) {
        adapter.addItem(Review(reviews.last().ID + 1, arguments?.getString("Username").toString(), comment, rating))
        reviews += Review(reviews.last().ID + 1, arguments?.getString("Username").toString(), comment, rating)


        //updates statistics
        binding.reviewsText.text =
            reviews.count().toString() + " REVIEWS, " + getAverageRating() + " AVERAGE"
        binding.ratingBar.rating = getAverageRating()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}