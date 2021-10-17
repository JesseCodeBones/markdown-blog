package com.example.demo

import java.time.format.DateTimeFormatter

object BlogDefaultConfiguration {
    val tagsFile:String = "blog_tags.json"
    val parttern:DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/y H:m:ss")
    val mdFiles:String = "blog_mds.json"
}