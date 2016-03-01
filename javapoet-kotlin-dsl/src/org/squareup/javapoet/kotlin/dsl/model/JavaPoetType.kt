package org.squareup.javapoet.kotlin.dsl.model

import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

class JavaPoetType(val modifiers : Set<Modifier>,
                   val name : String) {
  val constructors = arrayListOf<JavaPoetConstructor>()
  val methods = arrayListOf<JavaPoetMethod>()
  val fields = arrayListOf<JavaPoetValue>()
  var javaDoc : String? = null

  fun field(modifier : Modifier = Modifier.DEFAULT,
            type : TypeName,
            name : String,
            value : Any? = null,
            init : JavaPoetValue.() -> Unit = {})
          = field(setOf(modifier), type, name, value, init)
  fun field(modifiers : Set<Modifier> = setOf(Modifier.DEFAULT),
            type : TypeName,
            name : String,
            value : Any? = null,
            init : JavaPoetValue.() -> Unit = {}) {

    val jPValue = JavaPoetValue(modifiers, type, name, value)
    jPValue.init()
    fields.add(jPValue)
  }

  fun constructor(modifier : Modifier = Modifier.DEFAULT,
                  parameters : Set<JavaPoetValue> = emptySet(),
                  init : JavaPoetConstructor.() -> Unit = {})
          = constructor(setOf(modifier), parameters, init)

  fun constructor(modifiers : Set<Modifier> = setOf(Modifier.DEFAULT),
                  parameters : Set<JavaPoetValue> = emptySet(),
                  init : JavaPoetConstructor.() -> Unit = {}) {
    val constructor = JavaPoetConstructor(modifiers, parameters)
    constructor.init()
    constructors.add(constructor)
  }

  fun method(modifier : Modifier = Modifier.DEFAULT,
             returns : TypeName = TypeName.VOID,
             name : String,
             parameters : Set<JavaPoetValue> = emptySet(),
             init : JavaPoetMethod.() -> Unit)
          = method(setOf(modifier), returns, name, parameters, init)
  fun method(modifiers : Set<Modifier> = setOf(Modifier.DEFAULT),
             returns : TypeName = TypeName.VOID,
             name : String,
             parameters : Set<JavaPoetValue> = emptySet(),
             init : JavaPoetMethod.() -> Unit) {

    val javaPoetMethod = JavaPoetMethod(name, modifiers, returns, parameters)
    javaPoetMethod.init()
    methods.add(javaPoetMethod)
  }
}