package com.example.shows_your_name

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.view.accessibility.AccessibilityViewCommand
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shows_your_name.databinding.FragmentShowsBinding
import java.io.ByteArrayOutputStream

class ShowsViewModel : ViewModel(){

    //The list of data
    private val _listOfShowsLiveData = MutableLiveData<List<Show>>()
    val listOfShowsLiveData: LiveData<List<Show>> = _listOfShowsLiveData

    init {
        _listOfShowsLiveData.value = listOf(
            Show(1,"The Office","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_office),
            Show(2,"Stranger Things","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_stranger_things ),
            Show(3,"Krv nije voda","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",R.drawable.ic_krv_nije_voda )
        )
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val byteArr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArr)
        val base64 = byteArr.toByteArray()
        return Base64.encodeToString(base64, Base64.DEFAULT)
    }
}