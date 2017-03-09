package ccoderad.bnds.shiyiquanevent.beans;

import java.io.Serializable;

/**
 * Created by CCoderAD on 16/3/17.
 */
public class EventBean implements Serializable {
    public String eventTitle;
    public String sponsorName;
    public String eventDate;
    public String eventTime;
    public String eventDuration;
    public String eventContent;
    public String eventLocation;
    public String eventAvatar;
    public String eventURL;
    public int eventFollower;
    public String sponsorSname;
    public boolean isFaved;
    /*
    * timeBegin and timeEnd are only used in UserInfoActivity
    * */
    public long timeBegin;
    public long timeEnd;
    private boolean isParsed = false;

    public EventBean() {
        isFaved = false;
    }

    public void parseUrl() {
        if (!isParsed) {
            eventAvatar = eventAvatar.replace("medium", "large");
            this.eventAvatar = "http://shiyiquan.net" + this.eventAvatar;
            isParsed = true;
        }
    }
}
