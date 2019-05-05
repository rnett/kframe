package com.rnett.kframe.structure.addons

import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.dumps
import kotlinx.serialization.loads
import kotlin.browser.localStorage
import kotlin.browser.sessionStorage
import kotlin.collections.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import org.w3c.dom.Storage as RawJsStorage

typealias JsStorage = RawJsStorage

class StorageException(message: String) : RuntimeException(message)

sealed class Storage(val underlying: JsStorage, val storageName: String) {

    private val serializers: MutableMap<String, KSerializer<*>> = mutableMapOf()

    fun setKeySerializerOnly(key: String, serializer: KSerializer<*>) {
        serializers[key] = serializer
    }

    private val defaultSerializers: MutableMap<KClass<*>, KSerializer<*>> = mutableMapOf()

    fun <R : Any> setDefaultSerializer(serializer: KSerializer<R>, klass: KClass<R>) {
        defaultSerializers[klass] = serializer
    }

    inline fun <reified R : Any> setDefaultSerializer(serializer: KSerializer<R>) =
        setDefaultSerializer(serializer, R::class)

    fun hasDefaultSerializer(klass: KClass<*>) = klass in defaultSerializers

    operator fun contains(key: String) = underlying.getItem(key) != null

    inline fun <reified R : Any> setSerializer(key: String, serializer: KSerializer<R>) {
        setKeySerializerOnly(key, serializer)
        val c = R::class
        if (!hasDefaultSerializer(c))
            setDefaultSerializer(serializer)
    }

    inline operator fun <reified R : Any> set(key: String, serializer: KSerializer<R>) = setSerializer(key, serializer)

    inline operator fun <reified R : Any> plusAssign(serializer: KSerializer<R>) = setDefaultSerializer(serializer)

    fun <R : Any> getSerializer(key: String, klass: KClass<R>) =
        serializers[key] as? KSerializer<R> ?: defaultSerializers[klass] as? KSerializer<R>

    inline fun <reified R : Any> getSerializer(key: String) = getSerializer(key, R::class)

    operator fun <R : Any> get(key: String, serializer: KSerializer<R>): R {
        setKeySerializerOnly(key, serializer)
        return underlying.getItem(key)?.let {
            Cbor.plain.loads(
                serializer,
                it
            )
        } ?: throw StorageException("Key $key is not present in $storageName")
    }


    inline operator fun <reified R : Any> get(key: String): R = get(
        key,
        getSerializer(key) ?: throw StorageException("No Serializer for $key for $storageName")
    )

    inline fun <reified R : Any> getOrNull(key: String): R? = try {
        get(key)
    } catch (se: StorageException) {
        null
    }

    fun <R : Any> getOrNull(key: String, serializer: KSerializer<R>): R? = try {
        get(key, serializer)
    } catch (se: StorageException) {
        null
    }

    operator fun <R : Any> set(key: String, value: R, serializer: KSerializer<R>) {
        setKeySerializerOnly(key, serializer)
        underlying.setItem(
            key, Cbor.plain.dumps(
                serializer,
                value
            )
        )
    }

    inline operator fun <reified R : Any> set(key: String, value: R) {
        set(key, value, getSerializer(key) ?: throw StorageException("No Serializer for $key for $storageName"))
    }

    fun remove(key: String) = underlying.removeItem(key)

    inline operator fun <reified R : Any> getValue(thisRef: Any?, property: KProperty<*>): R = this[property.name]
    inline operator fun <reified R : Any> setValue(thisRef: Any?, property: KProperty<*>, value: R) =
        set(property.name, value)

    inner class Delegate(val name: String? = null) {
        inline operator fun <reified R : Any> getValue(thisRef: Any?, property: KProperty<*>): R {
            return get(name ?: property.name)
        }

        inline operator fun <reified R : Any> setValue(thisRef: Any?, property: KProperty<*>, value: R) {
            set(name ?: property.name, value)
        }
    }

    inner class TypedDelegate<T : Any> internal constructor(val name: String? = null, val serializer: KSerializer<T>) :
        ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            get(name ?: property.name, serializer)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            set(name ?: property.name, value, serializer)
        }

    }

    inline fun <reified T : Any> by(name: String) = Delegate(name)
    fun <T : Any> by(serializer: KSerializer<T>) = TypedDelegate(null, serializer)
    fun <T : Any> by(name: String, serializer: KSerializer<T>) = TypedDelegate(name, serializer)
}

object LocalStorage : Storage(localStorage, "Local Storage")

object SessionStorage : Storage(sessionStorage, "Session Storage")
