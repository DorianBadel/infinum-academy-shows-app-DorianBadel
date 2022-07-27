package com.example.shows_your_name

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctImage = "Image"
    private val ctDescription = "Description"
    private val ctID = "ID"
    private val ctTitle = "Title"
    private val HAS_PHOTO = "HAS_PHOTO"

    private val viewModel by viewModels<ShowsViewModel>()
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctImage,Context.MODE_PRIVATE)
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
        sharedPreferences = requireContext().getSharedPreferences(HAS_PHOTO,Context.MODE_PRIVATE)

        viewModel.initiateViewModel(arguments,binding,this)
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
        viewModel.listOfShowsLiveData.observe(viewLifecycleOwner){ Shows ->

            adapter = ShowsAdapter(Shows) { show ->

                val bundle = bundleOf(
                    ctTitle to show.title,
                    ctDescription to show.desc,
                    ctImage to show.imageResourceId,
                    ctID to show.ID,
                    ctUsername to viewModel.username.value
                )

                findNavController().navigate(R.id.to_showDetailsFragment, bundle)
            }
            binding.showsRecycler.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,false)

            binding.showsRecycler.adapter = adapter

        }
    }

    private fun showProfileBottomSheet(){
        viewModel.createProfileBottomSheet(resources,sharedPreferences)
    }

}