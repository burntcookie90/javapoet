package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

data class JavaPoetValue(val modifiers : Set<Modifier>,
                         val type : TypeName,
                         val name : String,
                         val value : Any? = null) {
  var javaDoc : String? = null
}