<%--
  Created by IntelliJ IDEA.
  User: yao
  Date: 2017/12/22
  Time: 10:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <meta charset="utf-8">
    <title>所有媒体报道</title>
    <link rel="stylesheet" href="<%=path %>/static/layui/css/layui.css" media="all"/>
</head>
<body style="padding-top: 20px">
<div class="searchType">
    <div class="layui-inline">
        <input class="layui-input" name="title" id="title" autocomplete="off" placeholder="搜索标题"/>
    </div>
    <div class="layui-inline">
        <input class="layui-input" name="url" id="url" autocomplete="off" placeholder="搜索url"/>
    </div>
    <div class="layui-inline">
        <input class="layui-input" name="createTime" id="createTime" autocomplete="off" placeholder="搜索时间"/>
    </div>
    <button class="layui-btn" data-type="reload">搜索</button>
</div>

<div class="layui-btn-group demoTable">
    <button class="layui-btn" data-type="getCheckData">获取选中行数据</button>
    <button class="layui-btn" data-type="getCheckLength">获取选中数目</button>
    <button class="layui-btn" data-type="isAll">验证是否全选</button>
    <button class="layui-btn" data-type="delete">删除文章</button>
</div>

<table id="allReport_table" lay-filter="demo"></table>

<script type="text/html" id="imgUtil">
    {{#  if(d.pic !== null && d.pic != ''){ }}
    <img src="<%=path %>/{{ d.pic }}" alt="d.pic" />
    {{#  } else { }}
    <span>没有封面图</span>
    {{#  } }}
</script>
<script type="text/html" id="urlUtil">
    {{#  if(d.url !== null && d.url != ''){ }}
    <a target="_blank" href="{{ d.url }}">{{ d.url }}</a>
    {{#  } else { }}
    <span>没有链接</span>
    {{#  } }}
</script>
<script type="text/javascript" src="<%=path %>/static/layui/layui.js"></script>
<script type="text/javascript" src="<%=path %>/static/js/home/public.js"></script>
<script>
    layui.use('table', function(){
        var table = layui.table;
        var $ = layui.$;

        table.render({
            elem: '#allReport_table'
            ,url: '<%=path %>/data/company/pagerCriteria'
            ,cols: [[
                {checkbox: true, fixed: true}
                ,{field:'mid', title:'ID', width:50, fixed: 'left'}
                ,{field:'title', title:'标题', width:150}
                ,{field:'summary', title:'摘要', width:200}
                ,{field:'pic', title:'封面', width:120, templet: '#imgUtil'}
                ,{field:'url', title:'链接', width:80, templet: '#urlUtil'}
                ,{field:'createdTime', title:'创建时间', width:180, sort: true, templet:'<div>{{ formatDate(d.createdTime)}}</div>'}
            ]]
            ,id: 'idTest'
            ,page: true
            ,height: 500
            ,response: {
                statusName: 'status'
                ,statusCode: 0
                ,msgName: 'message'
                ,countName: 'total'
                ,dataName: 'rows'
            }
        });

        //监听表格复选框选择
        table.on('checkbox(demo)', function(obj){
            console.log(obj)
        });
        //监听工具条
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'del'){
                layer.confirm('真的删除行么', function(index){
                    obj.del();
                    layer.close(index);
                });
            } else if(obj.event === 'edit'){
                layer.alert('编辑行：<br>'+ JSON.stringify(data))
            }
        });

        $('.demoTable .layui-btn').on('click', function(){
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        var active = {
            getCheckData: function(){ //获取选中数据
                var checkStatus = table.checkStatus('idTest')
                    ,data = checkStatus.data;
                layer.alert(JSON.stringify(data));
            }
            ,getCheckLength: function(){ //获取选中数目
                var checkStatus = table.checkStatus('idTest')
                    ,data = checkStatus.data;
                layer.msg('选中了：'+ data.length + ' 个');
            }
            ,isAll: function(){ //验证是否全选
                var checkStatus = table.checkStatus('idTest');
                layer.msg(checkStatus.isAll ? '全选': '未全选')
            }
            ,delete: function(){ //验证是否全选
                var checkStatus = table.checkStatus('idTest')
                    ,data = checkStatus.data;
                if(data.length == 1) {
                    layer.confirm('真的删除行么', function(index){
                        layer.msg('假装删除了');
                        layer.close(index);
                    });
                } else {
                    layer.msg('请选中一行！', {time:1500});
                }
            }
        };

        $('.searchType .layui-btn').on('click', function(){
            var type = $(this).data('type');
            search[type] ? search[type].call(this) : '';
        });

        var search = {
            reload: function(){
                var title = $('#title');
                var url = $('#url');
                var createTime =$('#createTime');
                //执行重载
                table.reload('idTest', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    ,where: {
                        title: title.val(),
                        url:url.val(),
                        createTime:createTime.val()
                    }
                });
            }
        };
    });
</script>
</body>
</html>
