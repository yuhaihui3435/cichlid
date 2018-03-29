/**
 * Created by yuhaihui8913 on 2017/1/22.
 */
var $table = $('#table'),
    $modal = $('#form-modal'),
    $form=$("#modal-form"),
    $area=$("#area"),
    $p_subGroup=$("#p_subGroup"),
    $subGroup=$("#subGroup"),
    $p_species=$("#p_species"),
    $species=$("#species"),
    $fh=$("#fh"),
    $rt=$("#rt"),
    $mf=$("#mf"),
    $breed=$("#breed"),
    $ha=$("#ha"),
    $ca=$("#ca"),
    $reproduce=$("#reproduce"),
    $rare=$("#rare"),
    $uId='',
    $editor='',
    $params={},
    $btn=$("#btn"),
    $bLen=$("#bLen"),
    $habitat=$("#habitat"),
    $sgId='',
    $speciesId='';
var $qn_url = $("#qn_url").val();
var $picMaxSize = $("#pic_maxSize").val();
var $fileInputConfig = {
    language: 'zh',
    allowedFileExtensions: ['jpg', 'png', 'jpeg', 'gif', 'bmp'],
    showUpload: false, //是否显示上传按钮
    maxFileSize: $picMaxSize,
    showPreview: true,
    initialPreviewShowDelete: false,
    initialPreviewAsData: true,
    overwriteInitial: true,
    showCaption: false,
};
//脚本初始化
$(document).ready(function () {
    $form.submit(function(ev){ev.preventDefault();});
    formValidator();
    $table.bootstrapTable({});
    initSelect();
    initBtn();
    $modal.on('hidden.bs.modal', function() {
        $form.data('bootstrapValidator').destroy();
        $form.data('bootstrapValidator', null);
        formValidator();
    });

});
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
        showModal("修改知识库信息",row);
    },
    'click .view': function (e, value, row) {
        $uId=row.id;
        var index = layer.open({
            type: 2,
            content: $("#basePath").attr("value") + '/adminKnowledgebase/view?id='+$uId,
            area: ['320px', '195px'],
            maxmin: true
        });
        layer.full(index);
    }
};
function showModal(title, row) {
    if($editor){
        $editor.destroy();
        $editor=initEditor("remark");
    }else{
        $editor=initEditor("remark");
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
//打开新增窗体初始化数据
function addInit() {
    $fileInputConfig.initialPreview=[];
    $fileInputConfig.initialPreviewConfig=[];
    $("#thumbnail").fileinput('destroy');
    $("#thumbnail").fileinput($fileInputConfig);//初始化缩略图上传组件

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
            'knowledgebase.scName': {
                validators: {
                    notEmpty: {
                        message: '学名必填'
                    }
                }
            }
        }
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
                if($uId!=''){
                    $subGroup.val($sgId).trigger('change');
                }else{
                    $subGroup.val('').trigger('change');
                }

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
                if($uId!=''){
                    $species.val($speciesId).trigger('change');
                }else{
                    $species.val('').trigger('change');
                }
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader(); serverErrorAlert()});
}

function initSelect() {
    $habitat.select2({
        language: "zh-CN",
        width: "100%",
        multiple: true,
        placeholder:'请选择',
        tag:true,
        templateSelection: template
    });
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
        width: "100%"
    });
    $species.select2({
        language: "zh-CN",
        width: "100%"
    });
    $fh.select2({
        language: "zh-CN",
        width: "100%"
    });
    $rt.select2({
        language: "zh-CN",
        width: "100%"
    });
    $mf.select2({
        language: "zh-CN",
        width: "100%"
    });
    $breed.select2({
        language: "zh-CN",
        width: "100%"
    });
    $ha.select2({
        language: "zh-CN",
        width: "100%"
    });
    $ca.select2({
        language: "zh-CN",
        width: "100%"
    });
    $reproduce.select2({
        language: "zh-CN",
        width: "100%"
    });
    $rare.select2({
        language: "zh-CN",
        width: "100%"
    });
    $bLen = $('#bLen').bootstrapSlider({step: 1, min: 3, max: 50, tooltip: 'always'});
}

function initBtn() {
    $btn.on("click", function () {
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            showFakeloader();
            var f = $form.ajaxSubmit({
                type: 'post',
                url: $("#basePath").attr("value") + "/adminKnowledgebase/saveOrUpdate",
                success: function (data) {
                    hideFakeloader();
                    if (data.resCode == 'success') {
                        swal('', data.resMsg, 'success');
                        // if(!$uId){
                        //     removeCacheData();
                        // }
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
    $('.create').click(function () {
        $uId='';
        $('#id').val('');
        showModal($(this).text());
        $habitat.val("").trigger("change");
    });
    $('.delete').click(function () {
        var selections = $table.bootstrapTable('getSelections');
        if (selections.length == 0) {
            swal('', '请选择要删除的知识库数据', 'error');
        } else {
            swalConfirm("删除知识库数据。",function() {
                var ids = '';
                for (var i = 0; i < selections.length; i++) {
                    if (ids == '') {
                        ids = selections[i].id + '';
                    } else {
                        ids = ids + "," + selections[i].id;
                    }
                }
                showFakeloader()
                $.post($("#basePath").attr("value") + "/adminKnowledgebase/del",
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

function setFormData(obj) {
    if (obj.hasOwnProperty("enName"))
        $("#enName").val(obj.enName);
    if (obj.hasOwnProperty("zhName"))
        $("#zhName").val(obj.zhName);
    if (obj.hasOwnProperty("areaId"))
        $area.val(obj.areaId).trigger('change');
    if (obj.hasOwnProperty("bName"))
        $("#bName").val(obj.bName);
    if (obj.hasOwnProperty("scName"))
        $("#scName").val(obj.scName);
    if (obj.hasOwnProperty("remark"))
        $editor.$txt.html(obj.remark);
    if (obj.hasOwnProperty("subGroupPId"))
        $("#p_subGroup").val(obj.subGroupPId).trigger('change');
    if (obj.hasOwnProperty("speciesPId"))
        $("#p_species").val(obj.speciesPId).trigger('change');
    if (obj.hasOwnProperty("mf"))
        $("#mf").val(obj.mf).trigger('change');
    if (obj.hasOwnProperty("ha"))
        $("#ha").val(obj.ha).trigger('change');
    if (obj.hasOwnProperty("rare"))
        $("#rare").val(obj.rare).trigger('change');
    if(obj.hasOwnProperty("rt"))
        $("#rt").val(obj.rt).trigger('change');
    if(obj.hasOwnProperty("reproduce"))
        $("#reproduce").val(obj.reproduce).trigger('change');
    if(obj.hasOwnProperty("fh"))
        $("#fh").val(obj.fh).trigger('change');
    if(obj.hasOwnProperty("ca"))
        $("#ca").val(obj.ca).trigger('change');
    if(obj.hasOwnProperty("breed"))
        $("#breed").val(obj.breed).trigger('change');
    if(obj.hasOwnProperty("bLen")){
        var v=obj.bLen;
        var array=v.split("-");
        array[0]=parseInt(array[0]);
        array[1]=parseInt(array[1]);

        $bLen.bootstrapSlider('setValue',array);
    }
    if(obj.hasOwnProperty("habitats")){
        var habitats=obj.habitats;
        var ownHabitas=[];
        $.each(habitats,function (index,data) {
            ownHabitas[index]=data.pId;
        })
        $("#habitat").val(ownHabitas).trigger('change');
    }

}

function updateInit() {
    showFakeloader();
    $("#id").val($uId);
    $.post($("#basePath").attr("value") + "/adminKnowledgebase/get",
        {id:$uId},
        function (data, status) {
            hideFakeloader();
            $sgId=data.sgId;
            $speciesId=data.speciesId;
            setFormData(data);

            if(data.thumbnail){
                var config=$fileInputConfig;
                var imgUrl=[$qn_url+data.thumbnail];
                var initialPreviewConfig=[{caption: '当前图片',size: 0, width: "120px", key: 1}]
                config['initialPreview']=imgUrl;
                config['initialPreviewConfig']=initialPreviewConfig;
                $("#thumbnail_bak").val(data.thumbnail);

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
        }).error(function() { serverErrorAlert()});
}

function clearForm() {
    $form.clearForm();
    $editor.$txt.html("");
    $("#p_subGroup").val("").trigger('change');
        $("#p_species").val("").trigger('change');
        $("#mf").val("").trigger('change');
        $("#ha").val("").trigger('change');
        $("#rare").val("").trigger('change');
        $("#rt").val("").trigger('change');
        $("#reproduce").val("").trigger('change');
        $("#fh").val("").trigger('change');
        $("#ca").val("").trigger('change');
        $("#breed").val("").trigger('change');
        $bLen.bootstrapSlider('setValue',[5,20]);
        $("#habitat").val("").trigger('change');
        $area.val("").trigger('change');

}


