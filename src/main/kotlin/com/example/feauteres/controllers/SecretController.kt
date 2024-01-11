package com.example.feauteres.controllers

import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGHT = 256
private const val SECRET = "osmilijey2648"

class SecretController() {

    @OptIn(ExperimentalStdlibApi::class)
    fun generateHash(pswrd: String, slt: String): String {
        val combinedSlt =  "$slt$SECRET".toByteArray()

        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(pswrd.toCharArray(), combinedSlt, ITERATIONS, KEY_LENGHT)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded

        return hash.toHexString()
    }

}