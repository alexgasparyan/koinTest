package com.armdroid.playground.home

import com.armdroid.playground.base.BaseFragment

class HomeFragment : BaseFragment<HomeContract.Presenter>(), HomeContract.View {

    override val presenterClass = HomeContract.Presenter::class
}