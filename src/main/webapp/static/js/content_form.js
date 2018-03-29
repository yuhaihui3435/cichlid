/**
 * Created by yuhaihui on 2017/1/1.
 */
var $form = $("#content");
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
var $id ;

var $publishBtn = $("#publishBtn");
var $previewBtn = $("#previewBtn");
var editor;
var $viewModal = $("#view-modal");
var $currUser = $("#currUser").val();
var $store_key_a = $currUser + "_a_content";
var $store_key_u = $currUser + "_u_content";
var $savingSpan=$("#savingSpan");
var $intervalKey;
var $text,$tagsData,$top,$good,$comment_status,$link_to,$thumbnail;
$(document).ready(function () {

    editor=initEditor('text');
    initForm();
    formValidator();
    initBtnEvt();

    if ($id) {
        updateInit();
    } else {
        addInit();

    }
    if (!store.enabled) {
        browserIsLowAlert();
    }

});



function formValidator() {
    $form.bootstrapValidator({
        message: '该值无效',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        }

    });
}

function addInit() {
    $("#thumbnail").fileinput($fileInputConfig);//初始化缩略图上传组件
    reBindFormData_a();//重新绑定本地缓存的表单数据
    $intervalKey=window.setInterval(cacheData,10000);//设置缓存时间间隔
}

function initBtnEvt() {
    $publishBtn.on("click", function () {
        var c = editor.$txt.text();
        if (c == '') {
            swal("", "请编辑文章内容", "error");
            return false;
        }
        var bootstrapValidator = $form.data('bootstrapValidator');
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            showFakeloader();
            var f = $form.ajaxSubmit({
                type: 'post',
                url: $("#basePath").attr("value") + "/adminContent/saveOrUpdate",
                success: function (data) {
                    hideFakeloader();
                    if (data.resCode == 'success') {
                        swal('', data.resMsg, 'success');
                        if(!$id){
                            removeCacheData();
                        }
                        setTimeout(window.location= $("#basePath").attr("value") +'/adminContent/index',2000);
                    } else {
                        $publishBtn.prop("disabled", false);
                        swal('', data.resMsg, 'error');
                    }
                },
                error: function (XmlHttpRequest, textStatus, errorThrown) {
                    $publishBtn.prop("disabled", false);
                    hideFakeloader();
                    serverErrorAlert();
                }

            });
        }
        else return;
    });

    $previewBtn.on("click", function () {
        $viewModal.find("#content-view").empty();
        $viewModal.find("#content-view").html(editor.$txt.html());
        $viewModal.modal("show");
    });
}

function initForm() {
    $form.submit(function (ev) {
        ev.preventDefault();
    });
    // $tags.select2({
    //     language: "zh-CN",
    //     width: "100%",
    //     multiple: true,
    //     placeholder: '文章标签',
    //     tag: true,
    //     templateSelection: template
    // });


    //icheck组件的初始化
    $('#original_y').on('ifChecked', function (event) {
        $("#link_to").val('');
        $("#link_to").hide();
    });
    $('#original_n').on('ifChecked', function (event) {
        $("#link_to").show();
    });
    // $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
    //     checkboxClass: 'icheckbox_flat-green',
    //     radioClass: 'iradio_flat-green'
    // });



}

// function initEditor() {
//     editor = new wangEditor('text');
//     editor.config.menus = [
//         'source',
//         '|',
//         'bold',
//         'underline',
//         'italic',
//         'strikethrough',
//         'eraser',
//         'forecolor',
//         'bgcolor',
//         '|',
//         'quote',
//         'fontfamily',
//         'fontsize',
//         'head',
//         'unorderlist',
//         'orderlist',
//         'alignleft',
//         'aligncenter',
//         'alignright',
//         '|',
//         'link',
//         'unlink',
//         'table',
//         'emotion',
//         '|',
//         'img',
//         'video',
//         '|',
//         'undo',
//         'redo',
//         'fullscreen'
//     ];
//     editor.config.customUpload = true;  // 设置自定义上传的开关
//     editor.config.customUploadInit = uploadInit;  // 配置自定义上传初始化事件，uploadInit方法在上面定义了
//     editor.create();
// }

function reBindFormData_a() {
    if (!store.enabled) {
        return
    }
    var cacheData = {};
    cacheData = store.get($store_key_a);
    if (!$.isEmptyObject(cacheData)) {
        setFormData(cacheData);
    }

}

function cacheData(){
    $savingSpan.fadeIn(1500)
    var obj=new Object();
    obj.catalog=$("#catalog").val();
    obj.area=$("#area").val();
    var tags=$("#tags").val();
    if(tags!=null){
        obj.tags=tags;
    }
    obj.title=$("#title").val();
    obj.summary=$("#summary").val();
    obj.text=editor.$txt.html();
    obj.link_to=$("#link_to").val();
    obj.original=$("input[name='original']:checked").val();
    obj.comment_status=$("input[name='content.comment_status']:checked").val();
    obj.good=$("input[name='content.good']:checked").val();
    obj.top=$("input[name='content.top']:checked").val();
    store.set($store_key_a,obj);
    $savingSpan.fadeOut(1500)
}

function removeCacheData() {
    store.remove($store_key_a);
    window.clearInterval($intervalKey);
}

function setFormData(obj) {
    if (obj.hasOwnProperty("catalog"))
        $("#catalog").val(obj.catalog);
    if (obj.hasOwnProperty("area"))
        $("#area").val(obj.area);
    if (obj.hasOwnProperty("tags")){
        var tags_array=obj.tags.split(',');
        $.each(tags_array,function (i,n) {
            $tags.tagsinput('add', n);
        })

    }
        //$("#tags").val(obj.tags);
    if(obj.hasOwnProperty("title"))
        $("#title").val(obj.title);
    if(obj.hasOwnProperty("summary"))
        $("#summary").val(obj.summary);
    if(obj.hasOwnProperty("text"))
        editor.$txt.html(obj.text);
    if(obj.hasOwnProperty("link_to"))
        $("#link_to").val(obj.link_to);

    if(obj.hasOwnProperty("original")){
        (obj.original=='0')?$("#original_y").iCheck('check'):$("#original_n").iCheck('check');
    }
    if(obj.hasOwnProperty("comment_status")){
        (obj.comment_status=='0')?$("#comment_status_y").iCheck('check'):$("#comment_status_n").iCheck('check');
    }
    if(obj.hasOwnProperty("good")){
        (obj.good=='0')?$("#good_y").iCheck('check'):$("#good_n").iCheck('check');
    }
    if(obj.hasOwnProperty("top")){
        (obj.top=='0')?$("#top_y").iCheck('check'):$("#top_n").iCheck('check');
    }
}

function updateInit() {
    showFakeloader();
    $.post($("#basePath").attr("value") + "/adminContent/get",
        {id:$id},
        function (data, status) {
            hideFakeloader();
            editor.$txt.html(data.text);
        }).error(function() { serverErrorAlert()});

    var array=$tagsData.split(",");
    $.each(array,function (i,n) {
        $tags.tagsinput('add', n);
    })

    if($link_to){
        $("#original_n").iCheck('check');
        $("#link_to").show();
    }else{
        $("#original_y").iCheck('check');
    }

    ($comment_status=='0')?$("#comment_status_y").iCheck('check'):$("#comment_status_n").iCheck('check');
    ($good=="false")?$("#good_y").iCheck('check'):$("#good_n").iCheck('check');
    ($top=="false")?$("#top_y").iCheck('check'):$("#top_n").iCheck('check');
    if($thumbnail){
        var config=$fileInputConfig;
        var imgUrl=[$qn_url+$thumbnail];
        var initialPreviewConfig=[{caption: '当前图片',size: 0, width: "120px", key: 1}]
        config['initialPreview']=imgUrl;
        config['initialPreviewConfig']=initialPreviewConfig;
        $("#thumbnail_bak").val($thumbnail);

        $("#thumbnail").fileinput('refresh',config).on('fileclear', function(event) {
            $("#thumbnail_bak").val('');
        });
    }else{
        $fileInputConfig.initialPreview=[];
        $fileInputConfig.initialPreviewConfig=[];
        $("#thumbnail").fileinput('destroy')
        $("#thumbnail").fileinput($fileInputConfig);
    }


}