package com.harputoglu.orhun.util

class NativeLib {
    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("orhun")
        }
    }
}
