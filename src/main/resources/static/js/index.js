var ws;
var wsUrl = "ws://127.0.0.1:8888/";
createWebSocket();
var heartclock;
var ary = new Array();
var isFirst = true;
var recInfoloop;
function createWebSocket(){
	try{
		ws = new WebSocket(wsUrl);
		websocketinit();
	}catch(e){
		console.log("d")
		dorelogin()
	}

}

function websocketinit() {
//	ws.binaryType = "arraybuffer";
	ws.onopen = function() {
	// Web Socket 已连接上，使用 send() 方法发送数据
/*	//第一次登入
	var ary = websocketlogin();
	ws.send(ary.buffer);*/
    pingfunc();

};
ws.onerror = function(err) {
	console.log("异常" + err);
}
ws.onclose = function() {
	dorelogin();

}
ws.onmessage = function(event) {
     appendRecviceInfo(event.data);
	if(typeof event.data === String) {
	    console.log("string msg")

    }
    if(event.data instanceof ArrayBuffer){
        var buffer = event.data;
        console.log("Received arraybuffer");
      }
      if(event.data instanceof Blob){
          var buffer = event.data;
          console.log("Received arraybuffer");
        }
}
}

//连接关闭后重连
function dorelogin() {
	clearInterval(heartclock);
	clearInterval(recInfoloop);

	console.log("重连")

	setTimeout(function(){
		createWebSocket();
	},4000);
}
function pingfunc() {
heartclock =window.setInterval(function(){ //每隔50秒钟发送一次心跳，避免websocket连接因超时而自动断开
var ping = {"type":"ping"};
ws.send(JSON.stringify(ping));
},1000*50);
}

function devicedebuging(value) {
    var msg = $("#command").val();
    var socketStr = $("#socketStr").text();
    var endmsg = {"type":"client","msg":msg,"socketAddress":socketStr}

    if(value==="start"){
        startClock(JSON.stringify(endmsg),msg,socketStr)

    }else if(value==="stop"){
        stopClock();
    }else{
        ws.send(JSON.stringify(endmsg))
        appendSendInfo(msg,socketStr)
    }
}
function startClock(endmsg,msg,socketStr) {
    if(sessionStorage.getItem($("#socketStr").text())!=null){
        stopClock();
    }
    var msgclock= self.setInterval(function() {
        ws.send(endmsg);
        appendSendInfo(msg,socketStr);
     }, 3 * 1000);
     sessionStorage.setItem($("#socketStr").text(),msgclock);
}
function stopClock() {
    clearInterval(sessionStorage.getItem($("#socketStr").text()));
}
function clearlog() {
    $("#feedinfo").html("");
}
function appendSendInfo(msg,socketStr) {
    if($("#socketStr").text()===socketStr){
        var curTime = new Date().toLocaleString();
        $("#feedinfo").append("<div style='width:100%;margin-top: 5px;font-size:10px;'><span style='float:left;'>&nbsp;<label>" + curTime + "&nbsp;&nbsp;" + $('#socketStr').text() + "</label>&nbsp;发送<label style='color: #5FB878;'></label>&nbsp;数据：<label style='color: #FF5722;'>" + msg + "</label></span></div><br>");
	    $("#feedinfo").scrollTop($("#feedinfo")[0].scrollHeight);
    }
}
function appendRecviceInfo(value) {
     var res =JSON.parse(value);
     if($("#socketStr").text()===res.socketAddress){
        var msg = res.msg;
     //   var curTime = new Date().toLocaleString();
        var curTime = new Date(Number(res.actionTime)).toLocaleString();
        $("#feedinfo").append("<div style='width:100%;margin-top: 5px;font-size:10px;'><span style='float:left;'>&nbsp;<label>" + curTime + "&nbsp;&nbsp;" + $('#socketStr').text() + "</label>&nbsp;接收<label style='color: #5FB878;'></label>&nbsp;数据：<label style='color: #FF5722;'>" + msg + "</label></span></div><br>");
        $("#feedinfo").scrollTop($("#feedinfo")[0].scrollHeight);
     }
}

function detail(value) {
    clearlog();
    $("#socketStr").text(value);
    querySendAndRecInfo()
    if(isFirst){
    recInfoloop =   window.setInterval(querySendAndRecInfo,5000);
        isFirst = false;
    }

}

function querySendAndRecInfo() {
    $.ajax({
                url:"queryModelSendData",
                method:"post",
                data:{"socketstr":$("#socketStr").text()},
                dataType:"json",
                success:function(res){
                    if(res.errorCode==="SUCCESS"){
                        var result = res.result;
                        updateDataInfo(result.sendNum,result.sendData,result.recNum,result.recData)
                    }else{
                        layer.alert(res.msg);
                    }
                },
                error:function(res){
                    layer.alert(res.status);
                }
            })
}
function updateDataInfo(sendNum,sendData,recNum,recData) {
    $("#sendNum").text(sendNum)
    $("#sendData").text(sendData)
    $("#recNum").text(recNum)
    $("#recData").text(recData)
    var res = recData <=0 ? "0%" :(Math.round(recData/sendData * 10000) / 100.00 + "%");
    $("#precentData").text(res)

}
function clearCache(value) {
    $.ajax({
           url:"clearCache",
           method:"post",
           data:{"type":value},
           dataType:"json",
           success:function(res){
               if(res.errorCode==="SUCCESS"){
                    layer.alert(res.msg)
               }else{
                    layer.alert(res.msg);
               }
           },
           error:function(res){
               layer.alert(res.status);
           }
    })
}
function stopAllClock () {
    $.ajax({
               url:"clearCache",
               method:"post",
               data:{"type":value},
               dataType:"json",
               success:function(res){
                   if(res.errorCode==="SUCCESS"){
                        layer.alert(res.msg)
                   }else{
                        layer.alert(res.msg);
                   }
               },
               error:function(res){
                   layer.alert(res.status);
               }
        })
}