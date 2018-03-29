/**
 * Created by yuhaihui on 2016/12/1.
 */
$(function(){
    moment.locale('zh-cn');
    $.cookie.raw = true;
    resetMenuState();//初始化系统后台菜单
    //icheck组件的初始化
    $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
        checkboxClass: 'icheckbox_flat-green',
        radioClass: 'iradio_flat-green'
    });
    $.ajaxSetup({
        contentType:"application/x-www-form-urlencoded;charset=utf-8",
        complete:function(XMLHttpRequest,textStatus){
            var sessionstatus=XMLHttpRequest.getResponseHeader("sessionstatus"); //通过XMLHttpRequest取得响应头，sessionstatus，
            if(sessionstatus&&sessionstatus.indexOf("timeout")>-1){
                // swal("","请先登录！","error");
                //如果超时就处理 ，指定要跳转的页面
                if(sessionstatus.indexOf("admin")>-1) {
                    setTimeout("location.href=$('#basePath').attr('value')+'/admin'", 1000);
                }
                else{
                    $("#loginBtn").show();
                    $("#settingBtn").hide();
                    toLogin();
                }

            }
        }
    });
});

var fakerObj;
function hideFakeloader() {
    if(fakerObj !=undefined){
        fakerObj.fadeOut();
    }else{
        $(".fakeLoader").fadeOut();
    }

}

function showFakeloader() {
    if(fakerObj ==undefined) {
        fakerObj = $(".fakeLoader").fakeLoader({
            bgColor: "#1abc9c",
            spinner: "spinner7"
        });
    }else{
        fakerObj.fadeIn();
    }
}
function serverErrorAlert() {

    hideFakeloader();
    swal("", "服务器内部错误，请稍后重试。", "error");
}

function swalConfirm(msg,callback) {
    swal({
        title: "您确定要执行该操作吗?",
        text: msg,
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "是的，确定。",
        cancelButtonText: "不，谢谢。",
        closeOnConfirm: true
    }, function () {
        setTimeout(function () {
            callback();
        }, 1000);

    });
}


function printLog(title, info) {
    window.console && console.log(title, info);
}

function browserIsLowAlert() {
    swal('','您的浏览器由于版本太过老旧，请升级较新的浏览器版本','warning');
}

function resetMenuState() {
    var selTopMenuId=$.cookie("topMenu");
    var selSecMenuId=$.cookie("secMenu");
    printLog(selSecMenuId,selTopMenuId);
    if(selTopMenuId!=undefined&&selTopMenuId!=null){
        $('#tm-'+selTopMenuId).attr("class","active");
    }
    if(selSecMenuId!=undefined&&selSecMenuId!=null){
        $('#tm-'+selSecMenuId).attr("class","active");
    }
}

function cacheMenuState(key,val) {
    printLog(key,val);
    $.cookie(key,val,{path:'/'});
}
function template(data, container) {
    return $('<span style="color: #00a157;font-weight: bold">'+data.text+'</span>');
}
// 初始化七牛上传
function uploadInit() {
    // this 即 editor 对象
    var editor = this;
    // 触发选择文件的按钮的id
    var btnId = editor.customUploadBtnId;
    // 触发选择文件的按钮的父容器的id
    var containerId = editor.customUploadContainerId;

    var qiuniu_url = $("#qn_url").val();
    var pic_maxSize = $("#pic_maxSize").val();
    printLog(qiuniu_url);

    // 创建上传对象
    var uploader = Qiniu.uploader({
        runtimes: 'html5,flash,html4',    //上传模式,依次退化
        browse_button: btnId,       //上传选择的点选按钮，**必需**
        uptoken_url: $("#basePath").val() + '/c/uptoken',
        //Ajax请求upToken的Url，**强烈建议设置**（服务端提供）
        // uptoken : '<Your upload token>',
        //若未指定uptoken_url,则必须指定 uptoken ,uptoken由其他程序生成
        //unique_names: true,
        // 默认 false，key为文件名。若开启该选项，SDK会为每个文件自动生成key（文件名）
        // save_key: true,
        // 默认 false。若在服务端生成uptoken的上传策略中指定了 `sava_key`，则开启，SDK在前端将不对key进行任何处理
        domain: qiuniu_url,
        //bucket 域名，下载资源时用到，**必需**
        container: containerId,           //上传区域DOM ID，默认是browser_button的父元素，
        max_file_size: pic_maxSize + 'k',           //最大文件体积限制
        flash_swf_url: '../plugins/plupload/Moxie.swf',  //引入flash,相对路径
        filters: {
            mime_types: [
                //只允许上传图片文件 （注意，extensions中，逗号后面不要加空格）
                {title: "图片文件", extensions: "jpg,gif,png,bmp"}
            ]
        },
        max_retries: 3,                   //上传失败最大重试次数
        dragdrop: true,                   //开启可拖曳上传
        drop_element: 'editor-container',        //拖曳上传区域元素的ID，拖曳文件或文件夹后可触发上传
        chunk_size: '4mb',                //分块上传时，每片的体积
        auto_start: true,                 //选择文件后自动上传，若关闭需要自己绑定事件触发上传
        init: {
            'FilesAdded': function (up, files) {
                plupload.each(files, function (file) {
                    // 文件添加进队列后,处理相关的事情
                    printLog('on FilesAdded');
                });
            },
            'BeforeUpload': function (up, file) {
                // 每个文件上传前,处理相关的事情
                printLog('on BeforeUpload');
            },
            'UploadProgress': function (up, file) {
                // 显示进度条
                editor.showUploadProgress(file.percent);
            },
            'FileUploaded': function (up, file, info) {
                var domain = up.getOption('domain');
                var res = $.parseJSON(info);
                // var sourceLink = domain + res.key+"-clchlid"; //获取上传成功后的文件的Url
                var sourceLink = domain + res.key; //获取上传成功后的文件的Url

                // 插入图片到editor
                editor.command(null, 'insertHtml', '<img src="' + sourceLink + '" style="max-width:100%;"/>')
            },
            'Error': function (up, err, errTip) {
                //上传出错时,处理相关的事情
                if (err.code == -600) {
                    swal('', errTip, 'error');
                } else {
                    swal('', errTip, 'error');
                }

            },
            'UploadComplete': function () {
                //队列文件处理完毕后,处理相关的事情

                // 隐藏进度条
                editor.hideUploadProgress();
            }
            // Key 函数如果有需要自行配置，无特殊需要请注释
            ,
            'Key': function (up, file) {
                // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
                // 该配置必须要在 unique_names: false , save_key: false 时才生效
                var currUser = $("#currUser").val();
                var nowDate = new Date().getTime();
                var key = nowDate+currUser + "-" + file.name;
                // do something with key here
                return key
            }
        }
    });
}

function initEditor(id) {
    var editor = new wangEditor(id);
    var qiuniu_url = $("#qn_url").val();
    editor.config.emotions = {
        // 支持多组表情
        // 第二组，id叫做'weibo'
        'wxm': {
            title: '猥亵萌',  // 组名称
            data: [  // data 还可以直接赋值为一个表情包数组
                // 第一个表情
                {
                    'icon': qiuniu_url+'/image/ejm/25.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[气人]'
                },
                // 第二个表情
                {
                    'icon': qiuniu_url+'/image/ejm/28.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[奸笑]'
                },
                {
                    'icon': qiuniu_url+'/image/ejm/31.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[惊]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/3f.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[帅气]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/74.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[翻眼]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/7a.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[蒙圈]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/7d.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[泪奔]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/7f.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[左顾右盼]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/9d.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[抛媚眼]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/b8.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[你懂得]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/c3.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[流口水]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/d1.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[鼓掌]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/d7.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[困]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/f1.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[可怜]'
                }
                ,
                {
                    'icon': qiuniu_url+'/image/ejm/f9.jpg?imageView2/2/w/60/h/60/interlace/0/q/100',
                    'value': '[流鼻血]'
                }

            ]
        }
        // 下面还可以继续，第三组、第四组、、、
    };
    editor.config.menus = [
        'source',
        '|',
        'bold',
        'underline',
        'italic',
        'strikethrough',
        'eraser',
        'forecolor',
        'bgcolor',
        '|',
        'quote',
        'fontfamily',
        'fontsize',
        'head',
        'unorderlist',
        'orderlist',
        'alignleft',
        'aligncenter',
        'alignright',
        '|',
        'link',
        'unlink',
        'table',
        'emotion',
        '|',
        'img',
        'video',
        'location',
        '|',
        'undo',
        'redo',
        'fullscreen'
    ];
    editor.config.customUpload = true;  // 设置自定义上传的开关
    editor.config.customUploadInit = uploadInit;  // 配置自定义上传初始化事件，uploadInit方法在上面定义了
    editor.create();
    return editor;
}

/**
 *  textarea 不要设置 margin 值，否则 IE 下的 scrollHeight 会包含该值，用外部嵌套div来布局
 * @param ele 必须是 textarea，并且在外部需要将 overflow 设置为 hidden
 * @param minHeight 最小高度值
 */
function autoHeight(ele, minHeight) {
    minHeight = minHeight || 16;
    // ele.style.height = minHeight + "px";
    if (ele.style.height) {
        ele.style.height = (parseInt(ele.style.height) - minHeight ) + "px";
    }
    ele.style.height = ele.scrollHeight + "px";

    // 返回了: 29  30 30，后两个始终比前一个大一个 px，经测试前都就是少了一个px的border-bottom
    // alert(ele.clientHeight + " : " +ele.scrollHeight + " : " + ele.offsetHeight);
    // 或许这个 currHeight 留着有点用
    // ele.currHeight = ele.style.height;
}

// 来自 git.oschina.net 项目首页，只支持自动增高，不支持减高
// textarea 自动调整高度，绑定 onkeyup="textAreaAdjustHeight(this);"
// git.oschina.net 的 issue 回复实现减高功能，但找不到代码
function textareaAdjustHeightOsc(textarea) {
    var adjustedHeight = textarea.clientHeight;
    adjustedHeight = Math.max(textarea.scrollHeight, adjustedHeight);
    if (adjustedHeight > textarea.clientHeight) {
        textarea.style.height = adjustedHeight + 'px';
    }
}
var toLoginId='';
function toLogin(basePath) {
    toLoginId=layer.open({
        type: 1,
        skin: 'layui-layer-lan', //样式类名
        closeBtn: 0, //不显示关闭按钮
        title:'请先登录',
        anim: 2,
        shadeClose: true, //开启遮罩关闭
        content: '<div id="loginPage" style="width: 300px;height: 200px;padding-top: 50px"><div class="text-center"><a href="javascript:openQQLoginWindow(\''+basePath+'\')" class="btn btn-circle btn-qq"><i class="fa fa-qq fa-5x"></i></a> ' +
        '<a href="javascript:openWXLoginWindow(\''+basePath+'\')" class="btn  btn-circle btn-wx "><i class="fa fa-weixin fa-5x"></i></a> ' +
        '</div></div>'
    });
}
var loginWinId='';
function openQQLoginWindow(basePath) {
    layer.close(toLoginId);

    // loginWinId=layer.open({
    //     type: 2,
    //     title: '登录',
    //     shadeClose: true,
    //     shade: 0.8,
    //     area: ['60%', '60%'],
    //     content: basePath+'/qqLogin'
    // });
    var iHeight=400,iWidth=500;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    window.open (basePath+'/qqLogin','QQ登录','height='+iHeight+',width='+iWidth+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');

    // window.location.href=basePath+'/qqLogin';
}

function openWXLoginWindow(basePath) {
    layer.close(toLoginId);
    var iHeight=400,iWidth=500;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    window.open (basePath+'/wxLogin','微信登录','height='+iHeight+',width='+iWidth+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
}

function collect(obj,id,module) {
    $.post($("#basePath").attr("value") + '/collect',
        {'cId':id,'module':module},
        function (data, status) {
            if (status == 'success'&&data.resCode=='success') {

                var i=$(obj).find("i");
                $(obj).attr("class","cc-icon-active");
               i.attr("class","fa fa-star");

                $("#collectCount").text(data.resData.collectCount);
                $(obj).removeAttr('onClick');
                // span.click(function(){
                //     cancelCollect(obj,data.resData.collectId,module)
                // })

                $(obj).off();
                $(obj).on('click',function(){cancelCollect(obj,data.resData.collectId,module)});
            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function cancelCollect(obj,id,module) {
    $.post($("#basePath").attr("value") + '/cancelCollect',
        {'collectId':id,'module':module},
        function (data, status) {
            if (status == 'success'&&data.resCode=='success') {

                var i=$(obj).find("i");
                $(obj).attr("class","cc-icon");
                i.attr("class","fa fa-star-o");
                $("#collectCount").text(data.resData.collectCount);
                $(obj).removeAttr('onClick');
                $(obj).off();
                $(obj).on('click',function(){collect(obj,data.resData.cId,module)});
            } else {
                if(data!="")
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function laud(obj,id,module) {
    $.post($("#basePath").attr("value") + '/laud',
        {'cId':id,'module':module},
        function (data, status) {
            if (status == 'success'&&data.resCode=='success') {

                var i=$(obj).find("i");
                $(obj).attr("class","cc-icon-active");
                i.attr("class","fa fa-thumbs-up");

                $("#laudCount").text(data.resData.laudCount);
                $(obj).removeAttr('onClick');
                // span.click(function(){
                //     cancelCollect(obj,data.resData.collectId,module)
                // })

                $(obj).off();
                $(obj).on('click',function(){cancelLaud(obj,data.resData.laudId,module)});
            } else {
                if(data!='')
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function cancelLaud(obj,id,module) {
    $.post($("#basePath").attr("value") + '/cancelLaud',
        {'laudId':id,'module':module},
        function (data, status) {
            if (status == 'success'&&data.resCode=='success') {

                var i=$(obj).find("i");
                $(obj).attr("class","cc-icon");
                i.attr("class","fa fa-thumbs-o-up");
                $("#laudCount").text(data.resData.laudCount);
                $(obj).removeAttr('onClick');
                $(obj).off();
                $(obj).on('click',function(){laud(obj,data.resData.cId,module)});
            } else {
                if(data!="")
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}