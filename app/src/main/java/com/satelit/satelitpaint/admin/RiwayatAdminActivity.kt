package com.satelit.satelitpaint.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.satelit.satelitpaint.R
import com.satelit.satelitpaint.adapter.PageAdapter
import com.satelit.satelitpaint.admin.uitab.DalamProsesFragment
import com.satelit.satelitpaint.admin.uitab.SelesaiProsesFragment
import kotlinx.android.synthetic.main.activity_riwayat_admin.*

class RiwayatAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_admin)

        view_pesanan.adapter = PageAdapter(supportFragmentManager)
        tab_pesanan!!.setupWithViewPager(view_pesanan)
        tab_pesanan!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        btnback.setOnClickListener {
            finish()
        }
    }





}