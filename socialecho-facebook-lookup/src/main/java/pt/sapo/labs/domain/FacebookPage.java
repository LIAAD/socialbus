package pt.sapo.labs.domain;

import com.restfb.Facebook;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 15/01/14
 * Time: 06:28
 * To change this template use File | Settings | File Templates.
 */
public class FacebookPage {

    public String getPage_id() {
        return page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFan_count() {
        return fan_count;
    }

    public void setFan_count(int fan_count) {
        this.fan_count = fan_count;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public int getTalking_about_count() {
        return talking_about_count;
    }

    public void setTalking_about_count(int talking_about_count) {
        this.talking_about_count = talking_about_count;
    }

    public int getWere_here_count() {
        return were_here_count;
    }

    public void setWere_here_count(int were_here_count) {
        this.were_here_count = were_here_count;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    @Facebook
    String page_id;

    @Facebook
    String username;


    @Facebook
    String name;

    @Facebook
    int fan_count;

    @Facebook
    String page_url;

    @Facebook
    int talking_about_count;

    @Facebook
    int were_here_count;

    @Facebook
    boolean is_verified;

    @Override
    public String toString() {
        return String.format("%s, %s,  (%s)", page_id, name ,page_url);
    }
}
