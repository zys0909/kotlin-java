package com.example.main.io

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/30
 */
object Signature {

    const val keyStroke = "C:/Users/allen/.android/debug.keystore"

    @Throws
    fun signature(unSignatureFile: File, signatureFile: File) {
        val cmd: Array<String> = arrayOf("cmd.exe", "/C",
                "jarsigner", "-sigalg", "MD5withRSA",
                "-digestalg", "SHA1",
                "-keystore", keyStroke,
                "-storepass", "android",
                "-keypass", "android",
                "-signedjar", signatureFile.absolutePath,
                unSignatureFile.absolutePath, "androiddebugkey")
        val exec = Runtime.getRuntime().exec(cmd)
        try {
            val waitFor = exec.waitFor()
            println(waitFor)
        }catch (e:InterruptedException){
            e.printStackTrace()
            throw e
        }
        if (exec.exitValue() != 0) {
            val inputStream = exec.inputStream
            val bos = ByteArrayOutputStream()
            var len: Int
            val buf = ByteArray(2048)
            while (inputStream.read(buf).let { len = it;it != -1 }) {
                bos.write(buf, 0, len)
            }
            println(bos.toByteArray().toString(Charset.forName("GBK")))
            throw RuntimeException("签名执行失败")
        }
        exec.destroy()
    }
}