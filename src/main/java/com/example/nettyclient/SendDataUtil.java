package com.example.nettyclient;



public class SendDataUtil extends  Thread {


    volatile private Integer timejg=3000;
    volatile private String msg="aabbcc";
    volatile private boolean flag = false;

    public Integer getTimejg() {
        return timejg;
    }

    public void setTimejg(Integer timejg) {
        this.timejg = timejg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void stopThread(){
        flag=true;
    }

    @Override
    public void run() {
        try {
            while(!flag){
                Thread.sleep(timejg);
                ClientUtil.getInstance().sendMsg(msg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }
}
