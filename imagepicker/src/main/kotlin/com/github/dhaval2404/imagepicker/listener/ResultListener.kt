package com.github.dhaval2404.imagepicker.listener

/**
 * Generic Class To Listen Async Result
 *
 * @author Dhaval Patel
 * @since 04 January 2018
 * @version 1.0
 */
internal interface ResultListener<T> {
    fun onResult(t: T?)
}
