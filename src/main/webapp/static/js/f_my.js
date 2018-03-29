/**
 * Created by yuhaihui8913 on 2017/8/22.
 */


;(function () {
    var href=$(".active").children().attr("href");
    if(href=='#myReply'){
        loadMyComments(0);
    }else if(href=='#myPletter'){
        loadMyPletter(0);
    }
    else if(href=='#atMe'){
        loadAtMe(0);
    }
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var obj=e.target;
        var preObj=e.relatedTarget;
        var target=$(obj).attr("href");
        var preTarget=$(preObj).attr("href");
        $(preTarget).empty();
        if(target=='#atMe'){
            loadAtMe(0);
        }else if(target=='#myReply'){
            loadMyComments(0);
        }else if(target=="#myPletter"){
            loadMyPletter(0);
        }else if(target=="#myCollect"){
            loadMyCollect(0);
        }else if(target=='#myFollow'){
            loadMyFollow();
        }

    })

})();


function loadMyComments(offset) {
    var qn_url=$("#qn_url").val();
    var $mc=$("#myReply");
    var seeUId=$("#seeUId").val();
    showFakeloader();
    $.post($("#basePath").attr("value") + '/myCommnets',
        {'offset':offset,seeUId:seeUId},
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $mc.empty();
                var html="<div class=\"box-comments\">";
                $.each(data.list,function (i,d) {

                    html+="<div class=\"box-comment\">";
                    html+="<img class=\"img-circle img-md\" src=\""+d.user_avatar+"\">";
                    html+="<div class=\"comment-text\"><span class=\"username\"><a href='"+$("#basePath").attr("value")+"/my?seeUId="+d.userId+"'>"+d.user_nickname+"</a><span class=\"text-muted pull-right\">"+d.createdTxt+"</span></span>"+d.text+" </div>"
                    html+="<div class=\"attachment-block clearfix\">";
                    html+="<img class=\"attachment-img\"  src=\""+qn_url+d.content_thumbnail+"\">";
                    html+="<div class=\"attachment-pushed\"><h4 class=\"attachment-heading\">";
                    html+="<a target=\"_blank\" href=\""+getReviewUrl(d)+"\">"+d.content_title+"</a></h4>";
                    html+="<div class=\"attachment-text\">"+d.content_summary+"</div></div></div>"
                    html+="</div>";

                });
                html+="</div>";
                html+="<div class=\"M-box\"></div>";

                $mc.append(html);


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
                        loadMyComments(offset);
                    }
                });

            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function loadAtMe(offset) {
    var qn_url=$("#qn_url").val();
    var $container=$("#atMe");
    showFakeloader();
    $.post($("#basePath").attr("value") + '/myAt',
        {'offset':offset},
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $container.empty();
                var html="<div class=\"box-comments\">";
                $.each(data.list,function (i,d) {

                    html+="<div class=\"box-comment\">";
                    html+="<img class=\"img-circle img-md\" src=\""+d.fromUser_avatar+"\">";
                    html+="<div class=\"comment-text\"><span class=\"username\"><a href='"+$("#basePath").attr("value")+"/my?seeUId="+d.fromUserId+"'>"+d.fromUser_nickname+"</a><span class=\"text-muted pull-right\">"+d.cAtTxt+"</span></span>"+d.text+" </div>"

                    html+="<div class=\"attachment-block clearfix\">";

                    html+="<img class=\"attachment-img\"  src=\""+qn_url+d.content_thumbnail+"\">";

                    html+="<div class=\"attachment-pushed\"><h4 class=\"attachment-heading\">";
                    html+="<a target=\"_blank\" href=\""+getReviewUrl(d)+"\">"+d.content_title+"</a></h4>";
                    html+="<div class=\"attachment-text\">"+d.content_summary+"</div></div></div>"
                    html+="</div>";

                });
                html+="</div>";
                html+="<div class=\"M-box\"></div>";

                $container.append(html);


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
                        loadAtMe(offset);
                    }
                });

            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function loadMyPletter(offset) {
    var $container=$("#myPletter");
    var nickname=$("#nickname").val();
    showFakeloader();
    $.post($("#basePath").attr("value") + '/mySx',
        {'offset':offset},
        function (data, status) {
            hideFakeloader();
            var targetUser='';//私信目标用户
            var targetUserId='';
            if (status == 'success') {
                $container.empty();
                var html="<div class=\"box-comments\">";
                $.each(data.list,function (i,d) {

                    html+="<div class=\"box-comment\">";

                    html+="<img class=\"img-circle img-md\" src=\""+d.fromUser.avatar+"\">";
                    if(nickname==d.fromUser.nickname){
                        html+="<div class=\"comment-text\" id='plettertxt-"+d.id+"'><span class=\"username\"><a href='"+$("#basePath").attr("value")+"/my?seeUId="+d.toUser.id+"'>"+d.toUser.nickname+"</a></span>"+d.lastTxt+" </div>";
                        targetUser=d.toUser.nickname;
                        targetUserId=d.toUser.id;
                    }else{
                        html+="<div class=\"comment-text\" id='plettertxt-"+d.id+"'><span class=\"username\"><a href='"+$("#basePath").attr("value")+"/my?seeUId="+d.fromUser.id+"'>"+d.fromUser.nickname+"</a></span>"+d.lastTxt+" </div>";
                        targetUser=d.fromUser.nickname;
                        targetUserId=d.fromUser.id;
                    }
                    html+="<div class=\"comment-text-footer\"><span class=\"text-muted pull-right\"><a href=\"javascript:delPriLetter("+d.id+")\">删除</a>&nbsp;&nbsp;&nbsp;<a href=\"javascript:viewRecords("+d.id+","+d.priBetterRecordCount+",'"+targetUser+"',"+targetUserId+")\" id='viewRecords-"+d.id+"'>查看私信("+d.priBetterRecordCount+")</a></span><span class=\"text-muted\">"+d.lastTime+"</span></div>"

                    html+="<div class=\"box box-primary direct-chat direct-chat-primary\" style='display: none;' id=\"box-"+d.id+"\" ><div class=\"box-header with-border\"><h3 class=\"box-title\"></h3><div class=\"box-tools pull-right\"><span data-toggle=\"tooltip\"  class=\"badge bg-light-blue\">"+d.priBetterRecordCount+"</span><button type=\"button\" class=\"btn btn-box-tool\" data-widget=\"remove\"><i class=\"fa fa-times\"></i></button></div></div><div class=\"box-body\"><div class=\"direct-chat-messages\"></div> </div><div class=\"box-footer\"><form><div class=\"input-group\"><input type=\"text\" id=\"message\" placeholder=\"请输入 ...\" class=\"form-control\"><span class=\"input-group-btn\"> <button type=\"button\" class=\"btn btn-primary btn-flat\">发送</button></span></div></form></div> </div>"

                    html+="</div>";

                });
                html+="</div>";
                html+="<div class=\"M-box\"></div>";

                $container.append(html);


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
                        loadMyPletter(offset);
                    }
                });

            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function getReviewUrl(d) {
    var contentModule=d.contentModule;
    if(contentModule=='article'){
        return $("#basePath").attr("value")+"/art/view?id="+d.contentId;
    }else if(contentModule=='knowledge'){
        return $("#basePath").attr("value")+"/view?id="+d.contentId;
    }else if(contentModule=='supplierView'){
        return $("#basePath").attr("value")+"/supplier/view?id="+d.contentId;
    }
}

function viewRecords(id,msgCount,targetUser,targetUserId) {
    let html="<div class=\"box box-primary direct-chat direct-chat-primary\" style='margin-bottom: 0px'  id=\"box-"+id+"\" ><div class=\"box-header with-border\"><h3 class=\"box-title\">与"+targetUser+"私信记录</h3></h3><div class=\"box-tools pull-right\"><span data-toggle=\"tooltip\"  class=\"badge bg-light-blue\" id='msgCount-"+id+"'>"+msgCount+"</span></div></div><div class=\"box-body\"><div class=\"direct-chat-messages\" id='messages-"+id+"'></div> </div><div class=\"box-footer\"><form><div class=\"input-group\"><input type=\"text\" id=\"sx_msg\" placeholder=\"请输入 ...\" class=\"form-control\"><span class=\"input-group-btn\"> <button type=\"button\" class=\"btn btn-primary btn-flat\" onclick=\"sendSx("+id+","+targetUserId+",'"+targetUser+"')\">发送</button></span></div></form></div> </div>";
    layer.open({
        type: 1,
        title: false,
        closeBtn: 0,
        // area: ['380px', '90%'],
        shadeClose: true,
        content: html
    });
    if(id!='0')
    loadRecords(id,targetUser);
}



function loadRecords(id,targetUser) {
    showFakeloader();
    let $container=$("#messages-"+id);
    $.post($("#basePath").attr("value") + '/mySxDetail',
        {'plId':id},
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $container.empty();
                var html="";
                $.each(data,function (i,d) {
                    if(d.sender.nickname==targetUser){
                        html+="<div class=\"direct-chat-msg right\"> <div class=\"direct-chat-info clearfix\"> <span class=\"direct-chat-name pull-right\">"+d.sender.nickname+"</span> <span class=\"direct-chat-timestamp pull-left\">"+d.cAtTxt+"</span> </div> <img class=\"direct-chat-img\" src=\""+d.sender.avatar+"\" > <div class=\"direct-chat-text\">"+d.txt+" </div> </div>"
                    }else{
                        html+="<div class=\"direct-chat-msg\"> <div class=\"direct-chat-info clearfix\"> <span class=\"direct-chat-name pull-left\">"+d.sender.nickname+"</span> <span class=\"direct-chat-timestamp pull-right\">"+d.cAtTxt+"</span> </div> <img class=\"direct-chat-img\" src=\""+d.sender.avatar+"\" > <div class=\"direct-chat-text\">"+d.txt+" </div> </div>"
                    }
                });

                $("#msgCount-"+id).text(data.length);
                //修改底层页面的私信数量
                $("#viewRecords-"+id).text('查看私信('+data.length+')');
                //修改底层页面的私信最后一条内容
                var str=$("#plettertxt-"+id).contents();
                    str=str.filter(function (index, content) {
                    return content.nodeType === 3;
                }).get();
                if(str.length>0)
                str[0].nodeValue=data[0].txt;
                $container.append(html);
            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function sendSx(priLetterId,targetUserId,targetUser) {

    var msg=$("#sx_msg").val();
    msg=$.trim(msg);
    if(msg==''){return;}
    showFakeloader();
    $.post($("#basePath").attr("value") + '/sx',
        {'toUserId':targetUserId,"content":msg},
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                $("#sx_msg").val('');
                if(priLetterId!='0')
                    loadRecords(priLetterId,targetUser);
                else
                    loadRecords(data.resData.id,targetUser);
            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function delPriLetter(id) {
    swalConfirm("删除该私信记录吗？",function () {
        delPriLetterDo(id);
    });
}
function delPriLetterDo(id) {
    showFakeloader();
    $.post($("#basePath").attr("value") + '/delSx',
        {'priLetterId':id},
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                loadMyPletter(0);
            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function loadMyCollect(offset) {
    var $container=$("#myCollect");
    showFakeloader();
    $.post($("#basePath").attr("value") + '/myCollect',
        {'offset':offset},
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $container.empty();

                if (!$.isEmptyObject(data)) {

                var html = "<div class=\"box box-primary\">";
                html += "<div class=\"box-header\"></div>";
                html += "<div class=\"box-body\">";
                html += "<ul class=\"todo-list ui-sortable\">";
                $.each(data.list, function (i, d) {
                        var moduleTxt='';
                    if(d.moduleToken=='1'){
                        moduleTxt="<small class=\"label label-info\">"+d.moduleTxt+"</small>";
                    }else if(d.moduleToken=='2'){
                        moduleTxt="<small class=\"label label-warning\">"+d.moduleTxt+"</small>";
                    }else if(d.moduleToken=='3'){
                        moduleTxt="<small class=\"label label-success\">"+d.moduleTxt+"</small>";
                    }

                    html += "<li><a href=\"" + $("#basePath").attr("value") + d.url + "\" target='_blank'><span class=\"text\">" + d.title + "</span>" +
                        moduleTxt+
                        "</a><div class=\"tools\"><i class=\"fa fa-trash-o\" onClick=\"delCollect(" + d.id + ")\"></i></div></li>"


                });
                html += "</ul>";
                html += "</div></div>";
                html += "<div class=\"M-box\"></div>";

                $container.append(html);



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
                        loadMyCollect(offset);
                    }
                });
                }

            } else {
                if(data!='')
                swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function delCollect(id) {
    swalConfirm("删除该收藏记录吗？",function () {
        delCollectDo(id);
    });

}

function delCollectDo(id) {
    showFakeloader();
    $.post($("#basePath").attr("value") + '/cancelCollect',
        {'collectId':id},
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                $("#myCollectsLabel").text(data.resData.collects);
                loadMyCollect(0);
            } else {
                if(data!='')
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function loadMyFollow() {
    var $container=$("#myFollow");
    showFakeloader();
    var seeUId=$("#seeUId").val();
    $.post($("#basePath").attr("value") + '/myFollow',
        {'seeUId':seeUId},
        function (data, status) {
            hideFakeloader();
            if (status == 'success') {
                $container.empty();

                if(!$.isEmptyObject(data)) {
                    var html = "<div class=\"box box-primary\">";
                    html += "<div class=\"box-header\"></div>";
                    html += "<div class=\"box-body no-padding\">";
                    html += "<ul class=\"users-list clearfix\">";
                    $.each(data, function (i, d) {

                        html += "<li><img src=\"" + d.followUser.avatar + "\" width='50px' height='50px'><a class='users-list-name' href=\"" + $("#basePath").attr("value") + "/my?seeUId=" + d.followUser.id + "\" target='_blank'>" + d.followUser.nickname + "</a></li>"
                    });
                    html += "</ul>";
                    html += "</div></div>";
                    html += "<div class=\"M-box\"></div>";

                    $container.append(html);
                }


            } else {
                if(data!='')
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}


function follow(followUserId,obj) {
    var jObj=$(obj);
    showFakeloader();
    $.post($("#basePath").attr("value") + '/follow',
        {'followUserId':followUserId},
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                jObj.hide();
                var nextObj=jObj.next();
                nextObj.show();
            } else {
                if(data!='')
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}

function cancelFollow(followUserId,obj) {
    var jObj=$(obj);
    showFakeloader();
    $.post($("#basePath").attr("value") + '/cancelFollow',
        {'followUserId':followUserId},
        function (data, status) {
            hideFakeloader();
            if (status == 'success'&&data.resCode=='success') {
                jObj.hide();
                var prevObj=jObj.prev();
                prevObj.show();
            } else {
                if(data!='')
                    swal('', data.resMsg, 'error');
            }
        }).error(function() { hideFakeloader();serverErrorAlert()});
}