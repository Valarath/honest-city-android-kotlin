package cz.city.honest.application.view.user.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import cz.city.honest.application.R

private val TAB_TITLES = arrayOf(
    //R.string.user_detail_settings,
    R.string.user_detail_suggestions
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class UserDetailPagerAdapter(private val context: Context, private val fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        POSITION_TO_FRAGMENT_MAP[position]!!

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return POSITION_TO_FRAGMENT_MAP.size
    }

    companion object {
        val POSITION_TO_FRAGMENT_MAP = mapOf<Int, Fragment>(
            0 to UserDetailSuggestionsFragment()
        )

        private const val ARG_SECTION_NUMBER = "section_number"


        @JvmStatic
        fun <T : Fragment> newInstance(sectionNumber: Int, tabFragment: Class<T>): T =
            tabFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }


    }
}