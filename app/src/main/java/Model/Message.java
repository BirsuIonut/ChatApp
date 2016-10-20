package Model;

/**
 * Created by Ady on 7/18/2016.
 */

public class Message {

    private String mId;
    private String mText;
    private String mFrom;
    private String mTo;

    public Message() {
    }

    public Message(String text, String from, String to) {
        mText = text;
        mFrom = from;
        mTo = to;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }
}
