package com.armdroid.playground.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.armdroid.annotations.KoinCheckDI
import com.armdroid.annotations.KoinNamed
import com.armdroid.playground.R
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

abstract class BaseFragment<T : Any> : Fragment(), BaseContract.View {

    protected val presenter: T by lazy {
        getKoin().get<T>(presenterClass, presenterNamed?.let { named(it) }, null)
    }

    @get:KoinCheckDI
    abstract val presenterClass: KClass<T>

    @KoinNamed
    open val presenterNamed: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inject here
        presenter.let { }
        (view as TextView).text = this.javaClass.name
    }
}