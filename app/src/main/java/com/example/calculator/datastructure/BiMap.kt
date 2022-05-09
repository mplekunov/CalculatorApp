package com.example.calculator.datastructure

class BiMap<K : Any, V: Any> : MutableMap<K, V> {
    private var direct: MutableMap<K, V> = mutableMapOf()
    private var inverse: MutableMap<V, K> = mutableMapOf()

    override val size: Int
        get() = direct.size

    // Ideally, I need to implement a BiSet
    // For my purposes... I don't need to use "entries" yet, so I'm gonna have a dirty implementation
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = direct.entries

    override val keys: MutableSet<K>
        get() = direct.keys

    override val values: MutableCollection<V>
        get() = direct.values


    val entriesInverse: MutableSet<MutableMap.MutableEntry<V, K>>
        get() = inverse.entries

    val keysInverse: MutableSet<V>
        get() = inverse.keys

    val valuesInverse: MutableCollection<K>
        get() = inverse.values

    override fun containsKey(key: K): Boolean = direct.containsKey(key)
    @JvmName("containsKeyInverse")
    fun containsKey(key: V): Boolean = inverse.containsKey(key)

    override fun containsValue(value: V): Boolean = direct.containsValue(value)
    @JvmName("containsValueInverse")
    fun containsValue(value: K): Boolean = inverse.containsValue(value)

    override fun get(key: K): V? = direct[key]
    @JvmName("getInverse")
    operator fun get(key: V): K? = inverse[key]


    override fun isEmpty(): Boolean = direct.isEmpty()

    override fun clear() {
        direct.clear()
        inverse.clear()
    }

    override fun put(key: K, value: V): V? {
        direct[key] = value
        inverse[value] = key

        return value
    }
    @JvmName("putInverse")
    fun put(key: V, value: K): K? {
        inverse[key] = value
        direct[value] = key

        return value
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) ->
            run {
                put(key, value)
                put(value, key)
            }
        }
    }
    @JvmName("putAllInverse")
    fun putAll(from: Map<out V, K>) {
        from.forEach { (key, value) ->
            run {
                put(key, value)
                put(value, key)
            }
        }
    }

    override fun remove(key: K): V? {
        val value = direct.remove(key)
        inverse.remove(value)

        return value
    }
    @JvmName("removeInverse")
    fun remove(key: V): K? {
        val value = inverse.remove(key)
        direct.remove(value)

        return value
    }


}