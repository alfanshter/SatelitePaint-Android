package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName

data class PostNotification(
    @SerializedName("to"           ) var to           : String?       = null,
    @SerializedName("collapse_key" ) var collapseKey  : String?       = null,
    @SerializedName("notification" ) var notification :PostNotif? = PostNotif(),
    @SerializedName("data"         ) var data         : PostNotifData?         = PostNotifData()
)