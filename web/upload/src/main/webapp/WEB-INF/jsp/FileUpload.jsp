<%@page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>File Upload</title>
</head>
<body>
<h1>Hello, it's jsp</h1>
<form action="upload" enctype="multipart/form-data" method="post">
    <p>
        <label>Select a file: </label>
        <input type="file" name="file"/>
    </p>
    <input type="submit" value="Upload">
</form>
</body>
</html>