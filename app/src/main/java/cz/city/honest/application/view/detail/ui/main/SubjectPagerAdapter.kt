package cz.city.honest.application.view.detail.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import cz.city.honest.application.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SubjectPagerAdapter(private val context: Context, private val fm: FragmentManager) :
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
            0 to ShowSubjectCostFragment(),
            1 to ShowSubjectSuggestionsFragment()
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

class SubjectTabFragment {

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
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