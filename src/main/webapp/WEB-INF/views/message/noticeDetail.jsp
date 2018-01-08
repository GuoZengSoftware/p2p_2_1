<%--
  Created by IntelliJ IDEA.
  User: ccf
  Date: 2017/12/28
  Time: 15:29
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 4.01 Transitional//EN">
<html>
<head>
    <meta charset="utf-8">
    <title>公告详情</title>
    <link rel="stylesheet" href="<%=path %>/static/layui/css/layui.css" media="all"/>
</head>
<body>
<div class="layui-container">
    <div class="layui-row">
        <div class="layui-col-md12" id="view">
            <form id="noticeDetail" class="layui-form" action="">

                <div class="layui-form-item" style="margin-top: 20px;">
                    <label class="layui-form-label"></label>
                    <div class="layui-input-block">
                        <input type="hidden" name="nid" id="nid" lay-verify autocomplete="off"
                               class="layui-input" readonly/>
                    </div>
                </div>

                <div class="layui-form-item" style="margin-top: 20px;">
                    <label class="layui-form-label">标题</label>
                    <div class="layui-input-block">
                        <input type="text" name="title" id="title" lay-verify="title" autocomplete="off"
                               class="layui-input"  style="border:none" readonly/>
                    </div>
                </div>
                <div class="layui-form-item" style="margin-top: 20px;">
                    <label class="layui-form-label">内容</label>
                    <div class="layui-input-block">
                        <textarea class="layui-textarea" name="content" id="content" lay-verify="content" style="border:none" readonly></textarea>
                    </div>
                </div>

                <div class="layui-form-item" style="margin-top: 20px;">
                    <label class="layui-form-label">创建时间</label>
                    <div class="layui-input-block">
                        <input type="text" name="createdTime" id="createdTime" lay-verify="createdTime" autocomplete="off"
                               class="layui-input" style="border:none" readonly/>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="<%=path %>/static/layui/layui.js"></script>
<script src="<%=path %>/static/js/home/public.js"></script>
<script>
    //获取url上的值,获取页面传过来的值
    function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    }
    var noticeId = GetQueryString("noticeId");
    layui.use(['form', 'laytpl', 'layedit'], function () {

        var form = layui.form;
        var $ = layui.jquery;
        var layer = layui.layer;
        var laytpl = layui.laytpl;
        var layedit = layui.layedit;

        $.get('<%=path %>/data/message/noticeDetail?noticeId=' + noticeId,
            function (data) {
                $('#nid').val(data.nid);
                $('#title').val(data.title);
                $('#content').val(data.content);
                if(data.createdTime) {
                    $('#createdTime').val(formatDate(data.createdTime));
                }
            });
        form.verify({
            title: function(value){
                if(value.length < 1){
                    return '需要填写标题';
                }
            },content: function(value){
                if(value.length < 1){
                    return '需要填写内容';
                }
            }
        });

    });
</script>

</body>
</html>

