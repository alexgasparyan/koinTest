package com.armdroid.playground.base

import com.armdroid.playground.Repository
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

abstract class BasePresenter : KoinComponent, BaseContract.Presenter {

    //this is fine, test should fail if BeanDefinition does not exist
    val repositoryByGet = get<Repository>()

    //this is dangerous, test cannot decide if Repository can be provided or not as inject is lazy.
    // AVOID USING by inject or simply inject by constructor
    val repositoryByInject by inject<Repository>()
}