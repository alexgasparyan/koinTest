package com.armdroid.playground

import com.armdroid.annotations.KoinCheckDI
import com.armdroid.annotations.KoinNamed
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class Processor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KoinCheckDI::class.java.canonicalName)
    }

    override fun process(
        annotationSet: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        val kClassType =
            processingEnv.elementUtils.getTypeElement(KClass::class.java.name).asType().erasure()
        val stringClassType =
            processingEnv.elementUtils.getTypeElement(String::class.java.name).asType()
        roundEnvironment.getElementsAnnotatedWith(KoinCheckDI::class.java).map { property ->
            property as ExecutableElement
            if (!property.hasModifier(Modifier.ABSTRACT)) {
                property.print("Property must be abstract")
                return false
            }

            val klass = property.enclosingElement

            if (klass !is TypeElement
                || !klass.hasModifier(Modifier.ABSTRACT)
                || klass.typeParameters.isEmpty()
            ) {
                klass.print("Enclosing class of abstract property must be an abstract generic class")
                return false
            }

            val targetGenericIndex =
                klass.generics.indexOfFirst { it isSame property.returnType.firstTypeArgument() }

            val koinNamedField = ElementFilter.fieldsIn(klass.enclosedElements)
                .filter { it.getAnnotation(KoinNamed::class.java) != null }
                .firstOrNull()?.also {
                    if (it.asType() isNotSame stringClassType) {
                        it.print("Field annotated with @KoinNamed must return string")
                        return false
                    }
                }?.simpleName?.toString()

            if (property.returnType.erasure() isNotSame kClassType || targetGenericIndex == -1) {
                //field must return KClass<T>
                property.print("Field must return KClass<T>")
                return false
            }

            val eligibleClasses = roundEnvironment.rootElements
                .filter { it is TypeElement && it.superclass.erasure() isSame klass.asType().erasure() }

            val eligibleMethods =
                ElementFilter.methodsIn(eligibleClasses.map { it.enclosedElements }.flatten())

            val testingTypes = eligibleMethods
                .filter {
                    it.simpleName == property.simpleName
                            && !it.hasModifier(Modifier.ABSTRACT)
                            && it.returnType.erasure() isSame kClassType
                            && it.enclosingElement.superclassGenerics.indexOfFirst { gen -> gen isSame it.returnType.firstTypeArgument() } == targetGenericIndex
                }
                .map { propertyGetter ->
                    koinNamedField?.let {
                        val namedValue =
                            ElementFilter.fieldsIn(propertyGetter.enclosingElement.enclosedElements)
                                .filter { it.simpleName.contentEquals(koinNamedField) && it.asType() isSame stringClassType }
                                .map { it.constantValue as String? }
                                .firstOrNull()
                        propertyGetter.returnType.firstTypeArgument().asTypeName() to namedValue
                    } ?: run {
                        propertyGetter.returnType.firstTypeArgument().asTypeName() to null
                    }
                }
                .toSet()
            Generator.generate(processingEnv.filer, klass.enclosingElement.toString(), testingTypes)
        }
        return true
    }

    private fun TypeMirror.firstTypeArgument() = (this as DeclaredType).typeArguments[0]

    private fun TypeMirror.erasure() = processingEnv.typeUtils.erasure(this)

    private fun Element.hasModifier(modifier: Modifier) = modifiers.any { it == modifier }

    private val Element.generics
        get() = (asType() as DeclaredType).typeArguments

    private val Element.superclassGenerics
        get() = ((this as TypeElement).superclass as DeclaredType).typeArguments

    private infix fun TypeMirror.isSame(typeMirror: TypeMirror) =
        processingEnv.typeUtils.isSameType(this, typeMirror)

    private infix fun TypeMirror.isNotSame(typeMirror: TypeMirror) = !isSame(typeMirror)

    private fun Element.print(message: String) =
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, this)

    private fun print(message: String) =
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, message)

}