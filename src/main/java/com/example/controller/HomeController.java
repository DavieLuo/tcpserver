package com.example.controller;

import com.example.conf.BaseConf;
import com.example.conf.ClearRedisData;
import com.example.entity.ClientOnAndOffline;
import com.example.entity.NettyMessage;
import com.example.entity.SendRecInfo;
import com.example.nettyclient.ClientUtil;
import com.example.nettyclient.SendDataUtil;
import com.example.nettyserver.ServerUtil;
import com.example.service.KeepDateService;
import com.example.service.NettyMsgService;
import com.example.service.OnAndOfflineService;
import com.example.unit.BackResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class HomeController {

    @Autowired
    private NettyMsgService nettyMsgService;

    @Autowired
    private KeepDateService dateService;

    @Autowired
    private OnAndOfflineService lineService;


    @Autowired
    private ClearRedisData clearRedisData;

    /**
     * 添加 client
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/createClient")
    public Object startClient(HttpServletRequest request, HttpServletResponse response){

        String clientNum = request.getParameter("clientNum");
        String port = request.getParameter("port");
        String host = request.getParameter("host");
        if(clientNum==null||port==null||host==null){
            return BackResult.getFail("参数错误");
        }
        Integer newClientNum=Integer.parseInt(clientNum);
        if(ClientUtil.getInstance().getChannelMapSize()==10){
            return BackResult.getFail("client 已经达到上限");
        }
        if(newClientNum+ClientUtil.getInstance().getChannelMapSize()>=10){

            newClientNum=10-ClientUtil.getInstance().getChannelMapSize();
        }
        try{
            ClientUtil.getInstance().startClient(Integer.parseInt(port),host,newClientNum);
            return BackResult.getSuccess("OK",null);
        }catch (Exception e){
            if(e instanceof ExecutionException){
                System.out.println(e.getMessage());
                return BackResult.getFail("client 创建失败");
            }
        }
        return BackResult.getFail("异常");

    }

    /**
     * 修改发送数据线程的参数 时间间隔 数据内容
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateSendParam")
    public Object setParam(HttpServletRequest request, HttpServletResponse response){
        String msg = request.getParameter("msg");
        String timeNum = request.getParameter("timeNum");
        if(msg==null||timeNum==null){
            return BackResult.getFail("参数错误");
        }
        if(BaseConf.getInstance().getSendDataUtil()==null){
            SendDataUtil sendDataUtil = new SendDataUtil();
            sendDataUtil.setTimejg(Integer.parseInt(timeNum));
            sendDataUtil.setMsg(msg);
            sendDataUtil.start();
            BaseConf.getInstance().setSendDataUtil(sendDataUtil);
        }
        BaseConf.getInstance().setSendData(msg);
        BaseConf.getInstance().setTimejg(Integer.parseInt(timeNum));
        return BackResult.getSuccess("OK",null);
    }


    /**
     * 查询设备信息 ip 状态等
     * @return
     */
    @RequestMapping("/queryClientInfo")
    public Object queryClientInfo(){
        List<NettyMessage> ls = nettyMsgService.queryAllNettyMsgList();
        return BackResult.getSuccess("OK",ls);
    }


    /**
     * 查询设备信息 ip 状态等
     * @return
     */
    @RequestMapping("/queryClientInfoByServer")
    public Object queryServreClientInfo(){
        List<NettyMessage> ls = nettyMsgService.queryAllNettyServerMsgList();
        return BackResult.getSuccess("OK",ls);
    }

    /**
     * 查询设备发送接受数据信息
     * @param request
     * @return
     */
    @RequestMapping("queryModelSendData")
    public Object queryModelData(HttpServletRequest request){
        String socketstr = request.getParameter("socketstr");
        if(socketstr==null){
            return BackResult.getFail("参数错误");
        }
        SendRecInfo sendRecInfo = dateService.getInfo(socketstr);
        return BackResult.getSuccess("",sendRecInfo);
    }

    @RequestMapping("querysendAndRecInfo")
    public Object queryAllsendAndRecData(){
        SendRecInfo sendRecInfo = dateService.getAllSendAndRecInfo();
        return BackResult.getSuccess("",sendRecInfo);
    }


    /**
     * 查询设备上下线数据
     * @param request
     * @return
     */
    @RequestMapping("/queryModelStatusInfo")
    public Object queryModelOnAndOff(HttpServletRequest request){
       String socketstr = request.getParameter("socketstr");
       if(socketstr==null){
           return BackResult.getFail("参数错误");
       }
       List<ClientOnAndOffline> ls = lineService.queryAllClientInfo(socketstr);
       return BackResult.getSuccess("",ls);
    }

    /**
     * 启动或者停止tcp服务
     * @param request
     * @return
     */
    @RequestMapping("/startTcpServer")
    public Object startTcpServer(HttpServletRequest request){

        String type=request.getParameter("type");
        String port = request.getParameter("port");

        if("start".equals(type)){
            if(port==null||port.equals("")){
                return BackResult.getFail("参数错误");
            }
            String msg = ServerUtil.getInstance().startService(Integer.parseInt(port));
            return BackResult.getSuccess(msg,null);
        }else {
            ServerUtil.getInstance().stopService();
        }
        return BackResult.getSuccess("OK",null);
    }

    /**
     * 清除 连接 缓存
     * @param request
     * @return
     */
    @RequestMapping("/clearCache")
    public Object clearCache(HttpServletRequest request){

        String type = request.getParameter("type");
        if("client".equals(type)){
            //清除缓存
            clearRedisData.clearDataForclient();
            //client 释放连接
            ClientUtil.getInstance().closeClient();


        }else if("server".equals(type)){

            clearRedisData.clearDataForServer();
            ServerUtil.getInstance().clearChannel();
        }else{
            return BackResult.getFail("参数错误");
        }
        return BackResult.getSuccess("OK",null);
    }

}
