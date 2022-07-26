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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shows_your_name.databinding.DialogProfileBinding
import com.example.shows_your_name.databinding.FragmentShowsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream


class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesValue: SharedPreferences
    private lateinit var sharedPreferencesProfileImage: SharedPreferences

    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val REMEMBERED_PHOTO = "REMEMBERED_PHOTO"

    private var shows = listOf(
        Show(1,"Office","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_office),
        Show(2,"Stranger Things","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_stranger_things ),
        Show(3,"Krv nije voda","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_krv_nije_voda )
    )

    private lateinit var adapter: ShowsAdapter
    lateinit var username : String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        sharedPreferencesValue = requireContext().getSharedPreferences("Username", Context.MODE_PRIVATE)
        sharedPreferencesProfileImage = requireContext().getSharedPreferences("Image",Context.MODE_PRIVATE)
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

        username = arguments?.getString("Username").toString()
        sharedPreferencesProfileImage = requireContext().getSharedPreferences("HAS_PHOTO",Context.MODE_PRIVATE)

        binding.showHideShows.setOnClickListener{
            if(binding.showHideShows.text == "Hide"){
                binding.noShowsIco.isVisible = true
                binding.noShowsText.isVisible = true
                binding.showsRecycler.isVisible = false
                binding.showHideShows.text = "Show"
            } else if (binding.showHideShows.text == "Show"){
                binding.noShowsIco.isVisible = false
                binding.noShowsText.isVisible = false
                binding.showsRecycler.isVisible = true
                initShowsRecycler()
                binding.showHideShows.text = "Hide"
            }
        }

        if(shows.isNullOrEmpty()){
            binding.noShowsIco.isVisible = true
            binding.noShowsText.isVisible = true
        } else{
            binding.noShowsIco.isVisible = false
            binding.noShowsText.isVisible = false
            initShowsRecycler()
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
        adapter = ShowsAdapter(shows) { show ->

            val bundle = bundleOf(
                "Title" to show.title,
                "Description" to show.desc,
                "Image" to show.imageResourceId,
                "ID" to show.ID,
                "Username" to arguments?.getString("Username").toString()
            )

            findNavController().navigate(R.id.to_showDetailsFragment,bundle)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL,false)

        binding.showsRecycler.adapter = adapter
    }

    private fun showProfileBottomSheet(){
        val dialog = BottomSheetDialog(requireView().context)

        val bottomSheetBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.txtUsername.text = arguments?.getString("Username").toString()

        //Change profile picture btn

        bottomSheetBinding.btnChangeProfilePic.setOnClickListener {
            val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(true){
                startActivityForResult(takePictureIntent,123)
                dialog.dismiss()
            }
        }
        val byteArr = ByteArrayOutputStream()
        val img = BitmapFactory.decodeResource(getResources(), R.drawable.profile_ico)
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        val encoded = Base64.encodeToString(base64,Base64.DEFAULT)
        val profilePhoto = sharedPreferencesProfileImage.getString(REMEMBERED_PHOTO, encoded )
        val decoded = Base64.decode(profilePhoto, Base64.DEFAULT)
        bottomSheetBinding.imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))

        //Logout button

        bottomSheetBinding.btnDialogLogOut.setOnClickListener{

            var builder = AlertDialog.Builder(activity)

            builder.setTitle("You will leave your shows behind")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(true)
                .setPositiveButton("Yes"){_,_ ->
                    //Log out
                    sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
                    sharedPreferencesValue = requireContext().getSharedPreferences("Username", Context.MODE_PRIVATE)
                    sharedPreferences.edit {
                        putBoolean(IS_REMEMBERED, false)
                    }
                    sharedPreferencesValue.edit{
                        putString(REMEMBERED_USER, "")
                    }
                    sharedPreferencesProfileImage.edit{
                        val byteArr = ByteArrayOutputStream()
                        val img = BitmapFactory.decodeResource(getResources(), R.drawable.profile_ico)
                        img.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
                        val base64 = byteArr.toByteArray()
                        val encoded = Base64.encodeToString(base64,Base64.DEFAULT)
                        putString(REMEMBERED_PHOTO,encoded)
                    }

                    findNavController().navigate(R.id.to_loginFraagment)
                    dialog.dismiss()
                    //Log out

                }
                .setNegativeButton("No"){dialogInterface,it ->
                    dialogInterface.cancel()
                }
                .show()


        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            sharedPreferencesProfileImage.edit{
                //An image becomes a string voodoo
                val byteArr = ByteArrayOutputStream()
                val img = data?.extras?.get("data") as Bitmap
                img.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
                val base64 = byteArr.toByteArray()
                val encoded = Base64.encodeToString(base64,Base64.DEFAULT)
               putString(REMEMBERED_PHOTO,encoded)
            }
        }
    }

}