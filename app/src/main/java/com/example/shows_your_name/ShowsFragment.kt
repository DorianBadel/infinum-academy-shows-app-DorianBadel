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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowsViewModel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import android.widget.ProgressBar
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.R
import com.example.shows_your_name.ShowsFragment
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.models.ShowApi
import com.example.shows_your_name.models.ShowsResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctImage = "Image"
    private val ctDescription = "Description"
    private val ctID = "ID"
    private val ctCurrentUser = "keyValuePairs"
    private val ctTitle = "Title"
    private val HAS_PHOTO = "HAS_PHOTO"

    private val viewModel by viewModels<ShowsViewModel>()
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctImage,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctCurrentUser,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctAccessToken,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctClient,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctUid,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(viewModel.ctTokenType,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctID,Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(HAS_PHOTO,Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initiateViewModel(arguments,binding,this,sharedPreferences)
        viewModel.getAllShows(arguments,binding,activity!!,this)
        initShowsRecycler()


        binding.showHideShows.setOnClickListener{
            viewModel.showOrHideShows(binding)
        }

        binding.btnProfile.setOnClickListener {
            showProfileBottomSheet()
        }


    }

    companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ShowsFragment::class.java)
        }
    }

    private fun initShowsRecycler(){
        viewModel.getListOfShows().observe(viewLifecycleOwner){ ShowsApi ->

            if(ShowsApi!= null){
                adapter = ShowsAdapter(ShowsApi) { show ->

                    val bundle = bundleOf(
                        ctTitle to show.title,
                        ctDescription to show.description,
                        ctImage to show.imageUrl,
                        ctID to show.id,
                        "avgRating" to show.avgRating,
                        "noRatings" to show.noOfReviews,
                        ctUsername to viewModel.username.value
                    )

                    sharedPreferences.edit{
                        putInt(ctID, show.id)
                        commit()
                    }

                    findNavController().navigate(R.id.to_showDetailsFragment, bundle)
                }
                binding.showsRecycler.layoutManager = LinearLayoutManager(activity,
                    LinearLayoutManager.VERTICAL,false)

                binding.showsRecycler.adapter = adapter

            }
            }

    }

    private fun showProfileBottomSheet(){
        viewModel.createProfileBottomSheet(resources,sharedPreferences)
    }

}