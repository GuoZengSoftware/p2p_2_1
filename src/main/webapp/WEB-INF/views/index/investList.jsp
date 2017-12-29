<%--
  Created by IntelliJ IDEA.
  User: 7025
  Date: 2017/12/21
  Time: 16:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>投资列表</title>
    <link rel="stylesheet" href="<%=path%>/static/css/front/public.css">
    <link rel="stylesheet" href="<%=path%>/static/layui/css/layui.css">
    <link rel="stylesheet" href="<%=path%>/static/css/front/index.css">
    <link rel="icon" href="<%=path%>/static/images/logo_title.jpg" type="image/x-icon">
</head>
<body>
<%@include file="../master/top.jsp" %>
<%@include file="../master/header.jsp" %>
<div class="invest-list">
    <div class="wrap">
        <div class="invest-top">
            <div class="invest-top-left">
                <div class="invest-top-list">
                    <p>项目期限：</p>
                    <ul id="term">
                        <li class="active"><a href="javascript:void(0);" onclick="searchBorrow('term', null, null, null, null);">全部</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('term', 3, null, null, null);">3个月</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('term', 6, null, null, null);">6个月</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('term', 12, null, null, null);">12个月</a></li>
                    </ul>
                </div>
                <div class="invest-top-list">
                    <p>年化收益：</p>
                    <ul id="nprofit">
                        <li class="active"><a href="javascript:void(0);" onclick="searchBorrow('nprofit', null, null, null, null);">全部</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('nprofit', null, 0, 10, null);"><=10%</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('nprofit', null, 10, 15, null);">10%-15%</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('nprofit', null, 15, 25, null);">15%-25%</a></li>
                    </ul>
                </div>
                <div class="invest-top-list">
                    <p>项目类型：</p>
                    <ul id="bz">
                        <li class="active"><a href="javascript:void(0);" onclick="searchBorrow('bz', null, null, null, null);">全部</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('bz', null, null, null, 3);">多金宝</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('bz', null, null, null, 2);">普金保</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('bz', null, null, null, 1);">恒金保</a></li>
                        <li><a href="javascript:void(0);" onclick="searchBorrow('bz', null, null, null, 4);">新手标</a></li>
                    </ul>
                </div>
            </div>
            <div class="invest-top-right">
                <div class="invest-top-list">
                    <div class="textmiddle">借款标题</div>
                    <input type="text" name="cpname" class="text" id="cpname"/>
                    <a href="javascript:void(0);" class="layui-btn layui-btn-normal" onclick="searchBorrow(null, null, null, null, null);">搜索</a>
                </div>
            </div>
        </div>
        <div class="invest-list-bottom">
            <ul class="invest-row listData creditor-row" id="content">
                <script type="text/html" id="borrowList">
                    {{#  layui.each(d, function(index, borrow){ }}
                    <li>
                        <div class="invest-title cl"><p>{{ borrow.bzname }}</p>
                            <h3><a href="javascript:void(0);" onclick="borrowDetail('{{borrow.baid}}');">{{borrow.cpname}}</a></h3>
                        </div>
                        <div class="invest-content cl">
                            <ul>
                                <li class="row1"><p class="row-top">预期年化收益率</p><p class="row-bottom color">{{borrow.nprofit}}<span>%</span></p></li>
                                <li class="row2"><p class="row-top">项目期限</p><p class="row-bottom">{{borrow.term}}个月</p></li>
                                <li class="row3"><p class="row-top">还款方式</p><p class="row-bottom">按月付息，到期还本</p></li>
                                <li class="row4"><p class="row-top">可投金额 / 募集总额</p><p class="row-bottom">{{borrow.money-borrow.moneyCount}}万元 / {{borrow.money}}万元</p></li>
                                <li class="row5">
                                    <div class="line">
                                        <div class="layui-progress" style="float: left;width: 150px;margin-top: 13px" lay-showPercent="true">
                                            <div class="layui-progress-bar layui-bg-red" lay-percent="{{borrow.moneyCount/borrow.money*100}}%"></div>
                                        </div>
                                    </div>
                                    <p class="row-top">募集进度</p></li>
                                <li class="row6">
                                    {{# if((borrow.money-borrow.moneyCount)>0){ }}
                                    <button type="button" class="btn" onclick="borrowDetail('{{borrow.baid}}');">立即投标</button>
                                    {{# } else if(borrow.ckstatus === 5) { }}
                                    <button type="button" class="btn disabled" onclick="">已完成</button>
                                    {{# } else { }}
                                    <button type="button" class="btn disabled" onclick="">还款中</button>
                                    {{# } }}
                                </li>
                            </ul>
                        </div>
                    </li>
                    {{#  }); }}
                </script>
            </ul>
            <div id="borrowDemo"></div>
        </div>
    </div>
</div>
<%@include file="../master/footer.jsp" %>
</body>
<script type="text/javascript" src="<%=path %>/static/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path %>/static/layui/layui.js"></script>
<script type="text/javascript" src="<%=path %>/static/js/front/public.js"></script>
<script>
    $(function(){
        search(null, null, null, null, null);
    });

    function borrowDetail(baid) {
        window.location.href='<%=path %>/page/borrowApply/detail/'+ baid;
    }

    function searchBorrow(demoId, term, min, max, bzid) {
        if(demoId !== '') {
            $('#'+ demoId +' li').removeClass('active');
            $('a').click(
                function(){
                    $(this).parent().attr('class','active');
                });
        }
        var cpname = $('#cpname').val();
        search(term, min, max, cpname, bzid);
    }

    function search(term, min, max, cpname, bzid) {
        layui.use(['element', 'laypage', 'laytpl'], function () {
            var $ = layui.$;
            var element = layui.element;
            var laypage = layui.laypage;
            var laytpl = layui.laytpl;

            var page = 1; // 第一页开始
            var limit = 10; // 每页十个数据，laypage默认也是十个
            var getTpl = $('#borrowList').html()
                , view = document.getElementById('content');
            // 获取数据
            $.get('<%=path %>/data/borrow/frontList', {
                page: page
                , limit: limit
                , term : term
                , nprofitMax:max
                ,nprofitMin:min
                ,cpname: cpname
                ,bzid: bzid
            }, function (data) {
                fenye(data.rows);
                pageTotal(data.total)
            });

            // 渲染数据
            function fenye(data) {
                laytpl(getTpl).render(data, function (html) {
                    view.innerHTML = html;
                });
            }

            // 分页组件
            function pageTotal(total) {
                laypage.render({
                    elem: 'borrowDemo'
                    , count: total
                    , curr: location.hash.replace('#!page=', '') //获取起始页
                    , hash: 'page' //自定义hash值
                    , jump: function (obj, first) {
                        //obj包含了当前分页的所有参数，比如：
                        console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                        console.log(obj.limit); //得到每页显示的条数

                        //在点击页号和上下页的时候重新加载数据
                        if (!first) {
                            $.get('<%=path %>/data/borrow/frontList', {
                                page: obj.curr,
                                limit: obj.limit
                            }, function (data) {
                                fenye(data.rows);
                            });
                        }
                    }
                });
            }
        });
    }
</script>
</html>