package com.elhady.memories.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Created by islam elhady on 28-Feb-21.
 */
fun View.hideKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)

fun Context.loadHiRezThumbnail(
    uri: Uri?,
    image: ImageView
) = Glide.with(this)
    .load(uri)
    .override(500, 500)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .thumbnail(0.1f)
    .transition(DrawableTransitionOptions.withCrossFade(200))
    .into(image)