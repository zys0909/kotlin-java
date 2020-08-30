package com.example.main.io

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/29
 */
object AES {
    const val DEFAULT_PWD = "abcdefghijklmnop"

    private const val algorithmStr = "AES/ECB/PKCS5Padding"

    private lateinit var encryptCipher: Cipher

    private lateinit var decryptCipher: Cipher

    fun init(password: String) {
        try {
            encryptCipher = Cipher.getInstance(algorithmStr)
            decryptCipher = Cipher.getInstance(algorithmStr)
            val keyStr = password.toByteArray()
            val key = SecretKeySpec(keyStr, "AES")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            decryptCipher.init(Cipher.DECRYPT_MODE, key)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
    }

    /**
     * 加密Dex文件
     */
    @Throws(IOException::class)
    fun encryptApkFile(srcApkFile: File, dstApkFile: File): File? {
        //解压apk
        Zip.unZip(srcApkFile, dstApkFile)
        //获得所有的dex文件，需处理分包情况
        val dexFile = dstApkFile.listFiles { _, name -> name?.endsWith(".dex") == true }
        var mainDexFile: File? = null
        dexFile?.forEach {
            //读数据
            val bytes = Utils.getBytes(it)
            //加密
            val encryptBytes = encrypt(bytes)
            if (it.name.endsWith("classes.dex")) {
                mainDexFile = it
            }
            //写数据  替换原来的数据
            encryptBytes?.run {
                val fos = FileOutputStream(it)
                fos.write(this)
                fos.flush()
                fos.close()
            }
        }
        return mainDexFile
    }

    /**
     * 加密字节数组
     */
    fun encrypt(bytes: ByteArray): ByteArray? {
        try {
            return encryptCipher.doFinal(bytes)
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 解密字节数组
     */
    fun decrypt(bytes: ByteArray): ByteArray? {
        try {
            return decryptCipher.doFinal(bytes)
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }
}