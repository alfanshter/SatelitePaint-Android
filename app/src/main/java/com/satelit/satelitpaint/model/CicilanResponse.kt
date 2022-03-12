package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName

data class CicilanResponse(

	@field:SerializedName("data")
	val data: List<CicilanModel>? = null,

	@field:SerializedName("message")
	val message: String? = null
)
