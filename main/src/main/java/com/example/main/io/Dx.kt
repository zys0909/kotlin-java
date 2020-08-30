package com.example.main.io

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/30
 */
object Dx {

    @Throws(IOException::class, InterruptedException::class)
    fun jar2Dex(aarFile: File): File {
        val fakeDex = File(aarFile.parent + File.separator + "temp")
        //将aar解压到fakeDex目录下
        Zip.unZip(aarFile, fakeDex)
        val files = fakeDex.listFiles { _, name -> name.equals("classes.jar", true) }
        if (files.isNullOrEmpty()) {
            throw RuntimeException("the aar is invalidate")
        }
        val classesJar = files[0]
        val aarDex = File(classesJar.parentFile, "classes.dex")
        //我们要将jar 转变成为dex 需要使用android tools 里面的dx.bat
        //使用java 调用windows 下的命令
        dxCommand(aarDex, classesJar)
        return aarDex
    }

    private fun dxCommand(aarFile: File, classesJar: File) {
        val exec = Runtime.getRuntime().exec("cmd.exe /C dx --dex --output=${aarFile.absolutePath} ${classesJar.absolutePath}")
        try {
            exec.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
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
            throw RuntimeException("dx run failed")
        }
        exec.destroy()
    }
}
