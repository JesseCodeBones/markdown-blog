package com.example.demo

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.auth.COSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.Bucket
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ListObjectsRequest
import com.qcloud.cos.model.ObjectListing
import com.qcloud.cos.region.Region
import org.apache.commons.io.IOUtils
import org.bouncycastle.util.encoders.UTF8
import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset


@SpringBootTest
class DemoApplicationTests {

	@Autowired
	val cloudBean:QCloudBean?=null

	@Test
	fun test_get_bean(){
		println(cloudBean)
	}

	fun testUploadFile(){
		val file:File = File("src/test/resources/test.text")
		cloudBean?.upload(FileInputStream(file))?.let { Assertions.assertTrue(it) }
	}

}
