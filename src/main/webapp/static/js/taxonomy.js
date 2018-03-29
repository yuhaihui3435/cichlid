/**
 * Created by yuhaihui8913 on 2016/12/26.
 */

var zTree;
var pnId = '';//菜单刷新后，要展开的节点id
var $form = $('#tForm');//表单obj


var setting = {
    view: {
        selectedMulti: false
    },
    async: {
        enable: true,
        url: $("#basePath").attr("value") + "/adminTaxonomy/listTree",
        autoParam: ["id"],
        method:"post"
    },
    callback: {
        onRightClick: OnRightClick,
        onAsyncError: onAsyncError,
        onAsyncSuccess: onAsyncSuccess,
        beforeAsync: beforeAsync
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

//菜单tree采用异步的数据加载方式，数据加载前的处理，打开loader遮罩层。在下面的onAsyncError，onAsyncSuccess响应处理中关闭掉loader遮罩层
function beforeAsync(treeId, treeNode) {
    showFakeloader();
    return true;
}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    hideFakeloader();
    serverErrorAlert();
}
function onAsyncSuccess(event, treeId, treeNode, msg) {
    if (pnId != '') {
        var n = zTree.getNodeByParam("id", pnId, null);
        zTree.expandNode(n, true, false, true);
    }
    pnId='';
    hideFakeloader();
}
//菜单tree的右键响应处理
function OnRightClick(event, treeId, treeNode) {

    if (treeNode.parentId==0 && event.target.tagName.toLowerCase() != "button" ) {
        zTree.selectNode(treeNode);
        showRMenu("root", event.clientX, event.clientY);
    } else if (treeNode && !treeNode.noR) {
        zTree.selectNode(treeNode);
        showRMenu("node", event.clientX, event.clientY);
    }
}
//显示右键菜单
function showRMenu(type, x, y) {
    $("#rMenu ul").show();
    if (type == "root") {
        $("#m_del").hide();
        $("#m_edit").hide();
    } else {
        $("#m_del").show();
        $("#m_edit").show();
    }
    rMenu.css({"top": y + "px", "left": x + "px", "visibility": "visible"});
    $("body").bind("mousedown", onBodyMouseDown);
}
function hideRMenu() {
    if (rMenu) rMenu.css({"visibility": "hidden"});
    $("body").unbind("mousedown", onBodyMouseDown);
}
function onBodyMouseDown(event) {
    if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
        rMenu.css({"visibility": "hidden"});
    }
}
function addTreeNode() {
    $("#box-form").show();
    hideRMenu();
    $form.data('bootstrapValidator').resetForm(true);
    $('input[type="checkbox"].flat-red').iCheck('uncheck');
    $("#box-header-txt").text("新增");
    $("#id").val('');
    if (zTree.getSelectedNodes().length == 0) {
        $("#pId").val("0");
        $("#pName").text("无");
    } else {
        var n = zTree.getSelectedNodes()[0];
        pnId = n.id;
        $("#pId").val(n.id);
        $("#type").val(n.type);
        $("#pName").text(n.name);
    }
    $("#pDiv").show();
}
function editTreeNode() {
    $("#box-form").show();
    hideRMenu();
    $form.data('bootstrapValidator').resetForm(true);
    $('input[type="checkbox"].flat-red').iCheck('uncheck');
    $("#box-header-txt").text("编辑");
    var n = zTree.getSelectedNodes()[0];
    pnId = n.pId;
    $("#pId").val(n.pId);
    $("#pDiv").hide();
    $("#title").val(n.title);
    $("#type").val(n.type);
    $("#contentModule").val(n.contentModule).trigger("change");
    $("#text").val(n.text);
    $("#orderNumber").val(n.orderNumber);
    $("#icon").val(n.icon);
    $("#id").val(n.id);
    if(n.menu=='0')
    $("input[name='taxonomy.menu']").iCheck('check');
    if(n.showStatus=='0')
    $("input[name='taxonomy.show_status']").iCheck('check');
}
function delTreeNode() {
    hideRMenu();
    showFakeloader();
    var n = zTree.getSelectedNodes()[0];
    pnId = n.pId;
    swalConfirm("删除该节点。", function () {
        $.get($("#basePath").attr("value") + "/adminTaxonomy/del",
            {
                id: n.id
            },
            function (data, status) {
                hideFakeloader();

                if (status == 'success') {
                    var code = data.resCode;
                    if (code == 'fail') {
                        swal("", data.resMsg, "error");
                    } else {
                        zTree.reAsyncChildNodes(null, "refresh");
                    }

                } else {
                    serverErrorAlert();
                }
            }).error(function() { serverErrorAlert()});
    })
}
//表单验证设置
function tFormValidator() {
    $form.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'taxonomy.title': {
                validators: {
                    notEmpty: {
                        message: '标题必填'
                    }
                }
            },
            'taxonomy.text': {
                validators: {
                    notEmpty: {
                        message: '内容必填'
                    }
                }
            }
        }
    });
    $form.submit(function (ev) {
        ev.preventDefault();
    });//禁止submit自动提交
}

//表单的数据提交
function submitData() {
    showFakeloader();
    var pId = $("#pId").val();
    var id = $("#id").val();
    var action;
    if (id != '') {
        action = '/adminTaxonomy/update';
    } else {
        action = '/adminTaxonomy/save';
    }

    $.post($("#basePath").attr("value") + action,
        $form.serialize(),
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                if (data.resCode == 'success') {
                    zTree.reAsyncChildNodes(null, "refresh");
                    $form.data('bootstrapValidator').resetForm(true);
                    $("#pDiv").hide();

                    $("#box-form").hide();
                    swal('', data.resMsg, 'success');
                } else {
                    swal('', data.resMsg, 'error');
                    $("#btn").prop("disabled", false);
                }
            } else {
                swal('', data.resMsg, 'error');
                $("#btn").prop("disabled", false);
            }
        }).error(function() { serverErrorAlert()});
}

//页面初始化的设置处理
$(document).ready(function () {
    $.fn.zTree.init($("#tTree"), setting);//初始化菜单zTree
    zTree = $.fn.zTree.getZTreeObj("tTree");//获得菜单tree的引用
    rMenu = $("#rMenu");//获得右键菜单的引用
    tFormValidator();
    $("#contentModule").select2();
    $("#btn").on("click", function () {//表单的保存按钮的click事件的绑定
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            submitData();
        }
        else return;
    });
    //icheck组件的初始化
    // $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
    //     checkboxClass: 'icheckbox_flat-green',
    //     radioClass: 'iradio_flat-green'
    // });
});