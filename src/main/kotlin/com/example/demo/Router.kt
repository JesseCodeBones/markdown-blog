package com.example.demo

import com.example.demo.BlogDefaultConfiguration.mdFiles
import com.qcloud.cos.model.Bucket
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.measureTime

@Controller
class Router {

    @Autowired
    val qCloudBean:QCloudBean? = null


    @Value("\${blog.pingcode}")
    private val configuredPingCode: String? = null

    var qCloudJsonFile = qCloudBean?.targetBucketName?.let { QCloudJsonFile(mdFiles, it) }

    @RequestMapping("/", produces  = arrayOf("text/html;charset=UTF-8"))
    fun indexRouter(@RequestParam params:Map<String,Object>, modelMap: ModelMap):String {
        if (qCloudJsonFile == null) {
            qCloudJsonFile = qCloudBean?.targetBucketName?.let { QCloudJsonFile(mdFiles, it) }
        }
        modelMap.addAttribute("tags", qCloudBean?.tags)
        qCloudBean?.cosClient?.let { qCloudJsonFile?.load(it) }
        var mds = qCloudJsonFile?.fromJson(List::class.java)
        mds?.forEach { println(it) }
        val lengthComparator = Comparator { o1: Any, o2: Any ->
            val m1 = o1 as Map<String, Any>
            val m2 = o2 as Map<String, Any>
            - LocalDateTime.parse(m1["date"] as String?, BlogDefaultConfiguration.parttern).compareTo(
                    LocalDateTime.parse(m2["date"] as String?, BlogDefaultConfiguration.parttern)
            )
        }
        if (params["tag"] != null) {
            mds = mds?.filter {
                val m = it as Map<String, Any>
                m.get("tags") == params["tag"]
            }
        }
        mds = mds?.sortedWith(lengthComparator)
        modelMap.addAttribute("mds", mds)
        return "index"
    }
    @Value("\${blog.pingcode}")
    val pingcodeConfig:String?=null

    @RequestMapping("/addDoc")
    fun addDoc(modelMap: ModelMap)="requestUpload"

    @RequestMapping("/confirmUpload", method = arrayOf(RequestMethod.POST))
    fun confirmUpload(@RequestParam("mdFile") file: MultipartFile, @RequestParam params:Map<String,Object>):String{

        if (qCloudJsonFile == null) {
            qCloudJsonFile = qCloudBean?.targetBucketName?.let {
                QCloudJsonFile(mdFiles, it).also { file->
                    qCloudBean?.cosClient?.let { cosClient ->
                        file.load(cosClient)}
                }
            }
        } else {
            qCloudBean?.cosClient?.let { qCloudJsonFile?.load(it)}
        }
        val content = qCloudJsonFile?.fromJson(List::class.java)
        val uuid = UUID.randomUUID()
        val fileName = "${uuid}-${file.originalFilename}"

        qCloudBean?.targetBucketName?.let { QCloudFile(fileName, it) }.also { qCloudFile ->
            qCloudFile?.content = file.bytes
            qCloudBean?.cosClient?.let { qCloudFile?.pushCloud(it) }
        }

        if (params["pingCode"] != null && configuredPingCode?.equals(params["pingCode"]) == true && file != null) {
            val map = HashMap<String, Any?>()
            map["fileName"] = fileName
            map["id"] = content?.size
            map["tags"] = params["tags"]
            map["date"] = LocalDateTime.now().format(BlogDefaultConfiguration.parttern)
            map["title"] = params["title"]
            map["viewTimes"] = 0
            val mutableList = content?.toMutableList()
            mutableList?.add(map)
            qCloudBean?.cosClient?.let { qCloudJsonFile?.pushWithJson(mutableList, it) }
            return "uploadOK"
        } else {
            return "requestUpload"
        }
    }

    @RequestMapping("/md")
    fun showMD(@RequestParam("target") index:Int, modelMap: ModelMap):String{
        qCloudJsonFile = qCloudBean?.targetBucketName?.let { QCloudJsonFile(mdFiles, it) }
        qCloudBean?.cosClient?.let { qCloudJsonFile?.load(it) }
        qCloudJsonFile?.fromJson(List::class.java).also { contentList->
            contentList?.filter {
                val item =   it as Map<String, Any?>
                (item["id"] as Double).toInt() == index
            }.also {
                val mutableList = contentList?.toMutableList()
                val item = (it?.get(0) as Map<String, Any?>).toMutableMap()
                mutableList?.remove(it.get(0))
                item["viewTimes"] = ((item["viewTimes"] as Number).toInt()).plus(1)
                mutableList?.add(item)
                qCloudBean?.cosClient?.let { client -> qCloudJsonFile?.pushWithJson(mutableList, client) }
                modelMap.addAttribute("fileName", item.get("fileName"))
            }
        }
        return "md"
    }

    @RequestMapping("/mdContent")
    @ResponseBody
    fun mdContent(@RequestParam("fileName") fileName:String):String{
        qCloudBean?.targetBucketName?.let { QCloudFile(fileName, it) }.also { qCloudFile ->
            qCloudBean?.cosClient?.let { it1 -> qCloudFile?.load(it1) }.also {
                qCloudFile?.content.also { content ->
                    if (content != null) {
                        return String(content, Charset.forName("utf-8"))
                    } else {
                        return ""
                    }

                }
            }
        }
    }

    @RequestMapping("test") fun testPage(modelMap: ModelMap) :String {

        modelMap.addAttribute("welcome", "瑞祥")
        return "test"
    }


}