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
import com.example.shows_your_name.database.ReviewEntity
import com.example.shows_your_name.database.ShowDetailsViewModelFactory
import com.example.shows_your_name.database.UserTypeConverter
import com.example.shows_your_name.databinding.DialogAddReviewBinding
import com.example.shows_your_name.databinding.FragmentShowDetailsBinding
import com.example.shows_your_name.models.ReviewApi
import com.example.shows_your_name.models.User
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedPrefs = "SHARED_STORAGE"
    private val utc = UserTypeConverter()
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

        viewModel.getShowInfoOffline(args.showID).observe(this){ Show ->
            binding.showDescription.text = Show.description
            binding.showTitle.text = Show.title
            Glide.with(this)
                .load(Show.imageUrl)
                .into(binding.showCoverImage)

            updateStatistics(Show.averageRating!!,Show.noOfReviews)

            binding.ratingBar.rating = Show.averageRating!!
            binding.reviewsText.text = Show.noOfReviews.toString() + " REVIEWS, " + Show.averageRating+ " AVERAGE"
        }

        viewModel.getShowInfoOnline().observe(this){ Show ->
            binding.showDescription.text = Show.description
            binding.showTitle.text = Show.title
            Glide.with(this)
                .load(Show.imageUrl)
                .into(binding.showCoverImage)

            updateStatistics(Show.avgRating!!,Show.noOfReviews)

            binding.ratingBar.rating = Show.avgRating!!
            binding.reviewsText.text = Show.noOfReviews.toString() + " REVIEWS, " + Show.avgRating+ " AVERAGE"
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

        if(hasInternet()){
            binding.progressbar.isVisible = true
            viewModel.getShowInfo(
                args.showID,
                sharedPreferences.getString(viewModel.ctAccessToken,"")!!,
                sharedPreferences.getString(viewModel.ctClient,"")!!,
                sharedPreferences.getString(viewModel.ctUid,"")!!,
                sharedPreferences.getString(viewModel.ctTokenType,"")!!

            )

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



        binding.toolbarBackBtn.setOnClickListener {
            findNavController().navigate(R.id.to_showsFragment)
        }


        binding.addReviewBtn.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun initShowsRecycler() {
        viewModel.getReviewsList().observe(viewLifecycleOwner){ reviewsApi ->
            adapter = ReviewsAdapter(reviewsApi) { review ->
            }
            val reviewEntity = reviewsApi.map{ review ->
                ReviewEntity(
                    id = review.id,
                    comment = review.comment,
                    rating = review.rating,
                    showID = review.showId,
                    user = utc.toUserJson(review.user)
                )
            }
            viewModel.updateDB(reviewEntity)
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


        if(!hasInternet()){
            viewModel.getReviewsOffline(args.showID).observe(viewLifecycleOwner){ reviewsEntity ->
                adapter = ReviewsAdapter(reviewsEntity.map { showReview ->
                    ReviewApi(
                        id = showReview.id,
                        comment = showReview.comment,
                        rating = showReview.rating,
                        showId = showReview.showID,
                        user = utc.toUser(showReview.user)
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


    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(requireView().context)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.submitReviewButton.setOnClickListener {
            binding.progressbar.isVisible = true
            if(hasInternet()){
                viewModel.addReview(
                    args.showID,
                    sharedPreferences.getString(viewModel.ctAccessToken,"")!!,
                    sharedPreferences.getString(viewModel.ctClient,"")!!,
                    sharedPreferences.getString(viewModel.ctUid,"")!!,
                    sharedPreferences.getString(viewModel.ctTokenType,"")!!,
                    bottomSheetBinding.reviewSetRating.rating.toInt(),
                    bottomSheetBinding.commentText.text.toString()
                )
            }
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

    private fun getUser(): User {
        return utc.toUser(
            sharedPreferences.getString(
                "REMEMBERED_USERR",
                utc.toUserJson(
                    User(
                        "999",
                        "test@gmail.com",
                        null
                    )
                )
            )!!
        )
    }

}
