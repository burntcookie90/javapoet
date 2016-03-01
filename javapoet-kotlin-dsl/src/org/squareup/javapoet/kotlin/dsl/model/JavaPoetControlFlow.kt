package org.squareup.javapoet.kotlin.dsl.model

import com.squareup.javapoet.MethodSpec

class JavaPoetControlFlow(val methodSpecBuilder : MethodSpec.Builder) {

  fun statement(statement : String) {
    methodSpecBuilder.addStatement(statement)
  }

  fun statement(format : String, vararg args : Any){
    methodSpecBuilder.addStatement(format, *args)
  }

  fun begin(condition : String, init : JavaPoetControlFlow.() -> Unit) {
    methodSpecBuilder.beginControlFlow(condition)
    val controlFlow = JavaPoetControlFlow(methodSpecBuilder)
    controlFlow.init()
  }

  fun next(condition : String, init : JavaPoetControlFlow.() -> Unit) {
    methodSpecBuilder.nextControlFlow(condition)
    val controlFlow = JavaPoetControlFlow(methodSpecBuilder)
    controlFlow.init()
  }

  fun end(condition : String = "") {
    if (condition.isEmpty()) {
      methodSpecBuilder.endControlFlow()
    }
    else {
      methodSpecBuilder.endControlFlow(condition)
    }
  }

}