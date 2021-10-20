# Simple markdown blog


## Prepare
* register an account to QCloud: https://cloud.tencent.com/
* create a bucket of COS (cloud object storage) of QCloud: https://console.cloud.tencent.com/cos5
* create an API access account: https://console.cloud.tencent.com/cam/overview
* init `blog_mds.json` and `blog_tags.json` at COS bucket
* in `blog_mds.json` input empty value with `[]`
* in `blog_tags.json` input the correct value such as `["life", "study"]`
* configure `src/resources/application.properties` with correct values

## Build and Run
* simply run `./gradlew bootRun -i`

## upload markdown document
* upload .md file with URL: "/addDoc"

## DEMO page

https://www.chenruixiang.top
