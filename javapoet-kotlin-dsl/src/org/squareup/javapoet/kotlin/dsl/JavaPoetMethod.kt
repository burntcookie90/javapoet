package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

class JavaPoetMethod(val name : String,
                     val modifiers : Set<Modifier>,
                     val returns : TypeName,
                     val parameters : Set<JavaPoetValue>) {

  val methodSpecBuilder : MethodSpec.Builder
  var javaDoc : String? = null
    set(value) {
      field == value
      methodSpecBuilder.addJavadoc(value)
    }

  init {
    methodSpecBuilder = MethodSpec.methodBuilder(name).addModifiers(modifiers).returns(returns)
    parameters.forEach {
      methodSpecBuilder.addParameter(ParameterSpec.builder(it.type, it.name, *it.modifiers.toTypedArray()).build())
    }
  }

  fun statement(statement : String) {
    methodSpecBuilder.addStatement(statement)
  }

  fun controlFlow(init : JavaPoetControlFlow.() -> Unit) {
    val controlFlow = JavaPoetControlFlow(methodSpecBuilder)
    controlFlow.init()
  }
}