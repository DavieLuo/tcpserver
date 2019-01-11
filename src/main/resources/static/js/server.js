var serverSocket;
var wsUrl = "ws://127.0.0.1:8888/";
createWebSocket();
var heartclock;  //心跳
var ary = new Array();
var isFirst = true;
var recInfoloop; //查询收发率
var resloveFlag = true; //解析数据时 flag
var message="";
function createWebSocket(){
	try{
		serverSocket = new WebSocket(wsUrl);
		websocketinit();
	}catch(e){
		console.log("d")
		dorelogin()
	}

}

function websocketinit() {
//	serverSocket.binaryType = "arraybuffer";
	serverSocket.onopen = function() {
	// Web Socket 已连接上，使用 send() 方法发送数据

    pingfunc();

};
serverSocket.onerror = function(err) {
	console.log("异常" + err);
}
serverSocket.onclose = function() {
	dorelogin();

}
serverSocket.onmessage = function(event) {

    try {
        var data = JSON.parse(event.data);
        if(message!=""){

            parseData(event.data)
        }else{
            message = event.data;
        }
    }catch (e) {
        parseData(event.data)
    }
    if (resloveFlag) {
        appendRecviceInfo(message);
        message="";
    }


	/*if(typeof event.data === String) {
	    console.log("string msg")

    }
    if(event.data instanceof ArrayBuffer){
        var buffer = event.data;
        console.log("Received arraybuffer");
    }
    if(event.data instanceof Blob){
        var buffer = event.data;
        console.log("Received arraybuffer");
    }*/
}
}
function parseData(data) {
     message += data;
    try {
        var value = JSON.parse(message);
        resloveFlag = true;
    }catch (e) {
        resloveFlag = false;
    }
}
//连接关闭后重连
function dorelogin() {
	clearInterval(heartclock);
    clearInterval(recInfoloop);

	console.log("重连")

	setTimeout(function(){
		createWebSocket();
	},5000);
}
function pingfunc() {
heartclock =window.setInterval(function(){ //每隔50秒钟发送一次心跳，避免websocket连接因超时而自动断开
var ping = {"type":"ping"};
serverSocket.send(JSON.stringify(ping));
},1000*50);
}

function devicedebuging(value) {
    var msg = $("#command").val();
    var socketStr = $("#socketStr").text();
    var endmsg = {"type":"server","msg":msg,"socketAddress":socketStr}

    if(value==="start"){
        startClock(JSON.stringify(endmsg),msg,socketStr)

    }else if(value==="stop"){
        stopClock();
    }else{
        serverSocket.send(JSON.stringify(endmsg))
        appendSendInfo(msg,socketStr)
    }
}
function startClock(endmsg,msg,socketStr) {
    if(sessionStorage.getItem($("#socketStr").text())!=null){
        stopClock();
    }
    var msgclock= self.setInterval(function() {
        serverSocket.send(endmsg);
        appendSendInfo(msg,socketStr);
     }, 400);
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
        var curTime = new Date(Number(res.actionTime)).toLocaleString();
        $("#feedinfo").append("<div style='width:100%;margin-top: 5px;font-size:10px;'><span style='float:left;'>&nbsp;<label>" + curTime + "&nbsp;&nbsp;" + $('#socketStr').text() + "</label>&nbsp;接收<label style='color: #5FB878;'></label>&nbsp;数据：<label style='color: #FF5722;'>" + msg + "</label></span></div><br>");
        $("#feedinfo").scrollTop($("#feedinfo")[0].scrollHeight);
     }
}

function detail(value) {
    clearlog();
    $("#socketStr").text(value);
    querySendAndRecInfo()
    $("#devicedetail").click()
    if(isFirst){
        recInfoloop = window.setInterval(function(){
        querySendAndRecInfo();
        queryAllSendAndRecInfo();
        },5000);
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
    var res = recData <=0 ? "0%" :(Math.round(recData/sendData * 10000) / 100.00 + "%")
    $("#precentData").text(res)
}

function queryAllSendAndRecInfo() {
    $.ajax({
                url:"querysendAndRecInfo",
                method:"post",
                data:{},
                dataType:"json",
                success:function(res){
                    if(res.errorCode==="SUCCESS"){
                        var result = res.result;
                        updateAllDataInfo(result.sendNum,result.sendData,result.recNum,result.recData)
                    }else{
                        layer.alert(res.msg);
                    }
                },
                error:function(res){
                    layer.alert(res.status);
                }
            })
}
function updateAllDataInfo(sendNum,sendData,recNum,recData) {
    $("#total_sendNum").text(sendNum)
    $("#total_sendData").text(sendData)
    $("#total_recNum").text(recNum)
    $("#total_recData").text(recData)
    var res = recData <=0 ? "0%" :(Math.round(recData/sendData * 10000) / 100.00 + "%")
    $("#total_precentData").text(res)
}


function queryDeviceDatile(table) {
    $.ajax({
       url:"queryModelStatusInfo",
       method:"post",
       data:{"socketstr":$("#socketStr").text()},
       dataType:"json",
       success:function(res){
           if(res.errorCode==="SUCCESS"){
                var result = res.result;
                var options = {
                	elem: '#failtable',
                	height: 450,
                	page: true,
                	data: result
                }
                table.init("failtableInfo",options);
           }else{
                layer.alert(res.msg);
           }
       },
       error:function(res){
           layer.alert(res.status);
       }
    })
}
function startServer(value) {
    $.ajax({
       url:"startTcpServer",
       method:"post",
       data:{"port":$("#port").val(),"type":value},
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