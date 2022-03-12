package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName

data class PostNotif(

    @SerializedName("body"  ) var body  : String? = null,
    @SerializedName("title" ) var title : String? = null
)