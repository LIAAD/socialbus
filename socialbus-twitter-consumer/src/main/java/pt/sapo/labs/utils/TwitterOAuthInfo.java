package pt.sapo.labs.utils;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/9/13
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterOAuthInfo {

    String consumerKey;
    String consumerSecret;
    String screenName;

    public boolean isValid(){

        if(this.consumerKey == null ||
           this.consumerSecret == null ||
           this.token == null ||
           this.secret == null ||
           this.consumerKey == "" ||
           this.consumerSecret == "" ||
           this.token == "" ||
           this.secret == ""
           ){
            return false;
        }

        return true;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screen_name) {
        this.screenName = screen_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    String token;

    String secret;
}
