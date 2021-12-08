package com.lensy.library.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * Developed by
 * @author Aleksandr Artemov
 */

fun <T> LiveData<T>.getDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if ((obj == null && lastObj != null)
                || obj != lastObj
            ) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })
    return distinctLiveData
}

fun <A, B> combineLiveData(
    a: LiveData<A>,
    b: LiveData<B>
): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

fun <A, B, C> combineLiveData(
    a: LiveData<A>,
    b: LiveData<B>,
    c: LiveData<C>
): LiveData<Triple<A, B, C>> {
    return MediatorLiveData<Triple<A, B, C>>().apply {
        var lastA: A? = null
        var lastB: B? = null
        var lastC: C? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            val localLastC = lastC
            if (localLastA != null && localLastB != null && localLastC != null)
                this.value = Triple(localLastA, localLastB, localLastC)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
        addSource (c) {
            lastC = it
            update()
        }
    }
}

fun <A, B, C> combineLiveDataTransform(
    a: LiveData<A>,
    b: LiveData<B>,
    transform: (A, B) -> C
): LiveData<C> {
    return MediatorLiveData<C>().apply {
        var first: A? = null
        var second: B? = null

        fun update() {
            val localFirst = first
            val localSecond = second

            if (localFirst != null && localSecond != null)
                this.value = transform(localFirst, localSecond)
        }

        addSource(a) {
            synchronized(this) {
                first = it
                update()
            }
        }

        addSource(b) {
            synchronized(this) {
                second = it
                update()
            }
        }
    }
}
