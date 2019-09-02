package com.armdroid.playground

import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer

typealias Element = Pair<TypeName, String?>
object Generator {

    fun generate(filer: Filer, packageName: String, elements: Set<Element>) {
        val testFunctionBuilder = FunSpec.builder("checkDI")
            .receiver(ClassName("org.koin.core", "KoinApplication"))
            .beginControlFlow("koin.apply")

        elements.forEach { (type, named) ->
            named?.let {
                val namedMember = MemberName("org.koin.core.qualifier", "named")
                testFunctionBuilder.addStatement("get<%T>(%T::class, %M(%S), null)", type, type, namedMember, named)
            } ?: run {
                testFunctionBuilder.addStatement("get<%T>(%T::class, null, null)", type, type)
            }
        }

        val testFunction = testFunctionBuilder
            .endControlFlow()
            .build()

        val file = FileSpec.builder(packageName, "KoinProviderTest")
            .addFunction(testFunction)
            .build()
        file.writeTo(filer)
    }
}