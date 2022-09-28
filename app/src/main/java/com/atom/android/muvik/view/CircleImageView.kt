package com.atom.android.muvik.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView


class CircleImageView : CardView {

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val with = measuredWidth
        setMeasuredDimension(with, with)
        radius = with.toFloat() / 2
    }


}
