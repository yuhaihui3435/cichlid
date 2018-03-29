/**
 * Created by yuhaihui8913 on 2017/2/6.
 */
var $area=$("#area"),
    $p_subGroup=$("#p_subGroup"),
    $subGroup=$("#subGroup"),
    $p_species=$("#p_species"),
    $species=$("#species"),
    $q_rs=$("#q_rs"),
    $searchBtn=$("#search_btn");
$params={};

//脚本初始化
$(document).ready(function () {
    init();
    initSelect();
    search(1);
});

function init() {
    //$q_rs.hide();
    document.onkeydown = function(e){
        var ev = document.all ? window.event : e;
        if(ev.keyCode==13) {
            search();
        }
    }
    $searchBtn.on("click",function () {
        search(1);
    })
    var delay = (function () {
        var timer = 0;
        return function (callback, time) {
            clearTimeout(timer);
            timer = setTimeout(callback, time);
        };
    })();
    $('#search').keyup(function () {
        delay(function () {
            var v=$('#search').val();
            if(v!="")
                search(1);
        }, 1000);
    });

}

function initSelect() {
    $area.select2({
        language: "zh-CN",
        width: "100%"
    });
    $p_subGroup.select2({
        language: "zh-CN",
        width: "100%"
    });
    $subGroup.select2({
        language: "zh-CN",
        width: "100%"
    });
    $p_species.select2({
        language: "zh-CN",
        width: "180px"
    });
    $species.select2({
        language: "zh-CN",
        width: "180px"
    });

}

//查询亚群体子集合
function querySGChildren(val) {

    if(val==''){
        $subGroup.empty();
        $subGroup.append(new Option('请先选择大类', '', false, true));
        $subGroup.val("").trigger("change");
        //swal("",'请选择亚群体大类',"warning");
        return ;
    }
    showFakeloader();
    $params={pId:val}
    $.post($("#basePath").attr("value") + '/adminTaxonomy/getChildrenByPId',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $subGroup.empty();
                $subGroup.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $subGroup.append(new Option(data.title, data.id, false, true));
                });
                $subGroup.val('').trigger('change');


            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

//查询属种子集合
function querySChildren(val) {

    if(val==''){
        $species.empty();
        $species.append(new Option('请先选择大类', '', false, true));
        $species.val("").trigger("change");
        //swal("",'请选择属种大类',"warning");
        return ;
    }
    showFakeloader();
    $params={pId:val}
    $.post($("#basePath").attr("value") + '/adminTaxonomy/getChildrenByPId',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $species.empty();
                $species.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $species.append(new Option(data.title, data.id, false, true));
                });

                $species.val('').trigger('change');

            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader(); serverErrorAlert()});
}

function search(page) {
    var basePath=$("#basePath").attr("value");
    var sgId=$subGroup.val();
    var speciesId=$species.val();
    var areaId=$area.val();
    var search=$("#search").val();
    if(!page)page=1;
    $params={"knowledgebase.sgId":sgId,"knowledgebase.speciesId":speciesId,"knowledgebase.areaId":areaId,search:search,offset:page};
    showFakeloader();
    $.post($("#basePath").attr("value") + '/searchK',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                var rs=data.list;
                if(rs.length==0){swal("","没有查询到结果","warning"); return ;}
                $q_rs.show();
                var totalPage=data.totalPage;
                var pageNumber=data.pageNumber;
                if(pageNumber&&pageNumber<totalPage){
                    var offset=pageNumber*data.pageSize;
                    $(".box-footer").html("<a href=\"javascript:search("+offset+")\" class=\"uppercase\">加载更多</a>")
                }else{
                    $(".box-footer").empty();
                }
                if(page==1){
                    $(".product-list-in-box").empty();
                }
                $.each(rs,function (index,d) {
                    var html="";
                    if(d.thumbnail!=''){
                        html="<li class=\"item\">"
                            +"<div class=\"product-img\">"
                            +    "<img src=\""+$("#qn_url").val()+d.thumbnail+"?imageView2/2/w/96/h/96/interlace/0/q/100\" alt=\"Product Image\">"
                            +"</div>"
                            +"<div class=\"product-info\">"
                            +"<a href=\""+basePath+"/view?id="+d.id+"\" class=\"product-title\" target='_blank'>";
                        if(d.rareTxt=="稀少"){
                            html=html+"<span class=\" label label-warning  \" style='margin-right: 10px;'>稀少</span>";
                        }
                        if(d.rareTxt=="罕见"){
                            html=html+"<span class=\"label label-success  \" style='margin-right: 10px;'>罕见</span>";
                        }
                        if(d.rareTxt=="受保护"){
                            html=html+"<span class=\"label bg-purple  \" style='margin-right: 10px;'>受保护</span>";
                        }
                        html=html+d.scName+"&nbsp;&nbsp;"+d.areaTxt+"";
                        // if(d.zhName!="")
                        //     html=html+"<span class=\"label label-info pull-right \"><h4>"+d.zhName+"</h4></span>"+"</a>";
                        // else
                            html=html+"<span class=\"label  pull-right \"><h4></h4></span>"+"</a>";

                        html=html+"<span class=\"product-description \">"
                            +"中文名:"+d.zhName+"</span><span class=\"product-description\">地域:"+d.areaTxt+"</span><span class=\"product-description\">亚群体:"+d.sgTxt+"</span><span class=\"product-description\">属种:"+d.speciesTxt
                            +"</span></div></li>";
                        $(".product-list-in-box").append(html);
                    }else{

                        html="<li class=\"item\">"
                            +"<div class=\"product-img\">"
                            +    "<img src=\""+$("#qn_url").val()+d.thumbnail+"/images/sys/df-noPic.png?imageView2/2/w/96/h/96/interlace/0/q/100\" alt=\"Product Image\">"
                            +"</div>"
                            +"<div class=\"product-info\">"
                            +"<a href=\""+basePath+"/view?id="+d.id+"\" target=\"_blank\" class=\"product-title\">";

                        if(d.rareTxt=="稀少"){
                            html=html+"<span class=\"label label-warning  \" style='margin-right: 10px;'>稀少</span>";
                        }
                        if(d.rareTxt=="罕见"){
                            html=html+"<span class=\"label label-success  \" style='margin-right: 10px;'>罕见</span>";
                        }
                        if(d.rareTxt=="受保护"){
                            html=html+"<span class=\"label bg-purple  \" style='margin-right: 10px;'>受保护</span>";
                        }

                        html=html+d.scName+"&nbsp;&nbsp;"+d.areaTxt+"";
                        // if(d.zhName!="")
                        //     html=html+"<span class=\"label label-info pull-right \"><h4>"+d.zhName+"</h4></span>"+"</a>";
                        // else
                            html=html+"<span class=\"label pull-right \"><h4></h4></span>"+"</a>";

                        html=html+"<span class=\"product-description\">"
                            +"中文名:"+d.zhName+"</span><span class=\"product-description\">地域:"+d.areaTxt+"</span><span class=\"product-description\">亚群体:"+d.sgTxt+"</span><span class=\"product-description\">属种:"+d.speciesTxt
                            +"</span></div></li>";
                        $(".product-list-in-box").append(html);
                    }
                })
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader(); serverErrorAlert()});
}

function showDetail(id) {
    var index = layer.open({
        type: 2,
        content: $("#basePath").attr("value") + '/view?id='+id,
        area: ['320px', '195px'],
        maxmin: true
    });
    layer.full(index);

}

function filterSearch(obj) {
    var val=obj.value;
    if(val!=""){
        search(1);
    }
}

