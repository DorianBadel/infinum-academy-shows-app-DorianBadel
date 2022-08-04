package com.example.shows_your_name.database

import androidx.room.TypeConverter
import com.example.shows_your_name.models.User
import org.json.JSONObject

class UserTypeConverter {
    @TypeConverter
    fun toUserJson(user: User): String{
        return JSONObject().apply{
            put("id",user.id)
            put("email",user.email)
            if(user.imageUrl == null){
                put("image_url","no_photo")
            }else{
                put("image url",user.imageUrl)
            }
        }.toString()
    }

    @TypeConverter
    fun toUser(user: String): User{
        val json = JSONObject(user)
        return User(json.getString("id"),json.getString("email"),json.getString("image_url"))
    }
}