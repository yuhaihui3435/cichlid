/**
 * Created by yuhaihui8913 on 2016/12/27.
 */
var $paramsJson = [];
var $watermarkTransparency;
var $fileInputConfig;
var $qn_url;
var picMaxSize;
var editor;
//页面初始化的设置处理
$(document).ready(function () {
    //icheck组件的初始化
    // $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
    //     checkboxClass: 'icheckbox_flat-green',
    //     radioClass: 'iradio_flat-green'
    // });

    $("#comment_btn").on("click", commentSave);
    $("#conventional_btn").on("click", conventionalSave);
    $("#login_btn").on("click", loginSave);
    $("#notify_btn").on("click", notifySave);
    $("#seo_btn").on("click", seoSave);
    $("#watermark_btn").on("click", watermarkSave);
    $("#refresh_cache_btn").on("click",refreshCache);
    $("#refresh_kl_btn").on("click",refreshKl);

    picMaxSize = $("#pic_maxSize").val();
    $qn_url=$("#qn_url").val();
    $fileInputConfig={
        language: 'zh',
        allowedFileExtensions: ['jpg', 'png','jpeg','gif','bmp'],
        showUpload: false, //是否显示上传按钮
        maxFileSize: picMaxSize,
        showPreview:true,
        initialPreviewShowDelete:false,
        initialPreviewAsData: true,
        overwriteInitial: true,
        showCaption:false,
    };
    $watermarkTransparency = $('#watermarkTransparency').bootstrapSlider({step: 1, min: 0, max: 100, tooltip: 'show'});
    editor=initEditor('notice');
    loadSetting();

});


function commentSave() {
    $("#comment").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');

            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}
function conventionalSave() {
    $("#conventional").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');

            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}
function loginSave() {
    $("#login").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');

            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}
function notifySave() {
    $("#notify").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');

            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}
function seoSave() {
    $("#seo").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');

            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}
function watermarkSave() {

    $("#watermark").ajaxSubmit({
        type: 'post',
        url: $("#basePath").attr("value") + "/adminSetting/save",
        success: function (data) {
            if (data.resCode == 'success') {
                swal('', data.resMsg, 'success');
            } else {
                swal('', data.resMsg, 'error');
            }
        },
        error: function (XmlHttpRequest, textStatus, errorThrown) {
            serverErrorAlert();
        },
        beforeSubmit: function () {
            hideFakeloader();
        }
    });
}

function loadSetting() {
    showFakeloader();
    $.getJSON($("#basePath").attr("value") + "/adminSetting/getSettingJSON", function (result) {
        hideFakeloader();
        $.each(result, function (i, item) {
            var obj = $("#" + item.k);

            // if (item.k == 'watermarkTransparency') {
            //     $watermarkTransparency.bootstrapSlider('setValue', item.val);
            //     return true;
            // }
            if (obj[0]) {

                if(obj.attr("type")=='file'){

                    if(item.val!=''){
                        var config=$fileInputConfig;
                        var imgUrl=[$qn_url+item.val];
                        var initialPreviewConfig=[{caption: '当前图片',size: item.note, width: "120px", key: 1}]
                        config['initialPreview']=imgUrl;
                        config['initialPreviewConfig']=initialPreviewConfig;
                        $("#"+item.k+"_bak").val(item.val);
                        obj.fileinput(config).on('fileclear', function(event) {
                            $("#"+item.k+"_bak").val('');
                        });
                    }else{
                        obj.fileinput({
                            language: 'zh',
                            allowedFileExtensions: ['jpg', 'png','jpeg','gif','bmp'],
                            showUpload: false, //是否显示上传按钮
                            maxFileSize: picMaxSize,
                            showPreview:true,
                            initialPreviewShowDelete:false,
                            initialPreviewAsData: true,
                            overwriteInitial: true,
                            showCaption:false,
                        });
                    }

                }else if(item.k=='notice'){
                       editor.$txt.html(item.val);
                }else{
                    obj.val(item.val);
                }
            } else {
                obj = $("#" + item.k + "_y");
                if (obj[0]) {
                    if (item.val == '0') {
                        $("#" + item.k + "_y").iCheck('check');
                    } else if (item.val == '1') {
                        $("#" + item.k + "_n").iCheck('check');
                    }
                }
            }
        });
    }).error(function() { serverErrorAlert()});
}

function refreshCache() {
    showFakeloader();
    $.post($("#basePath").attr("value") + '/admincc/refreshCache',
        {},
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
}

function refreshKl() {
    showFakeloader();
    $.post($("#basePath").attr("value") + '/c/synZh',
        {},
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
}
