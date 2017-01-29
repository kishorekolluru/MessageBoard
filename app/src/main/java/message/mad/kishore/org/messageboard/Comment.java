package message.mad.kishore.org.messageboard;

/**
 * Created by kishorekolluru on 11/14/16.
 */

public class Comment {
    private String userId;
    private String comment;
    private long time;

    public Comment(){

    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
