/**
 * Created by yuhaihui8913 on 2017/5/27.
 */

    var $orderTree;
    var $table;
    var $zhName;
    var $year='';

function _initOrderTree(){
        var setting = {
            view: {
                selectedMulti: false
            },
            async: {
                enable: true,
                url: $("#basePath").attr("value") + "/adminOrderinfo/listTree",
                autoParam: ["id","name","title"],
                method:"post"
            },
            callback: {
                onAsyncError: onAsyncError,
                onAsyncSuccess: onAsyncSuccess,
                beforeAsync: beforeAsync,
                onClick: onClickHandle
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            edit: {
                enable: true,
                showRemoveBtn: false,
                showRenameBtn: false
            }
        };

        function beforeAsync(treeId, treeNode) {
            if(treeNode!=undefined&&treeNode.getParentNode()==null) {
                $zhName = treeNode.name;
                $year='';
            }
            else if(treeNode!=undefined&&treeNode.getParentNode()!=null){
                $year=treeNode.name;
            }
            showFakeloader();
            return true;
        }
        function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
            hideFakeloader();
            serverErrorAlert();
        }
        function onAsyncSuccess(event, treeId, treeNode, msg) {
            hideFakeloader();
            // var orderinfoId=$('#orderinfoId').val();
            // var node=$orderTree.getNodeByParam("id", orderinfoId, null);
            // if(node!=null){
            //     node=node.getParentNode().getParentNode();
            //     if(!node.open){
            //         $orderTree.expandNode(node, true, true, true);
            //     }
            // }
        }

        $orderTree=$.fn.zTree.init($("#orderTree"), setting);//初始化菜单zTree
        return $orderTree;
    }

    function _initTable() {
        $table = $('#table').bootstrapTable();
        return $table;
    }



function queryParams(params) {
    params.orderinfoId=$('#orderinfoId').val();
    params.catalogName=$catalogNameSel.val();
    return params;
}
function onClickHandle(event, treeId, treeNode) {
    $catalogNameSel.hide();
    if(!treeNode.isParent){
        $("#orderinfoId").val(treeNode.id);
        var node=treeNode.getParentNode().getParentNode();
        var orderinfoTitle='订单列表【'+treeNode.name+node.name+'】';
        $('#orderinfoTitle').text(orderinfoTitle);


        $catalogNameSel.val('');
        refreshTable();
        queryCatalogName();
    }
}

function refreshTable() {
    $table.bootstrapTable('removeAll');
    $table.bootstrapTable('refresh');
}

var $modal = $('#form-modal');
var $btn=$("#uploadBtn");
var $form=$("#uploadForm");
var $catalogNameSel=$("#catalogName");
var $kbTable=$("#searchKb-table");
var $setKbModal=$("#form-modal-setKb");

$(document).ready(function () {

    _initTable();
    $catalogNameSel.hide();
    _initOrderTree();
    initEvent();
});

function  initEvent() {
    $('.create').click(function () {
        resetFileInput($("#file"))
        $modal.modal('show');
    });
    $("#uploadBtn").click(function () {
        $btn.button("loading");
        var f = $form.ajaxSubmit({
            type: 'post',
            url: $("#basePath").attr("value") + "/adminOrderinfo/uploadOrder",
            success: function (data) {
                $btn.button('reset');
                if (data.resCode == 'success') {
                    swal('', data.resMsg, 'success');
                    $modal.modal("hide");
                    $orderTree.refresh();
                } else {
                    swal('', data.resMsg, 'error');
                }
            },
            error: function (XmlHttpRequest, textStatus, errorThrown) {
                $btn.button('reset');
                serverErrorAlert();
            }
        });
    });

}
function resetFileInput(file)
{
    file.after(file.clone().val("")); file.remove();
}
function actionFormatter(value,row,index) {
    var t=$("#isFront");//由于本js本前后端两个页面使用，这个标识在前段页面中使用
    printLog("",t.val());
    if(t.val()==''&&row.kbId){
        return [

            '<a class="viewKnowledgebase" href="javascript:" title="查看知识库"><i class="glyphicon glyphicon-eye-open"></i></a>',
            '<a class="viewStatistics" href="javascript:" title="查看统计"><i class="glyphicon glyphicon glyphicon-stats"></i></a>'

        ].join('&nbsp;&nbsp;&nbsp;');
    }else if(t.val()==''){
        return [
            '<a class="viewStatistics" href="javascript:" title="查看统计"><i class="glyphicon glyphicon glyphicon-stats"></i></a>'

        ].join('');
    }

    if(row.kbId){
        return [

            '<a class="removeOdAndKb" href="javascript:" title="解除订单知识库关系"><i class="glyphicon glyphicon-remove-sign"></i></a>',
            '<a class="viewKnowledgebase" href="javascript:" title="查看知识库"><i class="glyphicon glyphicon-eye-open"></i></a>',
            '<a class="viewStatistics" href="javascript:" title="查看统计"><i class="glyphicon glyphicon glyphicon-stats"></i></a>'

        ].join('&nbsp;&nbsp;&nbsp;');
    }else{
        return [
            '<a class="setKnowledgebase" href="javascript:" title="设置知识库"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="viewStatistics" href="javascript:" title="查看统计"><i class="glyphicon glyphicon glyphicon-stats"></i></a>'
        ].join('&nbsp;&nbsp;&nbsp;');
    }

}
function setKbActionFormatter(value,row,index) {
            return [
                '<a class="setOdAndKb" href="javascript:" title="绑定订单知识库关系"><i class="glyphicon glyphicon-edit"></i></a>',
                '<a class="viewKnowledgebase" href="javascript:" title="查看知识库"><i class="glyphicon glyphicon-eye-open"></i></a>'
            ].join('&nbsp;&nbsp;&nbsp;');
}
window.actionEvents = {
    'click .setKnowledgebase': function (e, value, row) {
            $("#orderdetailId").val(row.id);
            $setKbModal.modal("show");

            $kbTable.bootstrapTable('removeAll');

            $kbTable.bootstrapTable('resetSearch',row.scientificName);
            $kbTable.bootstrapTable('refresh');
    },
    'click .setOdAndKb': function (e, value, row) {


        swal({
            title: "您确定要执行该操作吗?",
            text: '绑定知识库信息',
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "是的，确定。",
            cancelButtonText: "不，谢谢。",
            closeOnConfirm: true
        }, function () {
            setTimeout(function () {
                var orderdetailId=$("#orderdetailId").val();
                $.post($("#basePath").attr("value") + '/adminOrderinfo/bindOdAndKb',
                    {orderdetailId:orderdetailId,kbId:row.id},
                    function (data, status) {

                        if (status == 'success'&&data.resCode=='success') {
                            $setKbModal.modal("hide");
                            swal('', data.resMsg, 'success');
                            $table.bootstrapTable('refresh');
                        }else{
                            swal('', data.resMsg, 'error');
                        }
                    }).error(function() { hideFakeloader();serverErrorAlert()});
            }, 1000);

        });

    },
    'click .removeOdAndKb': function (e, value, row) {
        swal({
            title: "您确定要执行该操作吗?",
            text: '解除知识库信息',
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "是的，确定。",
            cancelButtonText: "不，谢谢。",
            closeOnConfirm: true
        }, function () {
            setTimeout(function () {
                var orderdetailId=row.id;
                $.post($("#basePath").attr("value") + '/adminOrderinfo/removeBindOdAndKb',
                    {orderdetailId:orderdetailId},
                    function (data, status) {

                        if (status == 'success'&&data.resCode=='success') {
                            $setKbModal.hide();
                            swal('', data.resMsg, 'success');
                            $table.bootstrapTable('refresh');
                        }
                    }).error(function() { hideFakeloader();serverErrorAlert()});
            }, 1000);

        });

    },
    'click .viewKnowledgebase': function (e, value, row) {
        var kbId;
        if(row.hasOwnProperty("kbId"))
            kbId=row.kbId;
        else
            kbId=row.id;


        window.open($("#basePath").attr("value") +'/adminKnowledgebase/view?id='+kbId+'&odId='+row.id,'_blank');
        // layer.open({
        //     type: 2,
        //     area: ['700px', '530px'],
        //     fixed: false,
        //     maxmin: true,
        //     content: $("#basePath").attr("value") +'/adminKnowledgebase/view?id='+kbId+'&odId='+row.id
        // });
    },

    'click .viewStatistics': function (e, value, row) {
        var priceStatisticsChart = echarts.init(document.getElementById('priceStatistics'));
        var countStatisticsChart = echarts.init(document.getElementById('countStatistics'));
        priceStatisticsChart.showLoading();
        countStatisticsChart.showLoading();


        var l=layer.open({
            type: 1,
            title:'数据统计窗口',
            closeBtn: 1,
            area: ['820px', '410px'],
            skin: 'layui-layer-rim', //没有背景色
            shadeClose: false,
            content: $('#datanalysis'),
            cancel: function(index, layero){

            }
            });
        var data={odId:row.id}
        $.post($("#basePath").attr("value") + '/adminOrderinfo/priceStatistics',
            data,
            function (data, status) {

                if (status == 'success') {
                    var title=data.orderZhname+"\n"+data.orderdetailName+"价格走势分析"+"\n";
                    var seriesData=[];
                    var xAxisData=[];
                    var d={};
                    d.name=data.orderdetailName;
                    d.type='line';
                    d.stack='价格';
                    var price=[];
                    $.each(data.statisticsData,function (i,n) {
                        xAxisData.push(n.orderDate);
                        price.push(n.price);
                    })
                    d.label={
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    };
                    d.data=price;
                    seriesData.push(d);

                    var option = {
                        title: {
                            text: title
                        },
                        color: ['#ff7f50','#87cefa','#da70d6','#32cd32','#6495ed',
                            '#ff69b4','#ba55d3','#cd5c5c','#ffa500','#40e0d0',
                            '#1e90ff','#ff6347','#7b68ee','#00fa9a','#ffd700',
                            '#6699FF','#ff6666','#3cb371','#b8860b','#30e0e0'],

                        tooltip : {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'line',
                                label: {
                                    backgroundColor: '#6a7985'
                                }
                            }
                        },
                        xAxis: {
                            type : 'category',
                            boundaryGap : false,
                            data: xAxisData
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        yAxis: { type : 'value'},
                        series: seriesData
                    };
                    priceStatisticsChart.hideLoading();
                    // 使用刚指定的配置项和数据显示图表。
                    priceStatisticsChart.setOption(option);

                } else {

                    priceStatisticsChart.hideLoading();
                    layer.colse(l);
                    swal('', data.resMsg, 'error');
                }
            }).error(function() { priceStatisticsChart.hideLoading();layer.colse(l); serverErrorAlert()});
        $.post($("#basePath").attr("value") + '/adminOrderinfo/countStatistics',
            data,
            function (data, status) {

                if (status == 'success') {
                    var legendData=[];
                    var xAxisData=[];
                    var seriesData=[];

                    $.each( data, function(i, n){
                        $.each(n[Object.keys(n)],function (j,o) {
                            if(!xAxisData.contains(o.orderYear)){
                                xAxisData.push(o.orderYear);
                            }
                            if(!legendData.contains(o.orderinfoName)){
                                legendData.push(o.orderinfoName);
                            }
                        })
                    });

                    $.each(data,function (i,n) {
                        var _v=n[Object.keys(n)];
                        var num=[];
                        var d={};
                        d.name=(Object.keys(n))[0];
                        d.type='bar';
                        $.each(xAxisData,function (k,p){
                            $.each(_v,function (j,o) {
                                var _v1=o.orderYear;
                                if(p==_v1){
                                    num[k]=o.num;
                                    return false;
                                }else{
                                    num[k]=0;
                                }
                            })
                        });
                        d.areaStyle={normal: {}};
                        d.data=num;
                        d.label={
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        };
                        seriesData.push(d);
                    })
                    var option = {
                        color: ['#ff7f50','#87cefa','#da70d6','#32cd32','#6495ed',
                            '#ff69b4','#ba55d3','#cd5c5c','#ffa500','#40e0d0',
                            '#1e90ff','#ff6347','#7b68ee','#00fa9a','#ffd700',
                            '#6699FF','#ff6666','#3cb371','#b8860b','#30e0e0'],
                        title: {
                            text: '订单出现率统计'+'\n'
                        },
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            }
                        },
                        legend: {
                            data:legendData
                        },
                        xAxis: {
                            type : 'category',
                            data: xAxisData
                        },


                        grid: {
                            x: 80,
                            y: 60,
                            x2: 80,
                            y2: 60,
                            // width: {totalWidth} - x - x2,
                            // height: {totalHeight} - y - y2,
                            backgroundColor: 'rgba(0,0,0,0)',
                            borderWidth: 1,
                            borderColor: '#ccc'
                        },
                        yAxis: { type : 'value'},
                        series: seriesData
                    };
                    countStatisticsChart.hideLoading();
                    // 使用刚指定的配置项和数据显示图表。
                    countStatisticsChart.setOption(option);
                } else {
                    countStatisticsChart.hideLoading();
                    layer.colse(l);
                    swal('', data.resMsg, 'error');
                }
            }).error(function() {  countStatisticsChart.hideLoading();layer.colse(l);serverErrorAlert()});

        //window.open($("#basePath").attr("value") +'/adminKnowledgebase/view?id='+kbId+'&odId='+row.id,'_blank');

    }

};
function queryCatalogName() {
    var $params={orderinfoId:$("#orderinfoId").val()};
    $.post($("#basePath").attr("value") + '/adminOrderinfo/orderdetailCatalogNameJson',
        $params,
        function (data, status) {
            $catalogNameSel.show();
            if (status == 'success') {
                $catalogNameSel.empty();
                $catalogNameSel.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $catalogNameSel.append(new Option(data.catalogName, data.catalogName, false, true));
                });
                $catalogNameSel.val('')
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}