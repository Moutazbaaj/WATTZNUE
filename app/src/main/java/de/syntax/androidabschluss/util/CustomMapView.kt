package de.syntax.androidabschluss.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView


/**
 * CustomMapView is a subclass of MapView that allows customization of touch event handling.
 *
 * It prevents the parent view from intercepting touch events while the user interacts with the map.
 *
 * @constructor Creates a CustomMapView.
 * @param context The context in which the CustomMapView operates.
 * @param attrs An AttributeSet for configuring the CustomMapView.
 * @param defStyle An Int representing the default style for the CustomMapView.
 */
class CustomMapView : MapView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs!!,
        defStyle
    )


    /**
     * Override of the dispatchTouchEvent method to customize touch event handling.
     *
     * This method prevents the parent view from intercepting touch events while the user interacts
     * with the map.
     *
     * @param ev The MotionEvent to handle.
     * @return A Boolean indicating whether the event was consumed.
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // User starts touching the screen, disable scrolling in the parent
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                // User moves their finger, disable scrolling in the parent
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> {
                // User lifts their finger, enable scrolling in the parent
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
