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
                put("image_url",user.imageUrl)
            }
        }.toString()
    }

    @TypeConverter
    fun toUser(user: String): User{
        val json = JSONObject(user)
        var image = "no"
        if(json.has("image_url")){
            image = json.getString("image_url")
        }
        return User(json.getString("id"),json.getString("email"),image )
    }
}