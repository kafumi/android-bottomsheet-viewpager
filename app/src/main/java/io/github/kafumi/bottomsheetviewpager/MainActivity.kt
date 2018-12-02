package io.github.kafumi.bottomsheetviewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = Adapter(supportFragmentManager)
    }

    class Adapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getItem(position: Int): Fragment {
            return PageFragment.newInstance()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Tab ${position + 1}"
        }
    }

    companion object {
        private const val PAGE_COUNT = 5
    }
}
