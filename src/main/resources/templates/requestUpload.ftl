<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>upload doc</title>
</head>
<body>
<form action="/confirmUpload" method="post" enctype="multipart/form-data"  >

    title: <input type="text" name="title">
    tag: <input type="text" name="tags">
    file: <input type="file" name="mdFile">
    pingCode: <input type="password" name="pingCode">

    <input type="submit" value="submit">
</form>
</body>
</html>