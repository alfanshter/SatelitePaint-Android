package com.satelit.satelitpaint.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context?) {
    val privateMode = 0
    val privateName ="login"
    var Pref : SharedPreferences?=context?.getSharedPreferences(privateName,privateMode)
    var editor : SharedPreferences.Editor?=Pref?.edit()

    private val islogin = "login"
    fun setLogin(check: Boolean){
        editor?.putBoolean(islogin,check)
        editor?.commit()
    }

    fun getLogin():Boolean?
    {
        return Pref?.getBoolean(islogin,false)
    }

    private val isloginadmin = "token"
    fun setToken(check: String){
        editor?.putString(isloginadmin,check)
        editor?.commit()
    }

    fun getToken():String?
    {
        return Pref?.getString(isloginadmin,"")
    }

    private val isdevice = "device"
    fun setDevice(check: String){
        editor?.putString(isdevice,check)
        editor?.commit()
    }

    fun getDevice():String?
    {
        return Pref?.getString(isdevice,"")
    }
}
