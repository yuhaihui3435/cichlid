/**
 * Created by yuhaihui8913 on 2017/7/7.
 * 
 * 评论  处理
 * 
 */

// var editor;
$(document).ready(function () {

    // editor=initEditor('replyContent');
    //loadReplyList(0);
});
function replaySubmit(btn) {
    let nickname=$("#currUserNickname").val();
    let contentModule=$("#contentModule").val();
    let contentId=$("#contentId").val();
    let replyContent=$("#replyContent").val();
    let authorId=$("#authorId").val();

    if(replyContent==''){
        layer.msg('请填写评论内容!',{icon:2});
        return ;
    }
    if(!nickname){
        toLogin();
    }else{
        showFakeloader();
        var params={nickname:nickname,contentModule:contentModule,contentId:contentId,replyContent:replyContent,authorId,authorId};
        $.post($("#basePath").attr("value") + '/addReply',
            params,
            function (data, status) {
                hideFakeloader();
                if (status == 'success') {
                    layer.msg(data.resMsg,{icon:0});
                    //location.reload(true);
                    $("#replyContent").val('');
                    loadReplyList(0);
                } else {
                    layer.msg(data.resMsg,{icon:1});
                }
            }).error(function() { hideFakeloader();serverErrorAlert()});
    }

}


function atThis(nickname) {
    let replyContent=$("#replyContent").val();
    nickname='@'+nickname+' ';
    $("#replyContent").val(replyContent+nickname);
}

function loadReplyList(offset) {
    let nickname=$("#currUserNickname").val();
    showFakeloader();
    if(!offset)offset=0;
    let contentModule=$("#contentModule").val();
    let contentId=$("#contentId").val();
    var qn_url=$("#qn_url").val();
    var anonymousAvatar=$("#anonymousAvatar").val();

    var params={contentModule:contentModule,contentId:contentId,offset:offset};
    $.post($("#basePath").attr("value") + '/replyList',
        params,
        function (data, status) {

            hideFakeloader();
            var replay=$("li:last");

            $(".cc-reply-list").empty();
            var html="";
            $.each(data.list,function (index,d) {
                html=html+"<li><div class=\"cc-reply-user-img\">";
                if(d.user_avatar){
                    html=html+"<a href=\"#\" target=\"_blank\"><img src=\""+d.user_avatar+"\"></a>"
                }else{
                    html=html+"<a href=\"#\" target=\"_blank\"><img src=\""+qn_url+anonymousAvatar+"?v="+new Date()+"\"></a>"
                }
                html=html+"</div>";
                html=html+"<div class=\"cc-reply-item\"> <div class=\"cc-reply-user-name\"><a href='"+$("#basePath").attr("value")+"/my?seeUId="+d.userId+"'>"+d.user_nickname+"</a></div><div class=\"cc-reply-time\">"+d.createdTxt+"</div><div class=\"cc-reply-content\">"+d.text;

                html=html+"<div class=\"cc-reply-and-delete\">" ;
                if(nickname==''||nickname!=d.user_nickname){
                    html=html+"<a class=\"cc-reply-link\" href=\"javascript:atThis('"+d.user_nickname+"')\">回复</a> "
                }else if(nickname!=''&&nickname==d.user_nickname){
                    html=html+"<a class=\"cc-reply-link\" href=\"javascript:delThis('"+d.id+"')\">删除</a> "
                }
                    html=html+"</div> </div></div> </li>";
                $(".cc-reply-list").append(html);
                html="";
            });

            $(".cc-reply-list").append(replay);

            $('.M-box').pagination({
                pageCount:data.totalPage,
                totalData:data.totalRow,
                current:data.pageNumber,
                jump:true,
                coping:true,
                homePage:'首页',
                endPage:'末页',
                prevContent:'上页',
                nextContent:'下页',
                showData:15,
                callback:function(api){
                    var offset=(api.getCurrent()-1)*15;
                    loadReplyList(offset);
                }
            });

            
        }).error(function() { hideFakeloader();serverErrorAlert()});
}


function delThis(id) {
    swalConfirm("删除该回复吗？",function () {
        delThisDo(id);
    });

}
function delThisDo(id) {
    showFakeloader();
    var params={id:id};
    $.post($("#basePath").attr("value") + '/delReply',
        params,
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                layer.msg(data.resMsg,{icon:0});
                loadReplyList(0);
            } else {
                layer.msg(data.resMsg,{icon:1});
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}
