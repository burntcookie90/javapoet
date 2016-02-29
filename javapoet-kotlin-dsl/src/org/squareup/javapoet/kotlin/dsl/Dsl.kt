package org.squareup.javapoet.kotlin.dsl

import com.squareup.javapoet.*
import com.squareup.javapoet.TypeName.BOOLEAN
import com.squareup.javapoet.TypeName.INT
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.*

inline fun classType(modifiers : Set<Modifier> = setOf(DEFAULT),
                     name : String,
                     init : JavaPoetType.() -> Unit) : TypeSpec {
  val type = JavaPoetType(modifiers, name)
  type.init()

  val typeSpecBuilder = TypeSpec.classBuilder(type.name).addModifiers(*type.modifiers.toTypedArray())

  type.javaDoc?.let { typeSpecBuilder.addJavadoc(it) }
  type.fields.forEach {
    val builder = FieldSpec.builder(it.type, it.name, *it.modifiers.toTypedArray())
    it.value?.let { builder.initializer("\$L", it) }
    it.javaDoc?.let { builder.addJavadoc(it) }
    typeSpecBuilder.addField(builder.build())
  }
  type.constructors.forEach { typeSpecBuilder.addMethod(it.methodSpecBuilder.build()) }
  type.methods.forEach { typeSpecBuilder.addMethod(it.methodSpecBuilder.build()) }
  return typeSpecBuilder.build()
}

fun main(args : Array<String>) {
  println(classType(setOf(PUBLIC), "TestDsl") {
    javaDoc = "This is a test class for the kotlin javapoet DSL\n"

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

    method(setOf(PUBLIC), BOOLEAN, "complexControlFlow", setOf(JavaPoetValue(setOf(FINAL), INT, "in"))) {
      javaDoc = "This method shows arbitrary complex control flow\n"
      controlFlow {
        begin("if(in > 0)") {
          controlFlow {
            begin("if(in < 5)") {
              statement("return true")
            }
            next("else if (in < 7)") {
              statement("return false")
            }
            next("else if (in < 10)") {
              statement("return true")
            }
            end()
          }
        }
        next("else") {
          statement("return false")
        }
        end()
      }
    }
  }.toString())
}

