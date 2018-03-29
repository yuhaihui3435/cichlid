/**
 * Created by yuhaihui8913 on 2017/5/26.
 */
var $shengSelect = $('#shengSelect'),
    $shiSelect=$('#shiSelect'),
    $quSelect=$('#quSelect')
    ;


$(document).ready(function () {
    initSelect();
    // initEvent();
});

function initSelect() {
    $shengSelect.select2({
        language: "zh-CN",

    });
    $shiSelect.select2({
        language: "zh-CN",

    });
    $quSelect.select2({
        language: "zh-CN",
    });

    if(sheng!=null&&sheng!=''){
        $shengSelect.val(sheng).trigger('change');
    }

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
                if(shi==null&&shi=='')
                    $shiSelect.val('').trigger('change');
                else
                    $shiSelect.val(shi).trigger('change');
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});

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
                if(qu==null&&qu=='')
                    $quSelect.val('').trigger('change');
                else
                    $quSelect.val(qu).trigger('change');
            } else {
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function changePage(pageNumber) {
    $("#offset").val(pageNumber);
    $("#searchForm").submit();
}