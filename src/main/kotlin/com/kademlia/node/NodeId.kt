package com.kademlia.node

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import java.math.BigInteger
import java.util.*
import kotlin.experimental.xor


data class NodeId(var key: ByteArray) {
    @Throws(IllegalArgumentException::class)
    constructor(key: String) : this(key.toByteArray())

    init {
        when {
            key.isEmpty()              -> {
                key = ByteArray(KEY_SIZE_BYTES)
                Random().nextBytes(key)
            }
            key.size != KEY_SIZE_BYTES -> {
                val hash = getHash(key)
                key = hash.asBytes()
            }
            else                       -> {

            }
        }

        if (key.size != KEY_SIZE_BYTES) { // should never reach
            throw IllegalArgumentException("Key must have $KEY_SIZE_BYTES bytes or ${KEY_SIZE_BYTES * 8} bits")
        }
    }

    private fun getHash(key: ByteArray): HashCode {
        val hf = Hashing.sha256()
        return hf.newHasher()
                .putBytes(key)
                .hash()
    }

    fun toInt() = BigInteger(1, key)
    /**
     * Checks the distance between this and another NodeId
     * @param nodeId
     * @return The distance of this NodeId from the given NodeId
     */
    fun xor(nodeId: NodeId): NodeId {
        val result = ByteArray(KEY_SIZE_BYTES)
        val nidBytes = nodeId.key
        for (i in 0 until KEY_SIZE_BYTES)
            result[i] = key[i] xor nidBytes[i]
        return NodeId(result)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodeId

        if (!Arrays.equals(key, other.key)) return false

        return true
    }

    override fun hashCode() = Arrays.hashCode(key)

    companion object {
        const val KEY_SIZE_BYTES = 32
        const val KEY_SIZE_BITS = KEY_SIZE_BYTES * 8
    }
}