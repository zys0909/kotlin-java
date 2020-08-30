package com.example.main.io

import java.io.File
import java.io.FileOutputStream

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/29
 */
object IOMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val tempApkFile = File("source/apk/temp")
        if (tempApkFile.exists()) {
            tempApkFile.listFiles()?.forEach {
                if (it.isFile) {
                    it.delete()
                }
            }
        }
        val tempAarFile = File("source/aar/temp")
        if (tempAarFile.exists()) {
            tempAarFile.listFiles()?.forEach {
                if (it.isFile) {
                    it.delete()
                }
            }
        }
        /**
         * 第一步处理原始apk  AES加密
         */
        AES.init(AES.DEFAULT_PWD)
        val apkFile = File("source/apk/app-debug.apk")
        val newApkFile = File(apkFile.parent + File.separator + "temp")
        if (!newApkFile.exists()) {
            newApkFile.mkdirs()
        }
        val mainDexFile = AES.encryptApkFile(apkFile, newApkFile)
        if (newApkFile.isDirectory) {
            newApkFile.listFiles()?.filter { it.isFile && it.name.endsWith(".dex") }?.forEach {
                val name = it.name
                val indexOfDex = name.indexOf(".dex")
                val newName = it.parent + File.separator + name.substring(0, indexOfDex) + "_.dex"
                it.renameTo(File(newName))
            }
        }
        /**
         * 第二部，处理aar，获得壳
         */
        val aarFile = File("source/aar/mylibrary-debug.aar")
        val aarDex = Dx.jar2Dex(aarFile)

        val tempMainFile = File(newApkFile.path + File.separator + "classes.dex")
        if (!tempMainFile.exists()) {
            tempMainFile.createNewFile()
        }
        val fos = FileOutputStream(tempMainFile)
        val bytes = Utils.getBytes(aarDex)
        fos.write(bytes)
        fos.flush()
        fos.close()

        /**
         * 第三步  打包签名
         */
        val unSignatureFile = File("result/apk-unsigned.apk")
        unSignatureFile.parentFile.mkdirs()
        Zip.unZip(newApkFile,unSignatureFile)
        val signatureFile = File("result/apk-signed.apk")
        Signature.signature(unSignatureFile,signatureFile)
    }
}