package com.satelit.satelitpaint.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.satelit.satelitpaint.admin.uitab.DalamProsesFragment
import com.satelit.satelitpaint.admin.uitab.PengirimanFragment
import com.satelit.satelitpaint.admin.uitab.SelesaiProsesFragment

class PageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private var fragmentList= ArrayList<Fragment>()
    private var titleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                DalamProsesFragment()
            }
            1->{
                PengirimanFragment()
            }
            2->{
                SelesaiProsesFragment()
            }

            else->
                DalamProsesFragment()
        }

    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0->"Dalam Proses"
            1->"Pengiriman"
            2->"Selesai"
            else ->"Dalam Proses"
        }
    }

    fun addFragment(
        fragment: Fragment?,
        title: String?
    ) {
        fragmentList.add(fragment!!)
        titleList.add(title!!)
    }

}