package com.example.unit;

public enum Nstatus {

    Online("online"),
    OffLine("offLine"),
    OpenWebSocket("open"),
    CloseWebSocket("close"),

    NettyList("socketInfoSet"), //as tcp client ,client infolist
    NettyServerList("serversocketInfoSet"), //as tcp server,client infolist

    Send_Rec_Info("send_rec_info"), //SendRecInfo 实体类对应的key

    RecInfo("recInfo"),  //接收数据 map
    RecNum("recNum"),
    RecData("recData"),

    SendInfo("sendInfo"), //发送数据 map
    SendNum("sendNum"),
    SendData("sendData"),

    Server("server"),    //websocket 发送数据时  type server 通过server 给模块发送数据
    Client("client"),      //websocket 发送数据时 type client 通过client  给模块发送数据

    On_Off_Line("on_off_line"), // socket   做server 时 模块的上线下线 记录

    Msg_to_web("msg_to_web"), //将接受到的消息保存至缓存redis 中
    Msg_middle("msg_middle"),
    ;
    public String V;
    Nstatus(String value){
        this.V=value;
    }

}
