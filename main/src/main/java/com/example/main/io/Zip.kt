package com.example.main.io

import java.io.*
import java.util.zip.*

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/29
 */
object Zip {


    fun unZip(zip: File, dir: File) {
        try {
            dir.delete()
            val zipFile = ZipFile(zip)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val zipElement = entries.nextElement()
                val name = zipElement.name
                if (name.startsWith("META-INF/")) {
                    continue
                }
                if (!zipElement.isDirectory) {
                    val file = File(dir, name)
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdir()
                    }
                    val fos = FileOutputStream(file)
                    val inputStream = zipFile.getInputStream(zipElement)
                    val byteBuffer = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(byteBuffer).let { len = it; it != -1 }) {
                        fos.write(byteBuffer, 0, len)
                    }
                    inputStream.close()
                    fos.close()
                }
            }
            zipFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun zip(dir: File, zip: File) {
        zip.delete()
        //对输出文件做CRC32校验
        val cos = CheckedOutputStream(FileOutputStream(zip), CRC32())
        val zos = ZipOutputStream(cos)

        zos.flush()
        zos.close()
    }

    @Throws(IOException::class)
    private fun compress(srcFile: File, zos: ZipOutputStream, basePath: String) {
        if (srcFile.isDirectory) {
            compressDir(srcFile, zos, basePath)
        } else {
            compressFile(srcFile, zos, basePath)
        }
    }

    @Throws(IOException::class)
    private fun compressDir(dir: File, zos: ZipOutputStream, basePath: String) {
        val files = dir.listFiles()
        if (files.isNullOrEmpty()) {
            val zipEntry = ZipEntry(basePath + dir.name + "/")
            zos.putNextEntry(zipEntry)
            zos.closeEntry()
            return
        }
        files.forEach {
            compressFile(it, zos, basePath)
        }
    }

    @Throws(IOException::class)
    private fun compressFile(file: File, zos: ZipOutputStream, basePath: String) {
        val dirName = basePath + file.name
        val dirNameNew = dirName.split("/")
        val name = dirNameNew.filterIndexed { index, _ -> index != 0 }.joinToString("/")
        val zipEntry = ZipEntry(name)
        zos.putNextEntry(zipEntry)
        val bis = BufferedInputStream(FileInputStream(file))
        val buffer = ByteArray(1024)
        var len: Int
        while (bis.read(buffer, 0, 1024).let { len = it;it != -1 }) {
            zos.write(buffer, 0, len)
        }
        bis.close()
        zos.closeEntry()
    }
}