package com.satelit.satelitpaint.webservice

import com.satelit.satelitpaint.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("nomorwa") nomorwa : String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("insertproduct")
    fun insertproduct(
        @Header("Authorization") token : String,
        @Field("nama") nama : String,
        @Field("foto") foto: String,
        @Field("kategori") kategori: String,
        @Field("deskripsi") deskripsi: String,
        @Field("stok") stok: Int,
        @Field("mingrosir") mingrosir: Int,
        @Field("maxgrosir") maxgrosir: Int,
        @Field("harga") harga: Int,
        @Field("hargagrosir") hargagrosir: Int,

    ): Call<ResponseModel>

    //Get Bukti Transfer
    @GET("getproduct")
    fun getproduct(): Call<ResponseProduk>

    //Get Bukti Transfer
    @GET("getproduct")
    fun getproductslider(): Call<ProdukSliderResponse>

    //Get Bukti Transfer
    @GET("getprodukoption")
    fun getprodukoption(
        @Query("kategori") kategori: String
    ): Call<ResponseProduk>

    //UPDATE PRODUK
    @FormUrlEncoded
    @POST("updateproduct")
    fun updateproduct(
        @Header("Authorization") token : String,
        @Field("id") id: Int,
        @Field("nama") nama : String,
        @Field("foto") foto: String,
        @Field("deskripsi") deskripsi: String,
        @Field("stok") stok: Int,
        @Field("mingrosir") mingrosir: Int,
        @Field("maxgrosir") maxgrosir: Int,
        @Field("harga") harga: Int,
        @Field("hargagrosir") hargagrosir: Int,

        ): Call<ResponseModel>

    //HAPUS PRODUK
    @FormUrlEncoded
    @POST("deleteproduct")
    fun deleteproduct(
        @Header("Authorization") token : String,
        @Field("id") id: Int
        ): Call<ResponseModel>


    //=======Cart======
    //AddCart
    @FormUrlEncoded
    @POST("addcart")
    fun addcart(
        @Field("idproduk") idproduk: Int,
        @Field("idusers") idusers: String,
        @Field("nomorpesanan") nomorpesanan: String,
        @Field("nama") nama: String,
        @Field("foto") foto: String,
        @Field("deskripsi") deskripsi: String,
        @Field("harga") harga: Int
    ): Call<ResponseModel>

    //Get Cart
    @GET("getcart")
    fun getcart(
        @Query("idusers") idusers: String
    ): Call<CartResponse>

    //PickCart
    @FormUrlEncoded
    @POST("pickcart")
    fun pickcart(
        @Field("id") id: Int,
        @Field("pickcart") pickcart: Int
    ): Call<ResponseModel>

    //UpdateJumlah
    @FormUrlEncoded
    @POST("updatejumlah")
    fun updatejumlah(
        @Field("id") id: Int,
        @Field("jumlah") jumlah: Int
    ): Call<ResponseModel>

    //hapusCart
    @FormUrlEncoded
    @POST("deletecart")
    fun deletecart(
        @Field("id") id: Int
    ): Call<ResponseModel>

    //hapusCart
    @FormUrlEncoded
    @POST("sumcart")
    fun sumcart(
        @Field("idusers") idusers: String
    ): Call<ResponseModel>

    //TRANSAKSI =======
    //Update Status (ADMIN)
    @FormUrlEncoded
    @POST("updatestatuscheckout")
    fun updatestatuscheckout(
        @Header("Authorization") token : String,
        @Field("id") id: Int,
        @Field("status") status: Int
    ): Call<ResponseModel>

    //Transaksi
    @FormUrlEncoded
    @POST("checkout")
    fun checkout(
        @Field("idusers") idusers: String,
        @Field("telepon") telepon: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        @Field("metodepembayaran") metodepembayaran: String,
        @Field("totalharga") totalharga: Int,
        @Field("diskon") diskon: Int,
        @Field("nomorpesanan") nomorpesanan: String,
        @Field("foto") foto: String
    ): Call<ResponseModel>

    //Cicilan
    @FormUrlEncoded
    @POST("cicilan")
    fun cicilan(
        @Field("idusers") idusers: String,
        @Field("telepon") telepon: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        @Field("metodepembayaran") metodepembayaran: String,
        @Field("totalharga") totalharga: Int,
        @Field("diskon") diskon: Int,
        @Field("nomorpesanan") nomorpesanan: String,
        @Field("foto") foto: String,
        @Field("hargacicilan") hargacicilan: Int,
        @Field("jumlahcicilan") jumlahcicilan: Int
    ): Call<ResponseModel>

    //Finish CIcilan
    @FormUrlEncoded
    @POST("finishcicilan")
    fun finishcicilan(
        @Field("nomorpesanan") nomorpesanan: String
    ): Call<ResponseModel>


    //Update status cicilan (ADMIN)
    @FormUrlEncoded
    @POST("updatestatuscicilan")
    fun updatestatuscicilan(
        @Field("id") id: Int,
        @Field("status") status: Int
    ): Call<ResponseModel>

    //Update status cicilan (ADMIN)
    @FormUrlEncoded
    @POST("updatesudahbayar")
    fun updatesudahbayar(
        @Field("id") id: Int,
        @Field("status") status: Int
    ): Call<ResponseModel>

    //Update status cicilan (ADMIN)
    @FormUrlEncoded
    @POST("updatejatuhtempo")
    fun updatejatuhtempo(
        @Field("id") id: Int,
        @Field("jatuhtempo") jatuhtempo: String
    ): Call<ResponseModel>

    //Get Detail Transaksi
    @GET("getcicilanakun")
    fun getcicilanakun(
        @Query("idusers") idusers: String
    ): Call<CicilanResponse>
    //Get Daftar Cicilan
    @GET("readbayarcicilan")
    fun readbayarcicilan(
        @Query("nomorpesanan") nomorpesanan: String
    ): Call<DaftarCicilanResponse>

    //Get Detail Transaksi
    @GET("getsumcicilanakun")
    fun getsumcicilanakun(
        @Query("nomorpesanan") nomorpesanan: String
    ): Call<ResponseModel>


    //Get Detail Transaksi
    @GET("getprodukcheckout")
    fun getprodukcheckout(
        @Query("idusers") idusers: String,
        @Query("nomorpesanan") nomorpesanan: String
    ): Call<CartResponse>

    //Get Detail Transaksi
    @GET("gettransaksibyidusers")
    fun gettransaksibyidusers(
        @Query("idusers") idusers: String
    ): Call<TransaksiAllResponse>

    //Get penghasilan semuanya
    @GET("totalpenghasilan")
    fun gettotalpenghasilan(
        @Header("Authorization") token : String
        ): Call<PenghasilanModel>

    //Get Penghasilan tahun ini
    @GET("totalpenghasilantahunini")
    fun gettotalpenghasilantahunini(
        @Header("Authorization") token : String,
        @Query("tahun") tahun : String
    ): Call<PenghasilanModel>

    //Get Penghasilan bulan ini
    @GET("totalpenghasilanbulanini")
    fun gettotalpenghasilanbulanini(
        @Header("Authorization") token : String,
        @Query("tahun") tahun : String,
        @Query("bulan") bulan : String
    ): Call<PenghasilanModel>

    //Get Penghasilan hari ini
    @GET("totalpenghasilanhariini")
    fun gettotalpenghasilanhariini(
        @Header("Authorization") token : String,
        @Query("tahun") tahun : String,
        @Query("bulan") bulan : String,
        @Query("hari") hari : String
    ): Call<PenghasilanModel>


    //Get Transaksi Admin
    @GET("gettransaksiadmin")
    fun gettransaksiadmin(
        @Header("Authorization") token : String
    ): Call<TransaksiAllResponse>

    //Get Transaksi Admin by Dalam proses
    @GET("gettransaksibystatusadmin")
    fun gettransaksibystatusadmin(
        @Header("Authorization") token : String,
        @Query("status") status : Int
    ): Call<TransaksiAllResponse>

    //Slider
    @FormUrlEncoded
    @POST("insertslider")
    fun insertslider(
        @Field("nama") nama: String,
        @Field("harga") harga: Int,
        @Field("deskripsi") deskripsi: String,
        @Field("foto") foto: String,
        @Field("foto_produk") foto_produk: String,
        @Field("stok") stok: Int,
        @Field("id_produk") id_produk: Int
    ): Call<ResponseModel>

    //Get Slider
    @GET("getslider")
    fun getslider(): Call<SliderResponse>

    //DElete slider
    @FormUrlEncoded
    @POST("deleteslider")
    fun deleteslider(
        @Field("id") id: Int
    ): Call<ResponseModel>

    //===========+GAMBAR=============
    //Insert Gambar
    @FormUrlEncoded
    @POST("insertgambar")
    fun insertgambar(
        @Header("Authorization") token : String,
        @Field("foto") foto: String
    ): Call<ResponseModel>

    //Get Gambar
    @GET("getgambar")
    fun getgambar(
        @Header("Authorization") token : String
    ): Call<GambarModel>

    //Get Gambar Customer
    @GET("getgambarcustomer")
    fun getgambarcustomer(): Call<GambarModel>


    //Delete Gambar
    @FormUrlEncoded
    @POST("deletegambar")
    fun deletegambar(
        @Header("Authorization") token : String,
        @Field("foto") foto: String
    ): Call<ResponseModel>
    //===========END GAMBAR=============

    //=======NOTIFIKASI
    //Crate Notif
    @POST("send")
    fun createnotif(
        @Header("Authorization") api : String,
        @Body body : PostNotification
//        @Field("to") token: String,
//        @Field("collapse_key") collapse_key: String,
//        @Field("notification") notification: PostNotif,
//        @Field("data") data: PostNotifData,
    ): Call<ResponseNotif>

    //===========CICILIAN
    @GET("getcicilan")
    fun getcicilan(): Call<CicilanResponse>
}
