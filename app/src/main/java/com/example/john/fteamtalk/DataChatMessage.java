package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/8.
 */
public class DataChatMessage {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;
    public static final int IF_PIC_YES = 2;
    public static final int IF_PIC_NO = 3;
    /**
     * 是否是图片
     */
    private int ifPic;
    /**
     * 图片path
     */
    private String path;
    /**
     * id
     */
    private int id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 聊天内容
     */
    private String chatMessage;
    /**
     *
     * 是否为本人发送
     */
    private int type;

    public DataChatMessage(int id, String name, String chatMessage, int type, int ifPic, String path) {
        this.id = id;
        this.name = name;
        this.chatMessage = chatMessage;
        this.type = type;
        this.ifPic = ifPic;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public int getType() {
        return type;
    }

    public int getIfPic() {
        return ifPic;
    }

    public String getPath() {
        return path;
    }
}
