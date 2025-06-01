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

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.uuid.Uuid

class LazyStaticTest {
    fun testFunction(): Uuid {
        return lazyStatic { Uuid.random() }
    }

    @Test
    fun `lazyStatic retains object value between calls`() {
        val id = testFunction()
        for (i in 0..<10) {
            testFunction() shouldBe id
        }
    }

    private var testValue: Int = 0

    fun testFunction2() {
        lazyStatic<Unit> { testValue++ }
    }

    @Test
    fun `lazyStatic with Unit should only be invoked once`() {
        testFunction2()
        testValue shouldBe 1
        testFunction2()
        testValue shouldBe 1
    }
}