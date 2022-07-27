package com.example.shows_your_name

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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


    //Constants
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctImage = "Image"
    private val ctDescription = "Description"
    private val ctID = "ID"
    private val ctTitle = "Title"
    private val HAS_PHOTO = "HAS_PHOTO"
    private val ctHideOff = "Hide"
    private val ctHideOn = "Show"
    private val ctLogoutAlertTitle = "You will leave your shows behind"
    private val ctLogoutAlertDescription = "Are you sure you want to log out?"
    private val ctLogoutAlertNegativeText = "No"
    private val ctLogoutAlertPossitiveText = "Yes"
    private val ctExtrasData = "data"

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

        viewModel.initiateViewModel(arguments,binding)
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
        val dialog = BottomSheetDialog(requireView().context)

        val bottomSheetBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.txtUsername.text = viewModel.username.value

        //Change profile picture btn

        bottomSheetBinding.btnChangeProfilePic.setOnClickListener {
            val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(true){
                startActivityForResult(takePictureIntent,123)
                dialog.dismiss()
            }
        }

        viewModel.setProfileImage(sharedPreferences,bottomSheetBinding,getResources())

        //Logout button

        bottomSheetBinding.btnDialogLogOut.setOnClickListener{

            var builder = AlertDialog.Builder(activity)

            builder.setTitle(ctLogoutAlertTitle)
                .setMessage(ctLogoutAlertDescription)
                .setCancelable(true)
                .setPositiveButton(ctLogoutAlertPossitiveText){_,_ ->
                    //Log out
                    sharedPreferences = requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
                    sharedPreferences = requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)
                    sharedPreferences.edit {
                        putBoolean(IS_REMEMBERED, false)
                    }
                    sharedPreferences.edit{
                        putString(REMEMBERED_USER, "")
                    }
                    sharedPreferences.edit{
                        putString(REMEMBERED_PHOTO,viewModel.encodeString(resources))
                    }

                    findNavController().navigate(R.id.to_loginFraagment)
                    dialog.dismiss()
                    //Log out

                }
                .setNegativeButton(ctLogoutAlertNegativeText){dialogInterface,it ->
                    dialogInterface.cancel()
                }
                .show()


        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            sharedPreferences.edit{
                //An image becomes a string voodoo
                val encoded = viewModel.getStringFromBitmap(data?.extras?.get(ctExtrasData) as Bitmap)
               putString(REMEMBERED_PHOTO,encoded)
            }
        }
    }

}