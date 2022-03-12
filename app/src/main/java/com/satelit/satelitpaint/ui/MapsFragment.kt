package com.satelit.satelitpaint.ui

import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.admin.auth.LoginActivity
import com.satelit.satelitpaint.databinding.FragmentMapsBinding
import org.jetbrains.anko.support.v4.startActivity

class MapsFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-7.3452872, 112.6294327)
        mMap.addMarker(
            MarkerOptions()
            .position(sydney)
            .title("Satelite Paint"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


}