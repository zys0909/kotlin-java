package com.example.main.io

import okhttp3.internal.and
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.zip.Adler32

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/30
 */
val ByteBuffer.bytes: ByteArray
    get() = {
        this.flip()
        val len = this.limit() - this.position()
        val bytes = ByteArray(len)
        if (!this.isReadOnly) {
            this.get(bytes)
        }
        bytes
    }.invoke()

object Utils {
    const val x = 241241143

    @JvmStatic
    fun main(args: Array<String>) {
        val byteBuf = ByteBuffer.allocate(4)
        byteBuf.putInt(x)
        println(byteBuf.bytes.contentToString())
        println(int2Bytes(x).contentToString())
    }


    fun int2Bytes(value: Int): ByteArray {
        val src = ByteArray(4)
        src[3] = value.shr(24).and(0xFF).toByte()
        src[2] = value.shr(16).and(0xFF).toByte()
        src[1] = value.shr(8).and(0xFF).toByte()
        src[0] = value.and(0xFF).toByte()
        return src
    }

    fun bytes2Int(src: ByteArray): Int {
        val zero = src[0].and(0xFF)
        val first = src[1].and(0xFF).shl(8)
        val second = src[2].and(0xFF).shl(16)
        val third = src[3].and(0xFF).shl(24)
        return zero.or(first).or(second).or(third).toInt()
    }


    /**
     * 更换dex文件 签名信息
     */
    @Throws(NoSuchAlgorithmException::class)
    fun changeSignature(newDex: ByteArray) {
        val md = MessageDigest.getInstance("SHA-1")
        //从32个字节开始计算SHA-1值
        md.update(newDex, 32, newDex.size - 32)
        val sha1 = md.digest()
        //从第12位开始,拷贝20字节内容
        //替换signature
        System.arraycopy(sha1, 0, newDex, 12, 20)
    }

    fun changeCheckSum(newDex: ByteArray) {
        val adler32 = Adler32()
        adler32.update(newDex, 12, newDex.size - 12)
        val checkSum = int2Bytes(adler32.value.toInt())
        System.arraycopy(checkSum, 0, newDex, 8, 4)
    }

    fun getBytes(dexFile: File): ByteArray {
        val fis = RandomAccessFile(dexFile, "r")
        val buffer = ByteArray(fis.length().toInt())
        fis.readFully(buffer)
        fis.close()
        return buffer
    }

    fun changeFileSize(mainDex: ByteArray, newDex: ByteArray, aarDex: ByteArray) {
        val bytes = int2Bytes(mainDex.size)
        //更改文件头长度信息
        val fileSize = int2Bytes(newDex.size)
        System.arraycopy(fileSize, 0, newDex, 32, 4)
    }
}