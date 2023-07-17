package me.fullIdle.fichatgpt.FIChatGPT.Util;

import java.net.HttpURLConnection;
import java.net.URL;

public class Delete {
    public static int deleteAllConversation(String url) throws Exception{
        url = url+"/api/conversations";
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("DELETE");
        int responseCode = conn.getResponseCode();
        return responseCode;
    }
    
    public static int deleteConversation(String url,String conversationID) throws Exception{
        url = url+"/api/conversation/"+conversationID;
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("DELETE");
        int responseCode = conn.getResponseCode();
        return responseCode;
    }
}
