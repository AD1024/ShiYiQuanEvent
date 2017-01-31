package ccoderad.bnds.shiyiquanevent.beans;

import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.global.URLConstants;

/**
 * Created by CCoderAD on 16/5/12.
 */
public class ClubModel {
    public String club_name;
    public List<String> status;
    public String sname;
    public String LargeAvatarURL;
    public String mediumAvatarURL;
    public String smallAvatarURL;
    public boolean isAdmin;

    public ClubModel(){
        status = new ArrayList<>();
        isAdmin = false;
    }

    /*
    * Add Prefix of Club Avatar
    * */
    public void parseURL() {
        if (!LargeAvatarURL.contains(URLConstants.HOME_URL)) {
            LargeAvatarURL = URLConstants.HOME_URL + LargeAvatarURL;
        }
        if (!mediumAvatarURL.contains(URLConstants.HOME_URL)) {
            mediumAvatarURL = URLConstants.HOME_URL + mediumAvatarURL;
        }
        if (!smallAvatarURL.contains(URLConstants.HOME_URL)) {
            smallAvatarURL = URLConstants.HOME_URL + smallAvatarURL;
        }
    }
}
