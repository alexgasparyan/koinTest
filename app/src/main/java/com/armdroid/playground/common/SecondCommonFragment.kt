package com.armdroid.playground.common

import com.armdroid.playground.base.BaseFragment

class SecondCommonFragment : BaseFragment<CommonContract.Presenter>(), CommonContract.View {

    override val presenterClass = CommonContract.Presenter::class

    override val presenterNamed = "common2"
}