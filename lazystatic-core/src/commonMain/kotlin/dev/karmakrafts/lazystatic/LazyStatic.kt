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
internal val tlLazyStaticValues: ThreadLocalRef<HashMap<SourceLocation, Any?>> = ThreadLocalRef()

@PublishedApi
internal val lazyStaticValues: ConcurrentMutableMap<SourceLocation, Any?> = ConcurrentMutableMap()

enum class LazyStaticStorage(
    @PublishedApi internal val getter: (SourceLocation) -> Any?,
    @PublishedApi internal val setter: (SourceLocation, Any?) -> Unit,
) {
    // @formatter:off
    THREAD_LOCAL({ tlLazyStaticValues.getOrPut { HashMap() }[it] },
                 { location, value -> tlLazyStaticValues.getOrPut { HashMap() }[location] = value }),
    ATOMIC      (lazyStaticValues::get, lazyStaticValues::set);
    // @formatter:on
}

inline fun <reified T> lazyStatic( // @formatter:off
    location: SourceLocation = SourceLocation.here(),
    storage: LazyStaticStorage = LazyStaticStorage.ATOMIC,
    block: () -> T
): T { // @formatter:on
    var value = storage.getter(location)
    if (value == null) {
        value = block()
        storage.setter(location, value)
    }
    return value as T
}