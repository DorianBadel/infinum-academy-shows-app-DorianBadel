package com.example.shows_your_name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
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

    private val viewModel by viewModels<ShowDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initiateViewModel(arguments)
        reviews = viewModel.getReviews()

        binding.showTitle.text = viewModel.title.value
        binding.showDescription.text = viewModel.description.value

        binding.showCoverImage.setImageResource(viewModel.imageId.value!!)

        binding.toolbarBackBtn.setOnClickListener {
            val bundle = bundleOf("Username" to viewModel.username.value)
            findNavController().navigate(R.id.to_showsFragment, bundle)
        }

        initShowsRecycler()

        binding.addReviewBtn.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun initShowsRecycler() {
        adapter = ReviewsAdapter(reviews) { review ->
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireView().context,
            LinearLayoutManager.VERTICAL, false
        )

        viewModel.updateStatistics(binding,reviews)

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
            reviews += viewModel.addReview(bottomSheetBinding, adapter, reviews)
            viewModel.updateStatistics(binding,reviews)
            dialog.dismiss()
        }

        bottomSheetBinding.closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
