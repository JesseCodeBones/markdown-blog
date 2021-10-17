<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <script src="/js/jquery.js"></script>
    <script src="/js/marked.js"></script>
    <link rel="stylesheet" href="/css/marked.css">
</head>
<body>
    <div class="markdown-body"></div>
</body>
<script>
    $.get('/mdContent?fileName='+"${fileName!''}",function(data,status){
        console.log(status);
        $(".markdown-body").html(marked(data))
    })
</script>
</html>