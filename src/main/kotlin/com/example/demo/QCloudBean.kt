package com.example.demo

import com.google.gson.Gson
import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.auth.COSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.*
import com.qcloud.cos.region.Region
import com.qcloud.cos.transfer.TransferManager
import com.qcloud.cos.transfer.TransferManagerConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.Charset
import java.util.concurrent.Executors
import javax.annotation.PostConstruct


@Component
class QCloudBean {

    @Value("\${blog.http.proxy.host}")
    private val proxyHost: String? = null

    @Value("\${blog.http.proxy.port}")
    private val proxyPort: String? = null

    @Value("\${blog.qcloud.secretID}")
    private val secretID: String? = null

    @Value("\${blog.qcloud.secretKey}")
    private val secretKey: String? = null

    @Value("\${blog.qcloud.region}")
    private val region: String? = null

    @Value("\${blog.qcloud.bucket}")
    val targetBucketName: String? = null

    @Value("\${blog.qcloud.appid}")
    private val appid: String? = null


    var cosClient: COSClient? = null
    private var bucket: Bucket? = null
    private var transferManager:TransferManager?=null

    @PostConstruct
    fun postConstruct() {
        val cosCredentials: COSCredentials = BasicCOSCredentials(secretID, secretKey)
        val configRegion = Region(region)
        val clientConfig = ClientConfig(configRegion)
        clientConfig.apply {
            this.setHttpProtocol(HttpProtocol.https)
            if (proxyHost != null && proxyPort != null && proxyHost != "null") {
                this.httpProxyIp = proxyHost
                this.httpProxyPort = Integer.valueOf(proxyPort)
            }
        }
        cosClient = COSClient(cosCredentials, clientConfig)

        //init blog bucket
        cosClient?.listBuckets()?.forEach {
            if (it.name.equals(targetBucketName)) {
                bucket = it
            }
        }

        //init tags
        ListObjectsRequest().apply {
            bucketName = bucket?.name
            delimiter = "/"
            maxKeys = 1000
        }.run {
            val objectSummaries = cosClient?.listObjects(this)?.objectSummaries
            var foundObject:COSObjectSummary? = null
            objectSummaries?.forEach {
                if (it.key.equals(BlogDefaultConfiguration.tagsFile)) {
                    foundObject = it
                }
            }
            GetObjectRequest(bucketName, foundObject?.key).run {
                val cosObject = cosClient?.getObject(this)
                tags = Gson().fromJson(
                    cosObject?.objectContent?.reader(Charset.forName("UTF-8")),
                    List::class.java
                ) as List<String>?
            }
        }

        //init transfer manager
        val threadPool = Executors.newFixedThreadPool(32)
        transferManager = TransferManager(cosClient, threadPool)
    }

    fun legal(): Boolean = cosClient != null && bucket != null
    var tags: List<String>? = null

    fun upload(reader: InputStream):Boolean{
        var result: Boolean
        PutObjectRequest(bucket?.name, "test", reader, null).also {
            val transferManagerConfiguration = TransferManagerConfiguration()
            transferManagerConfiguration.multipartUploadThreshold = (10 * 1024 * 1024).toLong()
            transferManager?.setConfiguration(transferManagerConfiguration)
            transferManager?.upload(it).also {
               result = it?.waitForUploadResult()?.key!=null
            }
        }
        return result
    }

}
