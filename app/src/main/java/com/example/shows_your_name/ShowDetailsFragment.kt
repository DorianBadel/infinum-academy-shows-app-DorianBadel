package com.example.shows_your_name

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_your_name.database.ShowDetailsViewModelFactory
import com.example.shows_your_name.database.UserTypeConverter
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.example.shows_your_name.models.ReviewApi
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedPrefs = "SHARED_STORAGE"
    val utc = UserTypeConverter()
    private val args by navArgs<ShowDetailsFragmentArgs>()


    private lateinit var adapter: ReviewsAdapter

    private val viewModel: ShowDetailsViewModel by viewModels {
        ShowDetailsViewModelFactory((requireActivity().application  as ShowsApp).database)
    }
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)

        /*viewModel.getShowInfoOnline(args.showID).observe(this){ Show ->

        }*/
        viewModel.getShowInfoOffline(args.showID).observe(this){ Show ->
            viewModel.initValues(args.showID) //TODO this makes issues
            binding.showDescription.text = Show.description
            binding.showTitle.text = Show.title
            Glide.with(this)
                .load(Show.imageUrl)
                .into(binding.showCoverImage)

            updateStatistics(Show.averageRating!!,Show.noOfReviews)

            binding.ratingBar.rating = Show.averageRating!!
            binding.reviewsText.text = Show.noOfReviews.toString() + " REVIEWS, " + Show.averageRating+ " AVERAGE"


        }
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

        if(!hasInternet()){
            binding.progressbar.isVisible = true
            viewModel.getReviews(
                args.showID,
                sharedPreferences.getString(viewModel.ctAccessToken,"")!!,
                sharedPreferences.getString(viewModel.ctClient,"")!!,
                sharedPreferences.getString(viewModel.ctUid,"")!!,
                sharedPreferences.getString(viewModel.ctTokenType,"")!!
            )
        }else{
            viewModel.getReviewsOffline(args.showID)
            viewModel.getShowInfoOffline(args.showID)
        }

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

            binding.recyclerView.adapter = adapter

            binding.recyclerView.addItemDecoration(
                DividerItemDecoration(requireView().context, DividerItemDecoration.VERTICAL)
            )
        }

        viewModel.getReviewsOffline(args.showID).observe(viewLifecycleOwner){ reviewsEntity ->
            adapter = ReviewsAdapter(reviewsEntity.filter { reviewEntity -> reviewEntity.showID == args.showID  }.map { showReview ->
                ReviewApi(
                    showReview.id,showReview.comment,showReview.rating,showReview.showID,utc.toUser(showReview.user)
                )
            }) { review ->
            }

            binding.progressbar.isVisible = false

            binding.recyclerView.layoutManager = LinearLayoutManager(
                requireView().context,
                LinearLayoutManager.VERTICAL, false
            )

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

    //Has internet check
    fun hasInternet(): Boolean{
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                //TODO add getListOfOffline here
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun updateStatistics(avg: Float, total: Int){
        binding.reviewsText.text =
            arguments?.getInt("noRatings").toString() + " REVIEWS, " + arguments?.getFloat("avgRating").toString() + " AVERAGE"
        binding.ratingBar.rating = arguments!!.getFloat("avgRating")
    }

}
