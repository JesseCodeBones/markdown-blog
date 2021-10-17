<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>Jesse Chen's Home Page</title>
    <link rel="stylesheet" href="/css/default.css">
    <script src="/js/jquery.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>

<#include "./header.ftl" >

<div class="tags">
    <#list tags as tag>
    <div>${tag!''}</div>
    </#list>
</div>

<div class="mds">
    <#if mds??>
        <#list mds as md>
            <div class="md-item">
                <div class="md-title" value="${md.id!''}">${md.title!''}</div>
                <div class="date">${md.date!''}</div>
                <div class="view-times">${md.viewTimes!''} view</div>
            </div>

        </#list>
    </#if>
</div>

<script>

    $(".md-title").on('click', function(e){
        let mdid = e.target.getAttribute("value");
        window.open("/md?target="+mdid)
    });

    $(".tags>div").on('click', function(e){
        window.location.href = "/?tag="+e.target.innerHTML
    });

</script>

</body>
</html>