package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.*
import com.squareup.javapoet.TypeName.BOOLEAN
import com.squareup.javapoet.TypeName.INT
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.*

class JavaPoetType(val modifiers : Set<Modifier>,
                   val name : String) {
  val constructors = arrayListOf<JavaPoetConstructor>()
  val methods = arrayListOf<JavaPoetMethod>()
  val fields = arrayListOf<JavaPoetValue>()

  fun field(modifiers : Set<Modifier> = setOf(DEFAULT),
            type : TypeName,
            name : String,
            value : Any? = null,
            init : JavaPoetValue.() -> Unit = {}) {

    val jPValue= JavaPoetValue(modifiers, type, name, value)
    jPValue.init()
    fields.add(jPValue)
  }

  fun constructor(modifiers : Set<Modifier> = setOf(DEFAULT),
                  parameters : Set<JavaPoetValue> = emptySet(),
                  init : JavaPoetConstructor.() -> Unit = {}) {
    val constructor = JavaPoetConstructor(modifiers, parameters)
    constructor.init()
    constructors.add(constructor)
  }

  fun method(modifiers : Set<Modifier> = setOf(DEFAULT),
             returns : TypeName = TypeName.VOID,
             name : String,
             parameters : Set<JavaPoetValue> = emptySet(),
             init : JavaPoetMethod.() -> Unit) {

    val javaPoetMethod = JavaPoetMethod(name, modifiers, returns, parameters)
    javaPoetMethod.init()
    methods.add(javaPoetMethod)
  }
}

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

  fun statement(statement : String){
    methodSpecBuilder.addStatement(statement)
  }

}

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

data class JavaPoetValue(val modifiers : Set<Modifier>,
                         val type : TypeName,
                         val name : String,
                         val value : Any? = null) {
  var javaDoc : String? = null
}

class JavaPoetControlFlow(val methodSpecBuilder : MethodSpec.Builder) {

  fun statement(statement : String) {
    methodSpecBuilder.addStatement(statement)
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

inline fun classType(modifiers : Set<Modifier> = setOf(DEFAULT),
                     name : String,
                     init : JavaPoetType.() -> Unit) : TypeSpec {
  val type = JavaPoetType(modifiers, name)
  type.init()

  val typeSpecBuilder = TypeSpec.classBuilder(name).addModifiers(*modifiers.toTypedArray())

  type.fields.forEach {
    val builder = FieldSpec.builder(it.type, it.name, *it.modifiers.toTypedArray())
    it.value?.let {
      builder.initializer("\$L", it)
    }
    it.javaDoc?.let {
      builder.addJavadoc(it)
    }
    typeSpecBuilder.addField(builder.build())
  }

  type.constructors.forEach {
    typeSpecBuilder.addMethod(it.methodSpecBuilder.build())
  }

  type.methods.forEach { typeSpecBuilder.addMethod(it.methodSpecBuilder.build()) }

  return typeSpecBuilder.build()
}

fun main(args : Array<String>) {
  println(classType(setOf(PUBLIC), "TestDsl") {

    field(setOf(PROTECTED, FINAL), BOOLEAN, "isProtected", true) {
      javaDoc = "this is a protected final field\n"
    }
    field(setOf(PRIVATE), BOOLEAN, "isPrivate")

    constructor(setOf(PUBLIC)) //no init block gives default empty constructor

    constructor(setOf(PUBLIC), setOf(JavaPoetValue(setOf(FINAL), BOOLEAN, "isPrivate"))) {
      javaDoc = "constructor that takes a parameter and sets the corresponding field\n"
      statement("this.isPrivate = isPrivate")
    }

    method(setOf(PRIVATE, FINAL), INT, "returnsInteger") {
      javaDoc = "this method returns an integer\n"

      statement("int total = 0")

      controlFlow() {
        begin("if(total == 0)") {
          statement("return 1")
        }
        next("else") {
          statement("return 2")
        }
        end()
      }
    }

    method(setOf(PUBLIC), BOOLEAN, "complexControlFlow", setOf(JavaPoetValue(setOf(FINAL), INT, "in"))){
      javaDoc = "This method shows arbitrary complex control flow\n"
      controlFlow {
        begin("if(in > 0)"){
          controlFlow {
            begin("if(in < 5)"){
              statement("return true")
            }
            next("else if (in < 7)") {
              statement("return false")
            }
            next("else if (in < 10)"){
              statement("return true")
            }
            end()
          }
        }
        next("else"){
          statement("return false")
        }
        end()
      }
    }
  }.toString())
}

