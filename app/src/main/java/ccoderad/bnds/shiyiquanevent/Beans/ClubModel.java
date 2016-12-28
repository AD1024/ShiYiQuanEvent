package ccoderad.bnds.shiyiquanevent.Beans;

import ccoderad.bnds.shiyiquanevent.Global.URLConstances;

/**
 * Created by CCoderAD on 16/5/12.
 */
public class ClubModel{
    private static String HOME_URL = URLConstances.HOME_URL;
    public String club_name;
    public String status;
    public String sname;
    public String LargeAvatarURL;
    public String mediumAvatarURL;
    public String smallAvatarURL;

    /*
    * Add Prefix of Club Avatar
    * */
    public void parseURL(){
        if(!LargeAvatarURL.contains(HOME_URL)){
            LargeAvatarURL = HOME_URL+LargeAvatarURL;
        }
        if(!mediumAvatarURL.contains(HOME_URL)){
            mediumAvatarURL = HOME_URL + mediumAvatarURL;
        }
        if(!smallAvatarURL.contains(HOME_URL)){
            smallAvatarURL = HOME_URL + smallAvatarURL;
        }
    }
}
