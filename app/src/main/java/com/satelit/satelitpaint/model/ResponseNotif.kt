package com.satelit.satelitpaint.model

data class ResponseNotif(
    var multicast_id : Double,
    var success : Int,
    var failure : Int,
    var canonical_ids : Int,
    var results : List<NotifResultModel>
)