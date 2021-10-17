package com.example.demo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.nio.charset.Charset

@SpringBootTest
class QCloudFileTest {

    @Autowired
    val cloudBean:QCloudBean?=null

    @Value("\${blog.qcloud.bucket}")
    private val targetBucketName: String? = null

    val testFileName:String = "test"

    @Test
    fun test_load(){
        if (targetBucketName != null) {
            QCloudFile(testFileName, targetBucketName).apply {
                cloudBean?.cosClient?.let { this.load(it) }
                this.content?.let {
                    val contentString = String(it, Charset.forName("UTF-8"))
                    Assertions.assertNotNull(contentString)
                }

            }

        }
    }

    @Test
    fun test_sync(){
        if (targetBucketName != null) {
            QCloudFile(testFileName, targetBucketName).apply {
                cloudBean?.cosClient?.let { this.load(it) }
                val updatedContent = "test"
                this.content = updatedContent.toByteArray(Charset.forName("utf-8"))
                cloudBean?.cosClient?.let {
                    Assertions.assertTrue(this.pushCloud(it))
                }
            }
        }
    }
}