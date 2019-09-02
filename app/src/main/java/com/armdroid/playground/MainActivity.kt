package com.armdroid.playground

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.armdroid.playground.common.FirstCommonFragment
import com.armdroid.playground.common.SecondCommonFragment
import com.armdroid.playground.home.HomeFragment

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, getFragment(view.id))
            .commit()
    }

    private fun getFragment(@IdRes id: Int) = when (id) {
        R.id.homeButton -> HomeFragment()
        R.id.common1Button -> FirstCommonFragment()
        else -> SecondCommonFragment()
    }
}