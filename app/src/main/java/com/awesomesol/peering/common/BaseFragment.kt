package com.awesomesol.peering.common

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    fun addFragment(fragment: Fragment, layoutId: Int, tag: String) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(
            layoutId,
            fragment,
            tag
        )
            .addToBackStack(null)
            .commit()
    }

    fun replaceFragment(fragment: Fragment, layoutId: Int, tag: String) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(
            layoutId,
            fragment,
            tag
        )
            .commit()
    }

    fun popFragment() {
        requireFragmentManager().popBackStack()
    }

}