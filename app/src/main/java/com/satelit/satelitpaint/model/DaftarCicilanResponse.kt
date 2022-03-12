package com.satelit.satelitpaint.model

import com.google.gson.annotations.SerializedName

data class DaftarCicilanResponse(

	@field:SerializedName("data")
	val data: List<DaftarCicilanModel>? = null,

	@field:SerializedName("message")
	val message: String? = null
)
