/**
 * Created by yuhaihui8913 on 2016/12/2.
 */

var $table = $('#table').bootstrapTable(),
    $modal = $('#form-modal'),
    $idcardtype=$('#idcardtype'),
    $form=$('#modal-form'),
    $roleIds=$('#roleIds'),
    $uId='';
    $viewModal=$("#view-modal");

$(function () {
    formValidator();
    $('.create').click(function () {
        $uId='';
        $('#id').val('');
        showModal($(this).text());
    });
    $('.forbidden').click(function () {
        var selections = $table.bootstrapTable('getSelections');
        if (selections.length == 0) {
            swal('', '请选择要禁用的用户', 'error');
        } else {
            swalConfirm("禁用该用户。",function() {
                var ids = '';
                for (var i = 0; i < selections.length; i++) {
                    if (ids == '') {
                        ids = selections[i].id + '';
                    } else {
                        ids = ids + "," + selections[i].id;
                    }
                }
                showFakeloader()
                $.post($("#basePath").attr("value") + "/adminUser/forbidden",
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
    $('.resumed').click(function () {
        var selections = $table.bootstrapTable('getSelections');
        if (selections.length == 0) {
            swal('', '请选择要恢复的用户', 'error');
        } else {
            swalConfirm("恢复该用户。",function() {
                var ids = '';
                for (var i = 0; i < selections.length; i++) {
                    if (ids == '') {
                        ids = selections[i].id + '';
                    } else {
                        ids = ids + "," + selections[i].id;
                    }
                }
                showFakeloader()
                $.post($("#basePath").attr("value") + "/adminUser/resumed",
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
    $modal.on('hidden.bs.modal',function(){
        $form.data('bootstrapValidator').resetForm(true);
        $form.data('bootstrapValidator').destroy();
        $form.data('bootstrapValidator', null);
        formValidator();
    });
    $modal.on('show.bs.modal',function() {
        $roleIds.empty();
        var params={};
        if($uId!='')
            params={userId:$uId};
        $.post($("#basePath").attr("value") + '/adminUser/loadRoles',
            params,
            function (data, status) {
                hideFakeloader();
                if (status == 'success') {
                        var ownRoles=data.ownRoles;
                        var ownRoleIds=[];
                        $.each(ownRoles,function (index,data) {
                            ownRoleIds[index]=data.id;
                        })
                        var allRoles=data.allRoles;
                        $.each(allRoles,function (index,data) {
                            $roleIds.append(new Option(data.name, data.id, false, true));
                        });
                        $roleIds.val(ownRoleIds).trigger('change');
                } else {
                    swal('', data.resMsg, 'error');
                }
            }).error(function() { serverErrorAlert()});


    });
    $idcardtype.select2({
        language: "zh-CN",
        placeholder:'请选择',
        width: "100%"
    });
    $roleIds.select2({
        language: "zh-CN",
        width: "100%",
        multiple: true,
        placeholder:'请选择',
        tag:true,
    templateSelection: template

    });



    $form.submit(function(ev){ev.preventDefault();});
    $("#btn").on("click", function(){
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if(bootstrapValidator.isValid()){
            var action='/adminUser/save';

            if($uId!=''){
                action='/adminUser/update';
            }
            showFakeloader();
            $.post($("#basePath").attr("value") + action,
                $form.serialize(),
                function (data, status) {
                    hideFakeloader();
                    if (status == 'success') {
                        if (data.resCode == 'success') {
                            swal('', data.resMsg, 'success');
                            $modal.modal('hide');
                            $table.bootstrapTable('refresh');
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
        else return;
    });
});

function showModal(title, row) {
    $modal.find('.modal-title').text(title);
    $modal.modal('show');
}

function formValidator(){
    $form.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'user.loginname': {
                validators: {
                    notEmpty: {
                        message: '登录名必填'
                    },
                    regexp:{
                        regexp:/^[a-zA-Z0-9_]{6,50}$/,
                        message:'登录名又字母数字或下划线构成,长度在6-50之间'
                    }

                }
            },
            'user.nickname': {
                validators: {
                    notEmpty: {
                        message: '昵称必填'
                    }
                }
            },
            'user.email': {
                validators: {
                    notEmpty: {
                        message: '邮箱必填'
                    },
                    emailAddress:{
                        message:'邮箱格式不正确'
                    }
                }
            },
            'user.phone': {
                validators: {
                    notEmpty: {
                        message: '电话必填'
                    }
                }
            }
        }
    });
}

//列表中添加编辑连接
function actionFormatter(value) {
    return [
        '<a class="edit" href="javascript:" title="编辑"><i class="glyphicon glyphicon-edit"></i></a>',
        '<a class="view" href="javascript:" title="查看"><i class="glyphicon glyphicon-eye-open"></i></a>'
    ].join('&nbsp;&nbsp;&nbsp;');
}
//设置权限按钮click事件响应处理
window.actionEvents = {

    'click .edit': function (e, value, row) {
        $uId=row.id;
        $("#id").val($uId);
        $("#loginname").val(row.loginname);
        $("#phone").val(row.phone);
        $("#email").val(row.email);
        $("#idcard").val(row.idcard);
        $idcardtype.val(row.idcardtype).trigger("change");
        $("#nickname").val(row.nickname);
        $("#isAdmin").val(row.isAdmin);

        $modal.modal("show");
    },
    'click .view': function (e, value, row) {
        $viewModal.find(".form-horizontal").empty();
        $(".widget-userinfo-username").text(row.nickname);
        $(".widget-userinfo-desc").text(row.signature);
        $viewModal.find(".img-circle").attr("src",row.avatar);
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">账号</label> <div class="col-md-6"><p class="form-control-static">'+row.loginname+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">EMAIL</label> <div class="col-md-6"><p class="form-control-static">'+row.email+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">EMAIL认证</label> <div class="col-md-6"><p class="form-control-static">'+row.emailStatusTxt+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">电话号</label> <div class="col-md-6"><p class="form-control-static">'+row.phone+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">电话号认证</label> <div class="col-md-6"><p class="form-control-static">'+row.phoneStatusTxt+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">证件类型</label> <div class="col-md-6"><p class="form-control-static">'+row.idCardtypeTxt+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">证件号</label> <div class="col-md-6"><p class="form-control-static">'+row.idcard+'</p></div> </div>');

        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">积分</label> <div class="col-md-6"><p class="form-control-static">'+row.score+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">第三方渠道</label> <div class="col-md-6"><p class="form-control-static">'+row.channel+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">第三方ID</label> <div class="col-md-6"><p class="form-control-static">'+row.thirdId+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">创建时间</label> <div class="col-md-6"><p class="form-control-static">'+moment(row.cAt).format('YYYY-MM-DD')+'</p></div> </div>');
        if(row.activated!='')
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">激活时间</label> <div class="col-md-6"><p class="form-control-static">'+moment(row.activated).format('YYYY-MM-DD')+'</p></div> </div>');
        if(row.logged!='')
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">最后登录时间</label> <div class="col-md-6"><p class="form-control-static">'+moment(row.logged).format('YYYY-MM-DD')+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">发布数量</label> <div class="col-md-6"><p class="form-control-static">'+row.contentCount+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">回复数量</label> <div class="col-md-6"><p class="form-control-static">'+row.commentCount+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">是否接受社区通知</label> <div class="col-md-6"><p class="form-control-static">'+row.receiveMsgTxt+'</p></div> </div>');
        $viewModal.find(".form-horizontal").append('<div class="form-group" style="margin-top: 20px"><label class="col-md-6 control-label">账户状态</label> <div class="col-md-6"><p class="form-control-static">'+row.statusTxt+'</p></div> </div>');

        $viewModal.modal("show");
    }
}

