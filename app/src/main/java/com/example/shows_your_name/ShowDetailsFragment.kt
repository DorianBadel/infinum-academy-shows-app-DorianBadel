package com.example.shows_your_name

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.bumptech.glide.Glide
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowDetailsViewModel
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
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctAccessToken, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctClient, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctUid, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctTokenType, Context.MODE_PRIVATE)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.initiateViewModel(arguments,binding, this)

        initShowsRecycler()

        binding.showTitle.text = viewModel.title.value
        binding.showDescription.text = viewModel.description.value

        Glide.with(this)
            .load(viewModel.imageId.value)
            .into(binding.showCoverImage)


        binding.toolbarBackBtn.setOnClickListener {
            val bundle = bundleOf("Username" to viewModel.username.value)
            findNavController().navigate(R.id.to_showsFragment, bundle)
        }


        binding.addReviewBtn.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun initShowsRecycler() {
        viewModel.getReviewsList().observe(viewLifecycleOwner){ reviewsApi ->
            adapter = ReviewsAdapter(reviewsApi) { review ->
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(
                requireView().context,
                LinearLayoutManager.VERTICAL, false
            )

            viewModel.updateStatistics(binding,arguments)

            binding.recyclerView.adapter = adapter

            binding.recyclerView.addItemDecoration(
                DividerItemDecoration(requireView().context, DividerItemDecoration.VERTICAL)
            )
        }

    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(requireView().context)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.submitReviewButton.setOnClickListener {
            viewModel.addReview(binding,bottomSheetBinding, this)
            viewModel.updateStatistics(binding,arguments)
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
