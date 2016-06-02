package ccoderad.bnds.shiyiquanevent.Beans;

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
    public int eventFollower;
    public boolean isFaved = false;
    private boolean isParsed = false;
    public void parseUrl(){
        if(!isParsed) {
            eventAvatar=eventAvatar.replace("medium","large");
            this.eventAvatar = "http://shiyiquan.net" + this.eventAvatar;
            isParsed = true;
        }
    }
}
