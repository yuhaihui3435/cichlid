/**
 * Created by yuhaihui8913 on 2017/2/3.
 */
var $idcardtype = $('#idcardtype');
var $userInfoForm = $("#userInfo");
var $modifyPasswordForm = $("#modifyPassword");
$(function () {
    $idcardtype.select2({
        language: "zh-CN",
        placeholder: '请选择',
        width: "100%"
    });
    $userInfoForm.submit(function (ev) {
        ev.preventDefault();
    });
    $modifyPasswordForm.submit(function (ev) {
        ev.preventDefault();
    });
    formValidator();
    initEvent();

});

function initEvent() {
    $("#userInfoSave_btn").on("click", function () {
        var bootstrapValidator = $userInfoForm.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            showFakeloader();
            $.post($("#basePath").attr("value") + '/adminUser/update',
                $userInfoForm.serialize(),
                function (data, status) {
                    hideFakeloader();
                    if (status == 'success') {
                        if (data.resCode == 'success') {
                            swal('', data.resMsg, 'success');
                        } else {
                            swal('', data.resMsg, 'error');
                            $("#userInfoSave_btn").prop("disabled", false);
                        }
                    } else {
                        swal('', data.resMsg, 'error');
                        $("#userInfoSave_btn").prop("disabled", false);
                    }
                }).error(function () {
                $("#userInfoSave_btn").prop("disabled", false);
                serverErrorAlert()
            });
        }
        else return;
    });
    $("#modifyPwdSave_btn").on("click", function () {
        var bootstrapValidator = $modifyPasswordForm.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            showFakeloader();
            $.post($("#basePath").attr("value") + '/adminUser/modifyPassword',
                $modifyPasswordForm.serialize(),
                function (data, status) {
                    hideFakeloader();
                    if (status == 'success') {
                        if (data.resCode == 'success') {
                            swal('', data.resMsg, 'success');
                        } else {
                            swal('', data.resMsg, 'error');
                            $("#modifyPwdSave_btn").prop("disabled", false);
                        }
                    } else {
                        swal('', data.resMsg, 'error');
                        $("#modifyPwdSave_btn").prop("disabled", false);
                    }
                }).error(function () {
                $("#modifyPwdSave_btn").prop("disabled", false);
                serverErrorAlert()
            });
        }
        else return;
    });
}

function formValidator() {
    $userInfoForm.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
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
                    emailAddress: {
                        message: '邮箱格式不正确'
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

    $modifyPasswordForm.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'oldPwd': {
                validators: {
                    notEmpty: {
                        message: '旧密码必填'
                    }
                }
            },
            'newPwd': {
                validators: {
                    notEmpty: {
                        message: '新密码必填'
                    },
                    identical: {//相同
                        field: 'confrimNewPwd',
                        message: '两次密码不一致'
                    }
                }
            },
            'confrimNewPwd': {
                validators: {
                    notEmpty: {
                        message: '确认新密码必填'
                    },
                    identical: {//相同
                        field: 'newPwd',
                        message: '两次密码不一致'
                    }
                }
            }
        }
    });
}