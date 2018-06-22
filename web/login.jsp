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
    <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#testAjax").on('click',function(){
                $.ajax({
                    url:"http://localhost:1025/api/login/jwpt/evaluate/captcha",
                    type:"get",
                    dataType:"json",
                    // timeout: 4000,
                    cache: false,
                    beforeSend: LoadFunction,
                    success: SuccessFunction,
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        alert(XMLHttpRequest.status);
                        alert(XMLHttpRequest.readyState);
                        alert(textStatus);
                    },
                    complete: completeFunction
                })
                function LoadFunction() {
                    $("#captchaDiv").html('加载中...');
                    $("#testAjax").attr({ disabled: "disabled" });
                }
                function SuccessFunction(data) {
                    console.log(data);
                    console.log(data.status);
                    console.log(data.message);
                    console.log(data.data);
                    $("#captchaDiv").remove();
                    var status = data.status;
                    var jsonData = data.data;
                    if(status === 500) {
                        status500();
                    } else if (status === 404) {
                        status404();
                    } else if (status === 200){
                        status200(jsonData.captchaImg, jsonData.cookie);
                    } else {
                        statusUnknow()
                    }
                }
                function completeFunction(data) {
                    $("#testAjax").removeAttr("disabled");
                }
                function statusUnknow() {
                    $("#msg").html("未知问题");
                }
                function status500() {
                    $("#msg").html("服务器未成功获取验证码，请重试");
                }
                function status404() {
                    $("#msg").html("404");
                }
                function status200(captchaImg, cookie) {
                    console.log(JSON.stringify(cookie));
                    $("#captchaImg").attr("src",captchaImg);
                    $("#cookieForm").attr("value",JSON.stringify(cookie));
                }
            });
        });

        $("#loginButton").on('click',function(){
            $.ajax({
                url:"http://localhost:1025/api/login/jwpt/evaluate/captcha",
                type:"POST",
                data:"json",
                contentType:"application/json",  //缺失会出现URL编码，无法转成json对象
                beforeSend: LoadFunction,
                success:function(){
                    alert("成功");
                },
                complete: completeFunction
            });

            function LoadFunction() {
                $("#msg").html('加载中...');
                $("#loginButton").attr({ disabled: "disabled" });
            }

            function completeFunction(data) {
            }
        });
    </script>
</head>
<body>
<div>
    <form action="/api/login/jwpt/evaluate" method="post" id="loginForm">
        <input type="text" name="username" id="username">
        <input type="password" name="pwd" id="pwd">
        <input type="text" name="captchaCode" id="captchaCode">
        <input type="submit" id="loginButton">
        <input type="hidden" name="cookieForm" id="cookieForm">
    </form>
    <button id="testAjax" type="button">获取验证码</button>
    <div id="captchaDiv"></div>
    <img src="" id="captchaImg">
    <div id="cookie"></div>
    <div id="msg"></div>
</div>

</body>
</html>
