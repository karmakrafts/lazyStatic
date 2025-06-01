/*
 * Copyright 2025 Karma Krafts & associates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.karmakrafts.lazystatic

import co.touchlab.stately.collections.ConcurrentMutableMap
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import dev.karmakrafts.introspekt.util.SourceLocation

@Suppress("UNCHECKED_CAST")
private fun <T> ThreadLocalRef<T>.getOrPut(initializer: () -> T): T {
    if (value == null) {
        value = initializer()
    }
    return value as T
}

@PublishedApi
internal val tlLazyStaticValues: ThreadLocalRef<HashMap<Int, Any?>> = ThreadLocalRef()

@PublishedApi
internal val lazyStaticValues: ConcurrentMutableMap<Int, Any?> = ConcurrentMutableMap()

/**
 * Defines the storage strategy for lazy static values.
 *
 * @property getter Function to retrieve a value from storage by its location
 * @property setter Function to store a value at a specific location
 */
enum class LazyStaticStorage(
    @PublishedApi internal val getter: (Int) -> Any?,
    @PublishedApi internal val setter: (Int, Any?) -> Unit,
) {
    // @formatter:off
    /**
     * Thread-local storage strategy.
     * Values are stored in thread-local storage, meaning each thread has its own copy.
     * This is useful when you need thread isolation for the lazy values.
     */
    THREAD_LOCAL({ tlLazyStaticValues.getOrPut { HashMap() }[it] },
                 { location, value -> tlLazyStaticValues.getOrPut { HashMap() }[location] = value }),

    /**
     * Atomic storage strategy.
     * Values are stored in a concurrent map shared across all threads.
     * This is useful when you need the same value to be shared across different threads.
     */
    ATOMIC      (lazyStaticValues::get, lazyStaticValues::set);
    // @formatter:on
}

/**
 * Creates a lazily initialized static value that is computed only once and then reused across multiple calls.
 *
 * This function provides a way to create values that are:
 * - Initialized lazily (only when first accessed)
 * - Cached for subsequent calls
 *
 * @param T The type of value to be lazily initialized
 * @param key The source location hash that uniquely identifies this lazy static value.
 *                 Defaults to the current source location hash.
 * @param storage The storage strategy to use for this value.
 *                ATOMIC (default) shares the value across all threads.
 *                THREAD_LOCAL provides a separate value for each thread.
 * @param block The initialization function that computes the value on first access
 * @return The lazily initialized value, either newly computed or retrieved from cache
 */
inline fun <reified T> lazyStatic( // @formatter:off
    key: Int = SourceLocation.hereHash(),
    storage: LazyStaticStorage = LazyStaticStorage.ATOMIC,
    block: () -> T
): T { // @formatter:on
    var value = storage.getter(key)
    if (value == null) {
        value = block()
        storage.setter(key, value)
    }
    return value as T
}
