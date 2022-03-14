package com.satelit.satelitpaint.webservice

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object{
        private var retrofit : Retrofit? = null
        private var opt = OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30,TimeUnit.SECONDS)

        }.build()

        private fun getClient() : Retrofit{
            return if (retrofit ==null){
                retrofit = Retrofit.Builder().apply {
                    client(opt)
                    baseUrl("http://www.satelitpaint.pws-solution.com/public/api/")
//                    baseUrl("http://192.168.1.10:8000/api/")
                    addConverterFactory(GsonConverterFactory.create())
                }.build()
                retrofit!!
            }else{
                retrofit!!
            }
        }

        fun instance() = getClient().create(ApiService::class.java)
    }

}
