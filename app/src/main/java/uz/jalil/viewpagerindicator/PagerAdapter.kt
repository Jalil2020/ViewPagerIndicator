package uz.jalil.viewpagerindicator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *    Created by Jalil Boynazarov on 06.04.2023.
 */
class PagerAdapter(private val adsList: ArrayList<String>, fm: FragmentManager, lc: Lifecycle) :
    FragmentStateAdapter(fm, lc) {
    override fun getItemCount(): Int = adsList.size

    override fun createFragment(position: Int): Fragment = MyFragment().apply {
        val bundle = Bundle()
        adsList[position].apply {
            bundle.putString("imageUri", adsList[position])
        }
        arguments = bundle
    }
}