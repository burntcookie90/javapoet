package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.ClassName

fun Class<*>.getClassName() = ClassName.get(this)
