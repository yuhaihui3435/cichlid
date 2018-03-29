/**
 * Created by yuhaihui8913 on 2017/5/19.
 */
var $shengSelect = $('#shengSelect'),
    $shiSelect=$('#shiSelect'),
    $quSelect=$('#quSelect'),
    $stateSelect=$('#stateSelect'),
    $f_shengSelect = $('#f_shengSelect'),
    $f_shiSelect=$('#f_shiSelect'),
    $f_quSelect=$('#f_quSelect'),
    $catalog=$('#catalog'),
    $orderType=$('#orderType'),
    $table=$('#table'),
    $uId='',
    $editor='',
    $modal = $('#form-modal'),
    $params={},
    $type=$("#type"),
    $btn=$("#btn"),
    $compensate=$('#compensate'),
    $auth=$('#auth'),
    $isTop=$('#isTop'),
    $orderSupport=$('#orderSupport'),
    $qrCode=$("#qrCode");
    $qrCode_bak=$("#qrCode_bak");
    $form=$("#modal-form");
var $qn_url = $("#qn_url").val();
var $picMaxSize = $("#pic_maxSize").val();
var $review_shiId="",$review_quId="";
var $fileInputConfig = {
    language: 'zh',
    allowedFileExtensions: ['jpg', 'png', 'jpeg', 'gif', 'bmp'],
    showUpload: false, //是否显示上传按钮
    maxFileSize: $picMaxSize,
    showPreview: true,
    maxFileCount: 5,
    initialPreviewShowDelete: false,
    initialPreviewAsData: true,
    overwriteInitial: true,
    showCaption: false,
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
};
var $fileInputConfig_qr = {
    language: 'zh',
    allowedFileExtensions: ['jpg', 'png', 'jpeg', 'gif', 'bmp'],
    showUpload: false, //是否显示上传按钮
    maxFileSize: $picMaxSize,
    showPreview: true,
    maxFileCount: 5,
    initialPreviewShowDelete: false,
    initialPreviewAsData: true,
    overwriteInitial: true,
    showCaption: false,
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
};


$(document).ready(function () {
    initSelect();
    initEvent();
    $form.submit(function(ev){ev.preventDefault();});
    formValidator();
    $modal.on('hidden.bs.modal', function() {
        $form.data('bootstrapValidator').destroy();
        $form.data('bootstrapValidator', null);
        formValidator();
    });
});

function initEvent() {
    $('.create').click(function () {
        $uId='';
        $('#id').val('');
        showModal($(this).text());
    });
    $btn.on("click", function () {
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            showFakeloader();
            var f = $form.ajaxSubmit({
                type: 'post',
                url: $("#basePath").attr("value") + "/adminSupplier/saveOrUpdate",
                success: function (data) {
                    hideFakeloader();
                    if (data.resCode == 'success') {
                        swal('', data.resMsg, 'success');
                        $modal.modal("hide");
                        $btn.prop("disabled", false);
                        $table.bootstrapTable("refresh");
                    } else {
                        $("#thumbnail_bak").val('');
                        $fileInputConfig.initialPreview=[];
                        $fileInputConfig.initialPreviewConfig=[];
                        $("#thumbnail").fileinput('destroy')
                        $("#thumbnail").fileinput($fileInputConfig);
                        $btn.prop("disabled", false);
                        swal('', data.resMsg, 'error');
                    }
                },
                error: function (XmlHttpRequest, textStatus, errorThrown) {
                    $btn.prop("disabled", false);
                    hideFakeloader();
                    serverErrorAlert();
                }

            });
        }
        else return;
    });
    $('.delete').click(function () {
        var selections = $table.bootstrapTable('getSelections');
        if (selections.length == 0) {
            swal('', '请选择要删除的鱼商', 'error');
        } else {
            swalConfirm("删除鱼商信息。",function() {
                var ids = '';
                for (var i = 0; i < selections.length; i++) {
                    if (ids == '') {
                        ids = selections[i].id + '';
                    } else {
                        ids = ids + "," + selections[i].id;
                    }
                }
                showFakeloader()
                $.post($("#basePath").attr("value") + "/adminSupplier/del",
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
                    }).error(function() {hideFakeloader(); serverErrorAlert()});
            });
        }
    });
}

function initSelect() {
    $shengSelect.select2({
        language: "zh-CN",
        width: "150px"
    });
    $shiSelect.select2({
        language: "zh-CN",
        width: "150px"
    });
    $quSelect.select2({
        language: "zh-CN",
        width: "150px"
    });
    $f_quSelect.select2({
        language: "zh-CN",
        width: "150px"
    })
    $f_shiSelect.select2({
        language: "zh-CN",
        width: "150px"
    })
    $f_shengSelect.select2({
        language: "zh-CN",
        width: "150px"
    })
    $auth.select2({
        language: "zh-CN",
        width: "150px"
    })
    $type.select2({
        language: "zh-CN",
        width: "150px"
    })
    $compensate.select2({
        language: "zh-CN",
        width: "150px"
    })
    $compensate.select2({
        language: "zh-CN",
        width: "150px"
    })
    $isTop.select2({
        language: "zh-CN",
        width: "100px"
    })
    $orderSupport.select2({
        language: "zh-CN",
        width: "100px"
    })
    $stateSelect.select2({
        language: "zh-CN",
        width: "150px"
    })
    $catalog.select2({
        language: "zh-CN",
        width: "100%",
        allowClear: true,
        multiple: true,
        placeholder:'请选择',
        tag:true,
        templateSelection: template
    })
    $orderType.select2({
        language: "zh-CN",
        width: "100%",
        multiple: true,
        placeholder:'请选择',
        tag:true,
        templateSelection: template
    })
}

//查询市
function queryShi() {
    var value=$shengSelect.val();
    var txt=$shengSelect.find("option:selected").text();
    if(value==''){
        return ;
    }
    showFakeloader();
    $params={shengName:txt,shengId:value}
    $.post($("#basePath").attr("value") + '/c/queryShiList',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $shiSelect.empty();
                $shiSelect.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $shiSelect.append(new Option(data.name, data.id, false, true));
                });
                $shiSelect.val('').trigger('change');
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});

    refreshTable();
}

//查询区
function queryQu() {
    var value=$shiSelect.val();
    var txt=$shiSelect.find("option:selected").text();
    if(value==''){
        return ;
    }
    showFakeloader();
    $params={shiName:txt,shiId:value}
    $.post($("#basePath").attr("value") + '/c/queryQuList',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $quSelect.empty();
                $quSelect.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $quSelect.append(new Option(data.name, data.id, false, true));
                });
                $quSelect.val('').trigger('change');
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
    refreshTable();
}

function refreshTable() {
    $table.bootstrapTable('removeAll');
    $table.bootstrapTable('refresh');
}

//配置 table 查询参数
function queryParams(params) {
    params.sheng=$shengSelect.val();
    params.shi=$shiSelect.val();
    params.qu=$quSelect.val();
    params.state=$stateSelect.val();
    return params;
}

//列表中添加编辑连接
function actionFormatter(value,row,index) {
    if(row.state=='0'){
        return [
            '<a class="edit" href="javascript:" title="编辑鱼商信息"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="view" href="javascript:" title="查看鱼商信息"><i class="glyphicon glyphicon-eye-open"></i></a>',
            '<a class="stop" href="javascript:" title="停用"><i class="glyphicon glyphicon-ban-circle"></i></a>'
        ].join('&nbsp;&nbsp;&nbsp;');
    }else if(row.state=='1'){
        return [
            '<a class="edit" href="javascript:" title="编辑鱼商信息"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="view" href="javascript:" title="查看鱼商信息"><i class="glyphicon glyphicon-eye-open"></i></a>',
            '<a class="reStart" href="javascript:" title="启用"><i class="glyphicon glyphicon-refresh"></i></a>'
        ].join('&nbsp;&nbsp;&nbsp;');
    }


}
//设置权限按钮click事件响应处理
window.actionEvents = {

    'click .edit': function (e, value, row) {
        $uId=row.id;
        showModal("修改鱼商信息",row);
    },
    'click .view': function (e, value, row) {
        $uId=row.id;

        var index = layer.open({
            type: 2,
            content: $("#basePath").attr("value") + '/adminSupplier/view?id='+$uId,
            area: ['320px', '195px'],
            maxmin: true
        });
        layer.full(index);
    },
    'click .stop': function (e, value, row) {
        swalConfirm("停用鱼商?",function() {
            showFakeloader()
            $.post($("#basePath").attr("value") + "/adminSupplier/stop",
                {id: row.id},
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
                }).error(function() {hideFakeloader(); serverErrorAlert()});
        });
    },
    'click .reStart': function (e, value, row) {
        swalConfirm("重新启用鱼商?",function() {
            showFakeloader()
            $.post($("#basePath").attr("value") + "/adminSupplier/reStart",
                {id: row.id},
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
                }).error(function() {hideFakeloader(); serverErrorAlert()});
        });
    },
};






//######################### 表单中的函数  ###############################

//查询市
function f_queryShi() {
    var value=$f_shengSelect.val();
    var txt=$f_shengSelect.find("option:selected").text();
    if(value==''){
        return ;
    }
    showFakeloader();
    $params={shengName:txt,shengId:value}
    $.post($("#basePath").attr("value") + '/c/queryShiList',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $f_shiSelect.empty();
                $f_shiSelect.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $f_shiSelect.append(new Option(data.name, data.id, false, true));
                });
                if($uId!=''){
                    $f_shiSelect.val($review_shiId).trigger('change');
                }else{
                    $f_shiSelect.val('').trigger('change');
                }


            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}
//查询区
function f_queryQu() {
    var value=$f_shiSelect.val();
    var txt=$f_shiSelect.find("option:selected").text();
    if(value==''){
        return ;
    }
    showFakeloader();
    $params={shiName:txt,shiId:value}
    $.post($("#basePath").attr("value") + '/c/queryQuList',
        $params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $f_quSelect.empty();
                $f_quSelect.append(new Option('请选择','', false, true));
                $.each(data,function (index,data) {
                    $f_quSelect.append(new Option(data.name, data.id, false, true));
                });
                if($uId!=''){
                    $f_quSelect.val($review_quId).trigger('change');
                }else{
                    $f_quSelect.val('').trigger('change');
                }

            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function showModal(title, row) {
    if($editor){
        $editor.destroy();
        $editor=initEditor("text");
    }else{
        $editor=initEditor("text");
    }
    clearForm();
    $modal.find('.modal-title').text(title);
    if ($uId) {
        updateInit();
    } else {
        addInit();
    }
    $modal.modal('show');
}

function clearForm() {
    $form.clearForm();
    $editor.$txt.html("");
    $f_shiSelect.empty();
    $f_quSelect.empty();
    $f_quSelect.append(new Option('请选择','', false, true));
    $f_shiSelect.append(new Option('请选择','', false, true));
    $f_shengSelect.val('').trigger('change');
    $f_shiSelect.val('').trigger('change');
    $f_quSelect.val('').trigger('change');
    $type.val('').trigger('change');
    $compensate.val('').trigger('change');
    $auth.val('00').trigger('change');
    $isTop.val('01').trigger('change');
    $orderSupport.val('01').trigger('change');
}

function addInit() {
    //$fileInputConfig.initialPreview=[];
    //$fileInputConfig.initialPreviewConfig=[];
    //$("#thumbnail").fileinput('destroy');
    $("#thumbnail").fileinput($fileInputConfig);//初始化缩略图上传组件
    $qrCode.fileinput($fileInputConfig_qr);
    $orderType.val([]).trigger('change');
    $catalog.val([]).trigger('change');

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
            'supplier.name': {
                validators: {
                    notEmpty: {
                        message: '鱼商名必填'
                    }
                }
            }
        }
    });
}
function updateInit() {
    showFakeloader();
    $("#id").val($uId);
    $.post($("#basePath").attr("value") + "/adminSupplier/get",
        {id:$uId},
        function (data, status) {
            hideFakeloader();
            setFormData(data);

            var pics=data.pics;
            var images = new Array();
            var cfgs=new Array();
            var t=0;
            var bak="";
            for(var i=0;i<pics.length; i++) {
                images.push($qn_url+pics[i].pic);
                cfgs.push({caption: '当前图片',size: 0, width: "120px", key: 1});
                if(t==0)
                    bak=pics[i].pic;
                else
                    bak=bak+"|"+pics[i].pic;
            }

                if(images.length>0){
                    var config=$fileInputConfig;
                    var imgUrl=images;
                    var initialPreviewConfig=cfgs;
                    config['initialPreview']=imgUrl;
                    config['initialPreviewConfig']=initialPreviewConfig;
                    $("#thumbnail_bak").val(bak);

                    $("#thumbnail").fileinput('refresh',config).on('fileclear', function(event) {
                        $("#thumbnail_bak").val('');
                    });
                }else{
                    $("#thumbnail_bak").val('');
                    $fileInputConfig.initialPreview=[];
                    $fileInputConfig.initialPreviewConfig=[];
                    $("#thumbnail").fileinput('destroy')
                    $("#thumbnail").fileinput($fileInputConfig);
                }
                var qrCodePic=data.QRCodePic;
                if(qrCodePic){
                    var config_qr=$fileInputConfig_qr;
                    var imgUrl_qr=[$qn_url+qrCodePic];
                    var initialPreviewConfig_qr=[{caption: '当前图片',size: 0, width: "120px", key: 1}];
                    config_qr['initialPreview']=imgUrl_qr;
                    config_qr['initialPreviewConfig']=initialPreviewConfig_qr;
                    $("#qrCode_bak").val(qrCodePic);

                    $("#qrCode").fileinput('refresh',config_qr).on('fileclear', function(event) {
                        $("#qrCode_bak").val('');
                    });
                }else{
                    $("#qrCode_bak").val('');
                    $fileInputConfig_qr.initialPreview=[];
                    $fileInputConfig_qr.initialPreviewConfig=[];
                    $qrCode.fileinput('destroy')
                    $qrCode.fileinput($fileInputConfig_qr);
                }

        }).error(function() { serverErrorAlert()});
}

function setFormData(obj) {
    if(obj.hasOwnProperty("name")){
        $("#name").val(obj.name);
    }
    if(obj.hasOwnProperty("tel")){
        $("#tel").val(obj.tel);
    }
    if(obj.hasOwnProperty("address")){
        $("#address").val(obj.address);
    }
    if(obj.hasOwnProperty("shi")){
        $review_shiId=obj.shi;
        // $f_shiSelect.val(obj.shi).trigger('change');
    }
    if(obj.hasOwnProperty("qu")){
        $review_quId=obj.qu;
        // $f_quSelect.val(obj.qu).trigger('change');
    }
    if(obj.hasOwnProperty("sheng")){
        $f_shengSelect.val(obj.sheng).trigger('change');
    }



    if(obj.hasOwnProperty("type")){
        $type.val(obj.type).trigger('change');
    }
    if(obj.hasOwnProperty("compensate")){
        $compensate.val(obj.compensate).trigger('change');
    }
    if(obj.hasOwnProperty("auth")){
        $auth.val(obj.auth).trigger('change');
    }
    if(obj.hasOwnProperty("lng")){
        $("#lng").val(obj.lng);
    }
    if(obj.hasOwnProperty("lat")){
        $("#lat").val(obj.lat);
    }
    if(obj.hasOwnProperty("weChat")){
        $("#weChat").val(obj.weChat);
    }
    if(obj.hasOwnProperty("isTop")){
        if(obj.isTop!=0)
            $isTop.val('00').trigger('change');
        else
            $isTop.val('01').trigger('change');
    }
    if(obj.hasOwnProperty("order_support")){
        $orderSupport.val(obj.order_support).trigger('change');
    }
    if(obj.hasOwnProperty("summary")){
        $("#summary").val(obj.summary);
    }
    if(obj.hasOwnProperty("text")){
        $editor.$txt.html(obj.text);
    }
    if(obj.hasOwnProperty("catalogs")){

        var catalogIds=[];
        $.each(obj.catalogs,function (index,data) {
            catalogIds[index]=data.taxonomyId;
        })
        $catalog.val(catalogIds).trigger('change');
    }

    if(obj.hasOwnProperty("ordertypes")){
        var otIds=[];
        $.each(obj.ordertypes,function (index,data) {
            otIds[index]=data.taxonomyId;
        })
        $orderType.val(otIds).trigger('change');
    }

}

function orderSupportChange() {
    var v=$orderSupport.val();
    if(v=='00'){
        $("#orderTypeDiv").css('display','block');
    }else{
        $("#orderTypeDiv").css('display','none');
    }
}
