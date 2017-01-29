package message.mad.kishore.org.messageboard;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kishorekolluru on 11/14/16.
 */

public class Message {
    private String msgId;
    private String user;
    private String userId;
    private String message;
    private long time;
    private String imageUrl;
    private ArrayList<Comment> comments;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Message(){

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "user :" + user + "; message :" + message + ";time :" + time + "; imageUrl :" + imageUrl;
    }
}
