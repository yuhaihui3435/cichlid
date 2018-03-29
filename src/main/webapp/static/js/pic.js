/**
 * Created by yuhaihui on 2017/10/31.
 */


var $table;
var params = {};
$(document).ready(function () {
    $table = $("#table").bootstrapTable();
});


//列表中添加编辑连接
function actionFormatter(value, row, index) {


    var a = [
        '<a class="btn bg-olive view" href="javascript:" title="查看"><i class="glyphicon glyphicon-eye-open"></i>查看</a>'];


    return a.join('&nbsp;&nbsp;&nbsp;');

}

//设置权限按钮click事件响应处理
window.actionEvents = {


    'click .view': function (e, value, row) {

    }

}

function req(action) {
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
//删除图片
function delPic() {
    var s = $table.bootstrapTable('getSelections');
    if (s.length > 0) {
        swalConfirm("确定要删除选中的图片吗？", delContentCB)
    } else {
        swal("", "请选择要删除的图片", "warning");
    }
}
//删除操作的回调处理
function delPicCB() {
    var s = $table.bootstrapTable('getSelections');
    var ids = [];
    $.each(s, function (i, n) {
        ids.push(n.id);
    });
    params = {"ids": ids.toString()}
    req("/adminPic/del");
}


//图片审核通过
function egsi() {
    var s = $table.bootstrapTable('getSelections');
    if (s.length > 0) {
        swalConfirm("确定要审核通过吗？", egsiCB)
    } else {
        swal("", "请选择要审核的图片", "warning");
    }
}
//审核通过回调处理
function egsiCB() {
    var s = $table.bootstrapTable('getSelections');
    var ids = [];
    $.each(s, function (i, n) {
        ids.push(n.id);
    });
    params = {"ids": ids.toString()}
    req("/adminPic/egsi");
}