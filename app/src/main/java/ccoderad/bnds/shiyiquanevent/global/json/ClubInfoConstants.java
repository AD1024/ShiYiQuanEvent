package ccoderad.bnds.shiyiquanevent.global.json;

/**
 * Created by CCoderAD on 2017/3/8.
 */

public class ClubInfoConstants {
    public static final String BADGE_LIST_TAG = "badge_list";
    public static final String EVENT_LIST_TAG = "ev_list";
    public static final String PRESENTATION_TAG = "presentation_list";
    public static final String HEAD_TAG = "club_head";
    public static final String VICE_TAG = "club_vice";
    public static final String VISITORS_TAG = "visitors";
    public static final String FOLLOWER_TAG = "club_follower_num";
    public static final String MEMBER_TAG = "club_member_num";
    public static final String FULL_INTRO_TAG = "full_intro";
    public static final String SIMP_INTRO_TAG = "simp_intro";
    public static final String FULL_NAME_TAG = "full_name";
    public static final String AVATAR_TAG = "avatar";

    public static class Event{
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String SUBJECT = "subject";
        public static final String LOCATION = "location";
        public static final String CONTENT = "content";
    }
    public static class Badge{
        public static final String DESC = "desc";
        public static final String NAME = "name";
        public static final String RANK = "rank";
    }

    public static class Presentation{
        public static final String TITLE = "title";
        public static final String PIC_URL = "link";
    }
}
