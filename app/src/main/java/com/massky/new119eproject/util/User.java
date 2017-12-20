package com.massky.new119eproject.util;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;

/**
 * Created by zhu on 2017/12/1.
 */

public class User {
    public String result;

    /**
     * 微型消防站列表
     */
    public List<wxxfz> wxxfzList;
    public static class wxxfz {
        public String id;
        public String name;
    }

    /**
     * 微型消防站职位
     */
    public List<zhiwei> zhiweiList;
    public static class zhiwei {
        public String id;
        public String name;
    }
    /**
     * 登录账号 119e 项目
     */
    public  account account;//119e project
    public static class account implements Serializable{
        public String id;
        public String userName;
        public String password;
        public String realName;
        public String status;
        public String type;
        public String phoneNumber;
        public String qq;
        public String weixin;
        public String zhiwei;
        public String wxxfz;
        public String address;
    }

  /*  App获取报警列表*/
    public List<fire>  fireList;
    public static class fire {
       public String id;
       public String address;
       public String content;
       public String    time;
    }
}
