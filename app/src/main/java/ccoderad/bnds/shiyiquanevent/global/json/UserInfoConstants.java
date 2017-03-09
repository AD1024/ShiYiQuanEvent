package ccoderad.bnds.shiyiquanevent.global.json;

/**
 * Created by CCoderAD on 2017/3/5.
 */

public class UserInfoConstants {
    public static final String BADGE_TAG = "badge_list";
    public static final String ATTEND_LIST_TAG = "attend_list";
    public static final String FRIEND_TAG = "contact_user";
    public static final String FOLLOW_CLUB_TAG = "club_follow";
    public static final String CLUB_JOIN_TAG = "club_join";
    public static final String SHARE_LIST_TAG = "share_list";

    public class ContactUser {
        public static final String LAST_NAME_TAG = "last_name";
        public static final String FIRST_NAME_TAG = "first_name";
        public static final String NICKNAME_TAG = "nickname";
        public static final String AVATAR_TAG = "avatar";
        public static final String VIS_COUNT_TAG = "visit_count";
        public static final String REG_TIME_TAG = "join_time";
    }

    public class UserClub {
        public static final String FOLLOWER_NUM_TAG = "follower_num";
        public static final String MEMBER_NUM_TAG = "member_num";
        public static final String POSITION_TAG = "position";
        // In data
        public static final String FULL_NAME_TAG = "full_name";
        public static final String SIMP_NAME_TAG = "simp_name";
        public static final String SIMP_INTRO_TAG = "simp_intro";
        public static final String EVENT_NUM_TAG = "event_num";
    }
}
