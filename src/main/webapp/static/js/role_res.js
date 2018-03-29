/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
var $form = $('#resForm');//表单obj
var pnId = '';//菜单刷新后，要展开的节点id
var zTree, rMenu,zTree_r;
var $table,
    $modal = $('#form-modal'),
    $roleForm = $('#modal-form'),
    $filpTree,
    $filpTreeView;
var setRoleId='';//被选中的角色id临时保存变量
//菜单resTree的基本设置
var setting = {
    view: {
        selectedMulti: false
    },
    async: {
        enable: true,
        url: $("#basePath").attr("value") + "/adminRes/listTree",
        autoParam: ["id"],
        method:"post"
    },
    callback: {
        onRightClick: OnRightClick,
        beforeDrag: beforeDrag,
        onDrag: onDrag,
        beforeDrop: beforeDrop,
        onDrop: onDrop,
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


//菜单resTreeBindRole的基本设置
var setting_r = {
    view: {
        selectedMulti: false
    },
    async: {
        enable: true,
        url: $("#basePath").attr("value") + "/adminRole/loadRes",
        autoParam: ["id"],
        method:"post"
    },
    callback: {
        onAsyncSuccess: onAsyncSuccess_r,
        onAsyncError: onAsyncError,
        beforeAsync: beforeAsync_r
        
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    check:{
        enable: true
    }
};


//拖拽子菜单所在的父菜单，只能调整子所在的父，不能将父调整到子里面
function beforeDrag(treeId, treeNodes) {
    for (var i = 0, l = treeNodes.length; i < l; i++) {
        if (treeNodes[i].drag === false) {
            return false;
        }
    }
    return true;
}
function onDrag(event, treeId, treeNodes) {

};
function beforeDrop(treeId, treeNodes, targetNode, moveType) {
    var node = treeNodes[0];
    if (targetNode != null) {
        var cNodes = targetNode.children;
        if (cNodes != undefined) {
            for (var j = 0; j < cNodes.length; j++) {
                if (node.name == cNodes[j].name) {
                    swal("", "迁移节点的名称在目标结点下已经存在");
                    return false;
                }
            }
        }
        pId = targetNode.id;
    }
    return targetNode ? targetNode.drop !== false : true;
}
function onDrop(event, treeId, treeNodes, targetNode, moveType) {
    showFakeloader();
    var node = treeNodes[0];
    var id = node.id;
    var pId = '0';
    if (targetNode != null) {
        pId = targetNode.id;
    } else {
        return false;
    }
    pnId = pId;
    $.get($("#basePath").attr("value") + "/adminRes/move",
        {
            id: id,
            pId: pId
        },
        function (data, status) {
            hideFakeloader();

            if (status == 'success') {
                var code = data.resCode;
                if (code == 'fail') {
                    swal("", data.resMsg, "error");
                    zTree.reAsyncChildNodes(null, "refresh");
                }
            } else {
                serverErrorAlert();
                zTree.reAsyncChildNodes(null, "refresh");
            }
        }).error(function() { serverErrorAlert()});
}

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
        zTree.expandNode(n, true, true, true);
    }
    hideFakeloader();
}
//菜单tree的右键响应处理
function OnRightClick(event, treeId, treeNode) {
    if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
        zTree.cancelSelectedNode();
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
    $("#box-header-txt").text("新增");
    $("#id").val('');
    if (zTree.getSelectedNodes().length == 0) {
        $("#pId").val("0");
        $("#pName").text("无");
    } else {
        var n = zTree.getSelectedNodes()[0];
        pnId = n.id;
        $("#pId").val(n.id);
        $("#pName").text(n.name);
    }
    $("input[name='res.logged'][value='0']").iCheck('check');
    $("#pDiv").show();
}
function editTreeNode() {
    $("#box-form").show();
    hideRMenu();
    $form.data('bootstrapValidator').resetForm(true);
    $("#box-header-txt").text("编辑");
    var n = zTree.getSelectedNodes()[0];
    pnId = n.pId;
    if(pnId==null)pnId=0;
    $("#pId").val(pnId);
    $("#pDiv").hide();
    $("#description").val(n.description);
    $("#name").val(n.name);
    $("#url").val(n.resUrl);
    $("#seq").val(n.seq);
    $("#id").val(n.id);
    $("input[name='res.logged'][value=" + n.logged + "]").iCheck('check');
}
function delTreeNode() {
    hideRMenu();
    showFakeloader();
    var n = zTree.getSelectedNodes()[0];
    pnId = n.pId;
    swalConfirm("删除该权限节点。", function () {
        $.get($("#basePath").attr("value") + "/adminRes/del",
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
//resTreeBindRole加载数据成功的回调
function onAsyncSuccess_r() {
    hideFakeloader();
}
//resTreeBindRole加载数据前的回调
function beforeAsync_r() {
    showFakeloader();
    return true;
}

//表单验证设置
function formValidator() {
    $form.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'res.name': {
                validators: {
                    notEmpty: {
                        message: '菜单名必填'
                    }
                }
            },
            'res.url': {
                validators: {
                    notEmpty: {
                        message: '菜单地址必填'
                    }
                }
            }
        }
    });
    $form.submit(function (ev) {
        ev.preventDefault();
    });//禁止submit自动提交
}
//表单验证设置
function roleFormValidator() {
    $roleForm.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'role.name': {
                validators: {
                    notEmpty: {
                        message: '角色名必填'
                    }
                }
            }
        }
    });
    $roleForm.submit(function (ev) {
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
        action = '/adminRes/update';
    } else {
        action = '/adminRes/save';
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
                    $("input[name='res.logged'][value='0']").iCheck('check');
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
//表单的数据提交
function submitRoleData() {
    showFakeloader();

    var id = $("#roleId").val();
    var action;
    if (id != '') {
        action = '/adminRole/update';
    } else {
        action = '/adminRole/save';
    }
    $.post($("#basePath").attr("value") + action,
        $roleForm.serialize(),
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                if (data.resCode == 'success') {
                    $modal.modal("hide");
                    swal('', data.resMsg, 'success');
                    $table.bootstrapTable('refresh');
                } else {
                    swal('', data.resMsg, 'error');
                    $("#role_btn").prop("disabled", false);
                }
            } else {
                swal('', data.resMsg, 'error');
                $("#role_btn").prop("disabled", false);
            }
        }).error(function() { serverErrorAlert()});
}
//角色表单模式窗体控制
function showModal(title, row) {
    row = row || {
            id: '',
            name: '',
            description: ''
        };
    $modal.find('.modal-title').text(title);
    for (var name in row) {
        $modal.find('input[name="' + name + '"]').val(row[name]);
    }
    $modal.modal('show');
}
//角色table中添加每条数据后面的设置权限超链接
function actionFormatter(value) {
    return [
        '<a class="setRes" href="javascript:" title="设置权限"><i class="glyphicon glyphicon-edit"></i></a>'

    ].join('');
}
//设置权限按钮click事件响应处理
window.actionEvents = {
    'click .setRes': function (e, value, row) {
        if($filpTreeView=='back'){
            setRoleId=row.id;
            zTree_r.setting.async.otherParam={roleId:function(){return setRoleId}};
            zTree_r.reAsyncChildNodes(null, "refresh");
        }else {
            $filpTreeView = 'back';//设置翻转到back
            setRoleId = row.id;
            $filpTree.flip(true);
        }
    }
};

//页面初始化的设置处理
$(document).ready(function () {
    $.fn.zTree.init($("#resTree"), setting);//初始化菜单zTree
    zTree = $.fn.zTree.getZTreeObj("resTree");//获得菜单tree的引用
    rMenu = $("#rMenu");//获得右键菜单的引用
    $("#pDiv").hide();//父节点显示div默认隐藏
    formValidator();//初始化form表单的验证规则
    roleFormValidator();
    $("#btn").on("click", function () {//表单的保存按钮的click事件的绑定
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            submitData();
        }
        else return;
    });
    $("#role_btn").on("click", function () {//表单的保存按钮的click事件的绑定
        var bootstrapValidator = $roleForm.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            submitRoleData();
        }
        else return;
    });
    // //icheck组件的初始化
    // $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
    //     checkboxClass: 'icheckbox_flat-green',
    //     radioClass: 'iradio_flat-green'
    // });
    $('.create').click(function () {
        showModal($(this).text());
    });
    $('.del').click(function () {
        var selections = $table.bootstrapTable('getSelections');
        if (selections.length == 0) {
            swal('', '请选择要删除的角色', 'error');
        } else {
            swalConfirm("删除该角色。",function() {
                var ids = '';
                for (var i = 0; i < selections.length; i++) {
                    if (ids == '') {
                        ids = selections[i].id + '';
                    } else {
                        ids = ids + "," + selections[i].id;
                    }
                }
                showFakeloader()
                $.post($("#basePath").attr("value") + "/adminRole/del",
                    {ids: ids},
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
                    }).error(function() { serverErrorAlert()});
            });
        }
    });
    $modal.on('hidden.bs.modal', function () {
        $roleForm.data('bootstrapValidator').resetForm(true);
        $roleForm.data('bootstrapValidator').destroy();
        $roleForm.data('bootstrapValidator', null);
        roleFormValidator();
    });

    $table = $('#table').bootstrapTable();
    $table.on('editable-save.bs.table', function ($el, field, row, oldValue) {

        if (field == 'name' && row.name == '') {
            swal('', '名称不能为空', 'error');
            $table.bootstrapTable('refresh');
            return false;
        }

        var param = {'role.id': row.id, 'role.name': row.name, 'role.description': row.description}
        showFakeloader();
        $.post($("#basePath").attr("value") + "/adminRole/update",
            param,
            function (data, status) {
                hideFakeloader();
                if (status == 'success') {
                    if (data.resCode == 'success') {
                        swal('', data.resMsg, 'success');
                    } else {
                        swal('', data.resMsg, 'error');
                    }
                } else {
                    swal('', data.resMsg, 'error');
                }
            }).error(function() { serverErrorAlert()});
    });
    $filpTree=$("#filpTree").flip({
        trigger: 'manual',
        autoSize:false
    }).on('flip:done',function(){
        if($filpTreeView=='back'){
            if(zTree_r==undefined) {
                setting_r.async.otherParam={roleId:function(){return setRoleId}};
                setting_r.check.chkboxType = {"Y": "ps", "N": "ps","enable": true};
                $.fn.zTree.init($("#resTreeBindRole"), setting_r);//初始化菜单resTreeBindRole
                zTree_r = $.fn.zTree.getZTreeObj("resTreeBindRole");//获得菜单resTreeBindRole的引用
            }else{
                zTree_r.reAsyncChildNodes(null, "refresh");
            }
        }

    });
    $("#backToFront").on('click',function () {
        $filpTreeView='front';//设置即将翻转到front
        $filpTree.flip(false);
    });
    $("#saveRes").on('click',function () {
       var checks=zTree_r.getCheckedNodes(true);
        // if(checks.length==0){
        //     swal('','请选择权限','error');
        //     return false;
        // }
        var resIds;
        $.each(checks,function (index,data) {
            if(!resIds){
                resIds=data.id;
            }else{
                resIds=resIds+","+data.id;
            }
        });
        var params={'roleId':setRoleId,resIds:resIds};
        $.post($("#basePath").attr("value") + "/adminRole/setRes",
            params,
            function (data, status) {
                hideFakeloader();
                if (status == 'success') {
                    if (data.resCode == 'success') {
                        swal('', data.resMsg, 'success');
                    } else {
                        swal('', data.resMsg, 'error');
                    }
                } else {
                    swal('', data.resMsg, 'error');
                }
            }).error(function() { serverErrorAlert()});

    });



});


