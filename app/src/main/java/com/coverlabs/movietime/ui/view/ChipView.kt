package com.coverlabs.movietime.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.ViewChipBinding

class ChipView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {

    private lateinit var binding: ViewChipBinding

    init {
        createViews(context, attributeSet)
    }

    var leftText: CharSequence? = null
        set(value) {
            binding.tvLeft.text = value
            field = value
        }

    var rightText: CharSequence? = null
        set(value) {
            binding.tvRight.text = value
            field = value
        }

    var leftDrawable: Drawable? = null
        set(value) {
            binding.tvLeft.setCompoundDrawablesRelativeWithIntrinsicBounds(
                value,
                null,
                null,
                null
            )
            field = value
        }

    var rightDrawable: Drawable? = null
        set(value) {
            binding.tvRight.setCompoundDrawablesRelativeWithIntrinsicBounds(
                value,
                null,
                null,
                null
            )
            field = value
        }

    private fun createViews(
        context: Context,
        attributeSet: AttributeSet? = null
    ) {
        binding = ViewChipBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.ChipView
            )

            leftText = typedArray.getString(R.styleable.ChipView_leftText).orEmpty()
            rightText = typedArray.getString(R.styleable.ChipView_rightText).orEmpty()
            val leftDrawableId = typedArray.getResourceId(R.styleable.ChipView_leftDrawable, -1)
            val rightDrawableId = typedArray.getResourceId(R.styleable.ChipView_rightDrawable, -1)

            if (leftDrawableId != -1) {
                leftDrawable = ResourcesCompat.getDrawable(resources, leftDrawableId, null)
            }

            if (rightDrawableId != -1) {
                rightDrawable = ResourcesCompat.getDrawable(resources, rightDrawableId, null)
            }

            typedArray.recycle()
        }
    }
}