<%--
  Created by IntelliJ IDEA.
  User: ningxy
  Date: 2018/6/20
  Time: 上午11:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>login</title>
</head>
<body>
<div>
    <form action="/api/login/jwpt/evaluate" method="post">
        <input type="text" name="username" id="username">
        <input type="password" name="pwd" id="pwd">
        <input type="text" name="captcha" id="captcha">
        <input type="submit">
    </form>
</div>
</body>
</html>
