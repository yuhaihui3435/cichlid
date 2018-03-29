/**
 * Created by yuhaihui8913 on 2017/7/5.
 */
var $table = $('#table');

$(document).ready(function () {
    $table.bootstrapTable({});
    $(".create").on("click",function () {
        showFakeloader();
        $.post($("#basePath").attr("value") + "/adminInvitecode/gen",
            '',
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
});