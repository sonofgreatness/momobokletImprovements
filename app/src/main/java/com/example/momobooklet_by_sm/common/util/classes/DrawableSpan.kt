package com.example.momobooklet_by_sm.common.util.classes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.view.View
import android.widget.Button
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.momobooklet_by_sm.R

/**
* This class is responsible for making progress bar appear in a button
* */
class DrawableSpan(val view: View, val marginStart: Int, var progressDrawable: CircularProgressDrawable) :DynamicDrawableSpan() {
private  var spannableString: SpannableString
    init {
        // create a SpannableString like "Loading [our_progress_bar]"
        let {
             spannableString = SpannableString("submitting ").apply {
                setSpan(it, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        val callback = object : Drawable.Callback {
            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            }
            override fun invalidateDrawable(who: Drawable) {
                (view as Button).invalidate()
            }
            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            }
        }
        progressDrawable = CircularProgressDrawable(view.context).apply {
            // let's use large style just to better see one issue
            setStyle(CircularProgressDrawable.LARGE)
            setColorSchemeColors(Color.WHITE)
            //bounds definition is required to show drawable correctly
            val size = (centerRadius + strokeWidth).toInt() * 2
            setBounds(0, 0, size, size)
        }
        progressDrawable.callback = callback
    }
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fontMetricsInt: Paint.FontMetricsInt?): Int {
            //get drawable dimensions
            val rect = drawable.bounds
            fontMetricsInt?.let {
            val fontMetrics = paint.fontMetricsInt
            // get a font height
            val lineHeight = fontMetrics.bottom - fontMetrics.top
            //make sure our drawable has height >= font height

            val drHeight = Math.max(lineHeight, rect.bottom - rect.top)
            val centerY = fontMetrics.top + lineHeight / 2
            //adjust font metrics to fit our drawable size

            fontMetricsInt.apply {
                ascent = centerY - drHeight / 2
                descent = centerY + drHeight / 2
                top = ascent
                bottom = descent
            }
        }

        //return drawable width which is in our case = drawable width + margin from text
        return rect.width() + marginStart
    }
    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.save()
        val fontMetrics = paint.fontMetricsInt
        // get font height. in our case now it's drawable height
        val lineHeight = fontMetrics.bottom - fontMetrics.top
        // adjust canvas vertically to draw drawable on text vertical center
        val centerY = y + fontMetrics.bottom - lineHeight / 2
        val transY = centerY - drawable.bounds.height() / 2
        // adjust canvas horizontally to draw drawable with defined margin from text
        canvas.translate(x + marginStart, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
    override fun getDrawable() = progressDrawable
   /*starts animating progress bar*/

    fun startProgress()
    {
       (view as Button).text= spannableString
       progressDrawable.start()
    }
/*stop animating progress bar*/
fun stopProgress()
    {
        (view as Button).text = view.resources.getString(R.string.submit)
        progressDrawable.stop()
    }
}