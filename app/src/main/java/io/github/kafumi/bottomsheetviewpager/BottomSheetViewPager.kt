package io.github.kafumi.bottomsheetviewpager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

class BottomSheetViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    constructor(context: Context) : this(context, null)

    // Need to access package-private `position` field of `ViewPager.LayoutParams` to determine
    // which child view is the view for currently selected item from `ViewPager.getCurrentItem()`.
    private val positionField: Field =
        ViewPager.LayoutParams::class.java.getDeclaredField("position").also {
            it.isAccessible = true
        }

    init {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                // Need to call requestLayout() when selected page is changed so that
                // `BottomSheetBehavior` calls `findScrollingChild()` and recognizes the new page
                // as the "scrollable child".
                requestLayout()
            }
        })
    }

    override fun getChildAt(index: Int): View {
        val stackTrace = Throwable().stackTrace
        val calledFromFindScrollingChild = stackTrace.getOrNull(1)?.let {
            it.className == "com.google.android.material.bottomsheet.BottomSheetBehavior" &&
                    it.methodName == "findScrollingChild"
        }
        if (calledFromFindScrollingChild != true) {
            return super.getChildAt(index)
        }

        // Swap index 0 and `currentItem`
        val currentView = getCurrentView() ?: return super.getChildAt(index)
        return if (index == 0) {
            currentView
        } else {
            var view = super.getChildAt(index)
            if (view == currentView) {
               view = super.getChildAt(0)
            }
            return view
        }
    }

    private fun getCurrentView(): View? {
        for (i in 0 until childCount) {
            val child = super.getChildAt(i)
            val lp = child.layoutParams as? ViewPager.LayoutParams
            if (lp != null) {
                val position = positionField.getInt(lp)
                if (!lp.isDecor && currentItem == position) {
                    return child
                }
            }
        }
        return null
    }
}
