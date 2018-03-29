/**
 * Created by yuhaihui8913 on 2017/1/18.
 */
var $table = $('#table'),
    $zhFilter= $("#zhFilter_ck"),
    $zhFilterVal='',
    $ownerSelect=$("#owner"),
    $modal = $('#form-modal'),
    $form=$("#modal-form");

$(document).ready(function () {
    $form.submit(function(ev){ev.preventDefault();});
    formValidator()
    $table.bootstrapTable({});

    $zhFilter.on('ifChecked', function (event) {
        $zhFilterVal='on';
        tableRefresh();
    });
    $zhFilter.on('ifUnchecked', function (event) {
        $zhFilterVal='';
        tableRefresh();
    });

    $ownerSelect.select2({
        language: "zh-CN",
        placeholder:'请选择',
        width: "100%"
    });

    $(".create").on("click",function () {
        $("#area.id").val('');

        showModal("新增地域");
    });

    $modal.on('hidden.bs.modal',function(){
        $form.data('bootstrapValidator').resetForm(true);
        $form.data('bootstrapValidator').destroy();
        $form.data('bootstrapValidator', null);
        formValidator();
    });

    $("#btn").on("click", function(){
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if(bootstrapValidator.isValid()){
            var action='/adminArea/save';

            if($('#id').val()!=''){
                action='/adminArea/update';
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

function tableRefresh() {
    if($zhFilterVal!=''){
        $table.bootstrapTable('refresh', {query: {zhFilter: ''}});
    }else{
        $table.bootstrapTable('refresh');
    }
}

//列表中添加编辑连接
function actionFormatter(value) {
    return [
        '<a class="edit" href="javascript:" title="编辑"><i class="glyphicon glyphicon-edit"></i></a>',
    ].join('&nbsp;&nbsp;&nbsp;');
}
//设置权限按钮click事件响应处理
window.actionEvents = {

    'click .edit': function (e, value, row) {

       showModal("修改地域信息",row);
    }
};

function formValidator(){
    $form.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            'area.enName': {
                validators: {
                    notEmpty: {
                        message: '地域英文名必填'
                    }
                    // ,
                    // regexp:{
                    //     regexp:/^[a-zA-Z0-9_]{2,100}$/,
                    //     message:'地域英文名字母数字或下划线构成,长度在2-100之间'
                    // }
                }
            },
            'area.owner': {
                validators: {
                    notEmpty: {
                        message: '所属湖必填'
                    }
                }
            }
        }
    });
}

function showModal(title, row) {
    row = row || {
            id: '',
            enName: '',
            zhName: '',
            state: '',
            owner: ''
        };
    $modal.find('.modal-title').text(title);

    for (var name in row) {
        $modal.find('input[id="' + name + '"]').val(row[name]);
    }
    $("#owner").val(row['owner']).trigger("change");

    $modal.modal('show');
}

