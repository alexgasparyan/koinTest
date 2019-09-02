package com.armdroid.playground

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin


class KoinUnitTest {

    lateinit var koinApplication: KoinApplication

    @Before
    fun before() {
        koinApplication = startKoin {
            modules(App().modules)
        }
    }

    @Test
    fun testDI() {
        koinApplication.checkDI()
    }

    @After
    fun after() {
        koinApplication.close()
    }
}
