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
    <script src="https://cdn.bootcss.com/jquery.form/4.2.2/jquery.form.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#testAjax").on('click', function () {
                $.ajax({
                    url: "http://localhost:1025/api/login/jwpt/evaluate/captcha",
                    type: "get",
                    dataType: "json",
                    // timeout: 4000,
                    cache: false,
                    beforeSend: LoadFunction,
                    success: successFunction,
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        alert(XMLHttpRequest.status);
                        alert(XMLHttpRequest.readyState);
                        alert(textStatus);
                    },
                    complete: completeFunction
                })

                function LoadFunction() {
                    $("#captchaDiv").html('加载中...');
                    $("#testAjax").attr({disabled: "disabled"});
                }

                function successFunction(data) {
                    console.log(data);
                    $("#captchaDiv").remove();
                    var status = data.status;
                    var jsonData = data.data;
                    if (status === 500) {
                        status500();
                    } else if (status === 404) {
                        status404();
                    } else if (status === 200) {
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
                    $("#captchaImg").attr("src", captchaImg);
                    $("#cookieForm").attr("value", JSON.stringify(cookie));
                }
            });

            $("#loginForm").on("submit", function () {
                $(this).ajaxSubmit({
                    type: 'post', // 提交方式 get/post
                    url: 'http://localhost:1025/api/login/jwpt/evaluate', // 需要提交的 url
                    beforeSend: LoadFunction,
                    success: successFunction,
                    complete: completeFunction,
                });

                function successFunction(data) {
                    console.log(data);
                    var JsonObj = JSON.parse(data);
                    console.log(JsonObj.result);
                    var res = JsonObj.result;
                    var numAll = JsonObj.numAll;
                    var numTot = JsonObj.numTot;
                    var numOK = JsonObj.numOK;
                    if(res == true) {
                        $("#msg").html("共查询到" + numAll + "门课程<br>" +
                            "共需评教" + numTot + "门课程<br>" +
                            "评教成功" + numOK +"门课程<br>" +
                            "请登录教务系统再次检查评教结果");
                    } else {
                        $("#msg").html("评教失败");
                    }
                }

                function completeFunction(data) {
                    $("#loginForm").resetForm();
                    $("#loginButton").removeAttr("disabled");
                }

                function LoadFunction() {
                    $("#msg").html('评教中，请稍等...');
                    $("#loginButton").attr({disabled: "disabled"});
                }

                return false; // 阻止表单自动提交事件
            });

        });


    </script>
</head>
<body>
<div>
    <form action="" method="post" id="loginForm">
        <input type="text" name="username" id="username">
        <input type="password" name="pwd" id="pwd">
        <input type="text" name="captchaCode" id="captchaCode">
        <input type="hidden" name="cookieForm" id="cookieForm">
        <input type="submit" id="loginButton">
    </form>
    <button id="testAjax" type="button">获取验证码</button>
    <div id="captchaDiv"></div>
    <img src="" id="captchaImg">
    <div id="cookie"></div>
    <div id="msg"></div>
</div>

</body>
</html>
