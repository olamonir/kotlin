/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.lightClasses.accessors;

import com.intellij.testFramework.LightProjectDescriptor
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.asJava.LightClassUtil
import org.jetbrains.kotlin.asJava.elements.KtLightMethod
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinWithJdkAndRuntimeLightProjectDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.test.JUnit3WithIdeaConfigurationRunner;
import org.junit.runner.RunWith;

@RunWith(JUnit3WithIdeaConfigurationRunner::class)
class LightClassAccessorsTest : KotlinLightCodeInsightFixtureTestCase() {
    fun testStressPropertyAccessor() {
        val file = myFixture.configureByText(
            "test.kt",
            //language=kotlin
            """
            class A {
                val a = 1
                val b = 2
            }
            """.trimIndent()
        )
        val declarations = mutableListOf<KtDeclaration>()
        file.accept(namedDeclarationRecursiveVisitor { declaration ->
            declarations.add(declaration)
        })
        val declarationWithMethods = declarations.map { Pair(it, it.toLightMethods())}
        for ((declaration, methods) in declarationWithMethods) {
            when (declaration) {
                is KtPropertyAccessor, is KtFunction, is KtProperty, is KtParameter -> {
                    val allMethods = LightClassUtil.getWrappingClasses(declaration).flatMap { it.methods.asSequence() }
                    val sequence = allMethods.filterIsInstance<KtLightMethod>().filter { it.kotlinOrigin === declaration }.toList()
                    assertThat(methods).isEqualTo(sequence)
                }
            }
        }
    }


    override fun getProjectDescriptor(): LightProjectDescriptor {
        return KotlinWithJdkAndRuntimeLightProjectDescriptor.INSTANCE
    }
}