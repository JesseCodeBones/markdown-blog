package com.example.demo

import com.google.gson.Gson
import com.qcloud.cos.COSClient
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ListObjectsRequest
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.transfer.TransferManager
import com.qcloud.cos.transfer.TransferManagerConfiguration
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class QCloudFile constructor (private val fileName:String, private val bucketName: String){

    var content:ByteArray? = null

    fun load(cosClient: COSClient){
        GetObjectRequest(bucketName, fileName).run {
            val cosObject = cosClient.getObject(this)
            content = cosObject.objectContent.readAllBytes()
        }
    }

    //init transfer manager

    fun pushCloud(cosClient: COSClient):Boolean {
        val threadPool:ExecutorService = Executors.newFixedThreadPool(32)
        var result: Boolean
        val transferManager = TransferManager(cosClient, threadPool)
        PutObjectRequest(bucketName, fileName, ByteArrayInputStream(content), null).also {
            val transferManagerConfiguration = TransferManagerConfiguration()
            transferManagerConfiguration.multipartUploadThreshold = (10 * 1024 * 1024).toLong()
            transferManager.configuration = transferManagerConfiguration
            transferManager.upload(it).also { upload ->
                result = upload?.waitForUploadResult()?.key!=null
            }
        }
        return result
    }

}