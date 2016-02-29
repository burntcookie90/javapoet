package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import javax.lang.model.element.Modifier

class JavaPoetConstructor(val modifiers : Set<Modifier>,
                          val parameters : Set<JavaPoetValue>) {
  val methodSpecBuilder : MethodSpec.Builder
  var javaDoc : String? = null
    set(value) {
      field == value
      methodSpecBuilder.addJavadoc(value)
    }

  init {
    methodSpecBuilder = MethodSpec.constructorBuilder().addModifiers(modifiers)
    parameters.forEach {
      methodSpecBuilder.addParameter(ParameterSpec.builder(it.type, it.name, *it.modifiers.toTypedArray()).build())
    }
  }

  fun statement(statement : String) {
    methodSpecBuilder.addStatement(statement)
  }

}