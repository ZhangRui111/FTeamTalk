package com.example.john.fteamtalk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/5/23.
 */

public class DataFriendList {
    private int code;
    private String msg;
    private List<Data> data;

    public class Data {
        private String username;
        private String friendName;
        private String depart;

        public String getDepart() {
            return depart;
        }

        public String getUsername() {
            return username;
        }

        public String getFriendName() {
            return friendName;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public List<Data> getData() {
        return data;
    }

    public Data getDataItem(int position) {
        return data.get(position);
    }
}