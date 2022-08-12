package com.example.shows_your_name

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shows_your_name.database.ShowEntity
import com.example.shows_your_name.database.ShowsViewModelFactory
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.example.shows_your_name.models.ShowApi
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.ShowsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((requireActivity().application  as ShowsApp).database)
    }
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initShowsRecycler()
        if(hasInternet()){
            binding.progressbar.isVisible = true
            viewModel.getAllShows(sharedPreferences.getString(getString(R.string.ACCESS_TOKEN),"")!!,
                sharedPreferences.getString(getString(R.string.CLIENT),"")!!,
                sharedPreferences.getString(getString(R.string.UID),"")!!,
                sharedPreferences.getString(getString(R.string.TOKEN_TYPE),"")!!
                )
        } else{
            viewModel.getListOfShowsOffline()
        }


        binding.showHideShows.setOnClickListener{
            if(binding.showHideShows.text == getString(R.string.ct_show_hide_btn_title_on)){
                showShows()
            } else if (binding.showHideShows.text == getString(R.string.ct_show_hide_btn_title_off)){
                hideShows()
            }
        }


        binding.btnEditProfile.setOnClickListener {
            showProfileBottomSheet()
        }
    }

    private fun initShowsRecycler(){
        if(hasInternet()){
            viewModel.getListOfShows().observe(viewLifecycleOwner){ ShowsApi ->

                if(ShowsApi!= null){
                    val showsEntity = ShowsApi.map{ show ->
                        ShowEntity(
                            id = show.id,
                            averageRating = show.avgRating,
                            title = show.title,
                            imageUrl = show.imageUrl,
                            description = show.description,
                            noOfReviews = show.noOfReviews
                        )
                    }
                    viewModel.updateDB(showsEntity)
                    showShows()
                    binding.progressbar.isVisible = false
                    adapter = ShowsAdapter(ShowsApi) { show ->



                        val action = ShowsFragmentDirections.toShowDetailsFragment(show.id)
                        findNavController().navigate(action)
                    }
                    binding.showsRecycler.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.VERTICAL,false)

                    binding.showsRecycler.adapter = adapter

                }
            }
        }


        //If offline
        else{
            viewModel.getListOfShowsOffline().observe(viewLifecycleOwner){ Shows ->

                if(Shows != null){
                    showShows()
                    if(binding.progressbar.isVisible) binding.progressbar.isVisible = false
                    adapter = ShowsAdapter(Shows.map { showEntity ->
                        ShowApi(showEntity.id,showEntity.averageRating,showEntity.description,
                            showEntity.imageUrl,showEntity.noOfReviews,showEntity.title)
                    }) { show ->
                        sharedPreferences.edit{
                            putInt(getString(R.string.ct_show_id),show.id) //TODO Not sure this is used
                        }
                        val action = ShowsFragmentDirections.toShowDetailsFragment(show.id)
                        findNavController().navigate(action)
                    }
                    binding.showsRecycler.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.VERTICAL,false)

                    binding.showsRecycler.adapter = adapter

                }
            }
        }


    }

    private fun showShows(){
        binding.noShowsIco.isVisible = false
        binding.noShowsText.isVisible = false
        binding.showsRecycler.isVisible = true
        binding.showHideShows.text = getString(R.string.ct_show_hide_btn_title_off)
    }

    private fun hideShows(){
        binding.noShowsIco.isVisible = true
        binding.noShowsText.isVisible = true
        binding.showsRecycler.isVisible = false
        binding.showHideShows.text = getString(R.string.ct_show_hide_btn_title_on)
    }

    //Profile bottom sheet

    private fun showProfileBottomSheet(){
        val dialog = BottomSheetDialog(this.requireView().context)

        val bottomSheetBinding = DialogProfileBinding.inflate(this.layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        setProfileImagePopup(bottomSheetBinding)

        bottomSheetBinding.txtUsername.text = sharedPreferences.getString(getString(R.string.ct_username),"")

        //Change profile picture btn

        bottomSheetBinding.btnChangeProfilePic.setOnClickListener {
            val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //setProfileImage(bottomSheetBinding)
            setProfileImagePopup(bottomSheetBinding)

            //if(takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(takePictureIntent,123)
            dialog.dismiss()
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
                putString(getString(R.string.REMEMBERED_PHOTO),viewModel.encodeBitmapToString(data))
            }
            Toast.makeText(context,"Profile photo set !",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProfileImagePopup(binding: DialogProfileBinding){
        val encoded = viewModel.getStringFromBitmap(
            BitmapFactory.decodeResource(resources,
            R.drawable.ic_new_profile_large
        ))
        val profilePhoto = sharedPreferences.getString(getString(R.string.REMEMBERED_PHOTO), encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        Glide.with(this)
            .load(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            .circleCrop()
            .into(binding.imgProfile)
    }

    /*fun setProfileImage(binding: DialogProfileBinding){
        val encoded = viewModel.getStringFromBitmap(
            BitmapFactory.decodeResource(resources,
                R.drawable.profile_ico
            ))
        val profilePhoto = sharedPreferences.getString(getString(R.string.REMEMBERED_PHOTO), encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)

        /*Glide.with(this)
            .load(BitmapFactory.decodeByteArray(decoded,0,decoded.size))
            .circleCrop()
            .into(_navBinding.btnProfile)*/

    }*/

    //Logging off



    private fun createAlert(dialog: BottomSheetDialog){


        val builder = AlertDialog.Builder(activity)

        builder.setTitle(getString(R.string.ct_alert_logout_title))
            .setMessage(getString(R.string.ct_alert_logout_description))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ct_alert_logout_positive)){_,_ ->
                logOut()

                findNavController().navigate(R.id.to_loginFraagment)
                dialog.dismiss()

            }
            .setNegativeButton(getString(R.string.ct_alert_logout_negative)){dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun logOut(){
        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(getString(R.string.IS_REMEMBERED), false)
            putString(getString(R.string.REMEMBERED_USER), "")
            remove(getString(R.string.REMEMBERED_PHOTO))
        }
    }


    //Has internet check
    private fun hasInternet(): Boolean{
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
}
