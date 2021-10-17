package com.example.demo

import com.google.gson.Gson
import com.qcloud.cos.COSClient
import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class QCloudJsonFile(fileName: String, bucketName: String) : QCloudFile(fileName, bucketName) {

    private val gson:Gson = Gson()

    fun <T> fromJson(classOfT:Class<T>): T? {
        if (content == null) {
            return null
        }
        ByteArrayInputStream(content).also {
            IOUtils.readFully(it, content!!.size).also { bytes ->
                return gson.fromJson(String(bytes, Charset.forName("utf-8")), classOfT)
            }
        }
    }

    fun <T> pushWithJson(item:T, cosClient: COSClient):Boolean{
        content = gson.toJson(item).toByteArray(Charset.forName("utf-8"))
        return pushCloud(cosClient)
    }
}