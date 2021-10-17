package com.example.demo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.math.cos

@SpringBootTest
class QCloudJsonFileTest {

    @Autowired
    val cloudBean:QCloudBean?=null

    @Value("\${blog.qcloud.bucket}")
    private val targetBucketName: String? = null

    val testFileName:String = "blog_tags.json"

    @Test
    fun test_load(){
        if (targetBucketName != null) {
            QCloudJsonFile(testFileName, targetBucketName).also {
                cloudBean?.cosClient?.let { cosClient ->
                    it.load(cosClient)
                    it.fromJson(List::class.java)?.forEach { item ->
                        println(item)
                    }
                    Assertions.assertNotNull(it.fromJson(List::class.java))
                }
            }
        }
    }

    @Test
    fun testPush(){
        if (targetBucketName != null) {
            QCloudJsonFile("test", targetBucketName).also {
                cloudBean?.cosClient?.let { cosClient ->
                    it.load(cosClient)
                    val list = ArrayList<Map<String, String>>()
                    Assertions.assertTrue(it.pushWithJson(list, cosClient))
                }
            }
        }
    }

    fun init_test(){
        if (targetBucketName != null) {
            QCloudJsonFile("blog_mds.json", targetBucketName).also {
                cloudBean?.cosClient?.let { cosClient ->
                    val list = ArrayList<Map<String, Any>>()
                    Assertions.assertTrue(it.pushWithJson(list, cosClient))
                }
            }
        }
    }
}