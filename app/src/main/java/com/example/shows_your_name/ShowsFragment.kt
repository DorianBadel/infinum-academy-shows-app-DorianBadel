package com.example.shows_your_name

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_your_name.ShowsFragment
import com.example.shows_your_name.database.ShowsViewModelFactory
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences


    val IS_REMEMBERED = "IS_REMEMBERED"
    val REMEMBERED_USER = "REMEMBERED_USER"
    val ctLogoutAlertTitle = "You will leave your shows behind"
    val ctLogoutAlertDescription = "Are you sure you want to log out?"
    val ctLogoutAlertNegativeText = "No"
    val ctLogoutAlertPossitiveText = "Yes"
    private val sharedPrefs = "SHARED_STORAGE"
    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctImage = "Image"
    private val ctDescription = "Description"
    private val ctID = "ID"
    private val ctCurrentUser = "keyValuePairs"
    private val ctTitle = "Title"
    private val HAS_PHOTO = "HAS_PHOTO"
    private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"
    //val thisApp = (requireActivity().application as ShowsApp)


    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((requireActivity().application  as ShowsApp).database)
    }
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
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

        viewModel.initiateViewModel(sharedPreferences.getString(ctUsername,"")!!)
        viewModel.getAllShows(binding,this)
        initShowsRecycler()


        binding.showHideShows.setOnClickListener{
            viewModel.showOrHideShows(binding)
        }

        binding.btnProfile.setOnClickListener {
            showProfileBottomSheet()
        }


    }

    /*companion object{
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ShowsFragment::class.java)
        }
    }*/

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

    //Profile bottom sheet

    private fun showProfileBottomSheet(){
        val dialog = BottomSheetDialog(this.requireView().context)

        val bottomSheetBinding = DialogProfileBinding.inflate(this.layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        setProfileImage(bottomSheetBinding)

        bottomSheetBinding.txtUsername.text = sharedPreferences.getString(ctUsername,"")

        //Change profile picture btn

        bottomSheetBinding.btnChangeProfilePic.setOnClickListener {
            val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            setProfileImage(bottomSheetBinding)

            if(true){ //takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null
                startActivityForResult(takePictureIntent,123)
                dialog.dismiss()
            }
            else{
                Toast.makeText(context,"Can't open the camera app",Toast.LENGTH_SHORT).show()
            }
        }


        bottomSheetBinding.btnDialogLogOut.setOnClickListener{
            createAlert(dialog)
        }

        dialog.show()
    }

    //Changing photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == Activity.RESULT_OK){

            sharedPreferences.edit{
                putString(REMEMBERED_PHOTO,viewModel.encodeBitmapToString(data))
            }
            Toast.makeText(context,"Profile photo set !",Toast.LENGTH_SHORT).show()
        }
    }

    fun setProfileImage(binding: DialogProfileBinding){
        val encoded = viewModel.getStringFromBitmap(
            BitmapFactory.decodeResource(resources,
            R.drawable.profile_ico
        ))
        val profilePhoto = sharedPreferences.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        Glide.with(this)
            .load(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            .circleCrop()
            .into(binding.imgProfile)

        //binding.imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
    }

    //Loging off

    fun createAlert(dialog: BottomSheetDialog){

        var builder = AlertDialog.Builder(activity)

        builder.setTitle(ctLogoutAlertTitle)
            .setMessage(ctLogoutAlertDescription)
            .setCancelable(true)
            .setPositiveButton(ctLogoutAlertPossitiveText){_,_ ->
                logOut()

                findNavController().navigate(R.id.to_loginFraagment)
                dialog.dismiss()

            }
            .setNegativeButton(ctLogoutAlertNegativeText){dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

    fun logOut(){
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(IS_REMEMBERED, false)
            putString(REMEMBERED_USER, "")
            remove(REMEMBERED_PHOTO)
        }
    }




}