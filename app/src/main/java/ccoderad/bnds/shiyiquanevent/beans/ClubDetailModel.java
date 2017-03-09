package ccoderad.bnds.shiyiquanevent.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.global.URLConstants;

/**
 * Created by CCoderAD on 2016/10/27.
 */

public class ClubDetailModel implements Serializable {
    private static String HOME_URL = "http://shiyiquan.net/";
    public String ClubDescription;
    public String SimpleIntro;
    public List<String> Followee;
    public int Like;
    public String Category;
    public String ClubHomePage;
    public boolean Followed;
    public String club_name;
    public String status;
    public String sname;
    public String LargeAvatarURL;
    public String mediumAvatarURL;
    public String smallAvatarURL;
    public String visitorNum;

    public ClubDetailModel() {
        Followee = new ArrayList<>();
    }

    public void parseURL() {
        if (!LargeAvatarURL.contains(HOME_URL)) {
            int index = LargeAvatarURL.indexOf("large");
            String First = LargeAvatarURL.substring(0, index);
            String Second = LargeAvatarURL.substring(index + 6);
            LargeAvatarURL = First + Second;
            LargeAvatarURL = HOME_URL + LargeAvatarURL;
        }
        if (!mediumAvatarURL.contains(HOME_URL)) {
            mediumAvatarURL = HOME_URL + mediumAvatarURL;
        }
        if (!smallAvatarURL.contains(HOME_URL)) {
            smallAvatarURL = HOME_URL + smallAvatarURL;
        }
    }

    /*
    * Add Prefix of Club Home page
    * */
    public void ChangeHomePage(String uri) {
        if (!uri.contains(URLConstants.HOME_URL)) {
            ClubHomePage = URLConstants.HOME_URL + uri;
        }
    }
}
