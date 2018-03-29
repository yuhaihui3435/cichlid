/**
 * Created by yuhaihui8913 on 2017/1/5.
 */
/**
 * Created by yuhaihui on 2017/1/1.
 */
var $table;
var params = {};
var $viewModal = $("#view-modal");
$(document).ready(function () {
    $table = $("#table").bootstrapTable();
});


//列表中添加编辑连接
function actionFormatter(value, row, index) {


    var a = ['<a class="btn bg-olive edit" href="javascript:" title="编辑"><i class="glyphicon glyphicon-edit"></i>编辑</a>',
        '<a class="btn bg-olive view" href="javascript:" title="查看"><i class="glyphicon glyphicon-eye-open"></i>查看</a>'];
    if (row.top == '0') {
        a.push('<a class="btn bg-orange cancelTop" href="javascript:" title="取消置顶"><i class="fa fa-heart"></i>取消置顶</a>');
    } else {
        a.push('<a class="btn bg-olive setTop" href="javascript:" title="置顶"><i class="fa fa-heart-o"></i>置顶</a>');
    }
    if (row.good == '0') {
        a.push('<a class="btn bg-orange cancelGood" href="javascript:" title="取消精华"><i class="fa  fa-star"></i>取消精华</a>');
    } else {
        a.push('<a class="btn bg-olive setGood" href="javascript:" title="精华"><i class="fa  fa-star-o"></i>精华</a>');
    }
    // if(row.status=='01'){
    //     a.push('<a class="btn bg-olive contentCheck" href="javascript:" title="审核"><i class="fa fa-thumb-tack"></i>审核</a>');
    // }

    return a.join('&nbsp;&nbsp;&nbsp;');

}
//设置权限按钮click事件响应处理
window.actionEvents = {

    'click .edit': function (e, value, row) {
        window.location=$("#basePath").attr("value")+"/adminContent/toE?id="+row.id;
    },
    'click .view': function (e, value, row) {

        $viewModal.find(".box").empty();
        $viewModal.find(".box").load($("#basePath").attr("value") + '/adminContent/view?id='+row.id,function(response,status,xhr) {

            $viewModal.modal("show");
        });



    },
    'click .cancelTop': function (e, value, row) {
        params = {"content.id": row.id};
        updateContent("/adminContent/cancelTop");
    }
    ,
    'click .setTop': function (e, value, row) {
        params = {"content.id": row.id};
        updateContent("/adminContent/setTop");
    }
    ,
    'click .cancelGood': function (e, value, row) {
        params = {"content.id": row.id};
        updateContent("/adminContent/cancelGood");
    }
    ,
    'click .setGood': function (e, value, row) {
        params = {"content.id": row.id};
        updateContent("/adminContent/setGood");
    },
    // 'click .contentCheck': function (e, value, row) {
    //
    // }
}

function updateContent(action) {
    showFakeloader();
    $.post($("#basePath").attr("value") + action,
        params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                if (data.resCode == 'success') {
                    swal('', data.resMsg, 'success');
                    $table.bootstrapTable('refresh');
                } else {
                    swal('', data.resMsg, 'error');
                }
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function () {
        serverErrorAlert();
    });
}
//删除文章
function delContent() {
    var s = $table.bootstrapTable('getSelections');
    if (s.length > 0) {
        swalConfirm("确定要删除选中的文章吗？", delContentCB)
    } else {
        swal("", "请选择要删除的文章", "warning");
    }
}
//删除操作的回调处理
function delContentCB() {
    var s = $table.bootstrapTable('getSelections');
    var ids = [];
    $.each(s, function (i, n) {
        ids.push(n.id);
    });
    params = {"ids": ids.toString()}
    updateContent("/adminContent/del");
}

