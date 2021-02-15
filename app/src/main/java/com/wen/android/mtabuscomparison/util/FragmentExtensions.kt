package com.wen.android.mtabuscomparison.util

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

@Throws(IllegalStateException::class)
fun FragmentManager.findNavController(@IdRes id: Int): NavController {
    val fragment = findFragmentById(id) ?: throw IllegalStateException("Fragment is not found for id:$id")
    return NavHostFragment.findNavController(fragment)
}