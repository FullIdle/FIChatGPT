package me.fullIdle.fichatgpt.FIChatGPT.Util;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static me.fullIdle.fichatgpt.FIChatGPT.Main.*;

public class Talk {
    private static String getRequestBody(String prompt, String model, String messageId, String parentMessageId,
                                         String conversationId, boolean stream){
        String json;
        JsonObject object = new JsonObject();
        object.addProperty("prompt",prompt);
        object.addProperty("model",model);
        object.addProperty("message_id",messageId);
        object.addProperty("stream",stream);
        object.addProperty("parent_message_id",parentMessageId == null?UUID.randomUUID().toString():parentMessageId);
        if (conversationId != null){
            object.addProperty("conversation_id",conversationId);
        }
        json = gson.toJson(object);
        return json;
    }

    public static String ContinueToTalk(String url,String prompt,String messageId,String parentMessageId,String conversationId,Boolean useStream) throws Exception{
        url = url+"/api/conversation/talk"; // 替换为实际的API端点URL
        String model = "text-davinci-002-render-sha";
        String json = getRequestBody(prompt, model, messageId, parentMessageId, conversationId, useStream);

        return talk(url, json);
    }

    public static String unicodeToUtf8(String unicode) {
        StringBuilder utf8Content = new StringBuilder(unicode.length());

        int length = unicode.length();
        int i = 0;
        while (i < length) {
            char currentChar = unicode.charAt(i);
            if (currentChar == '\\' && i + 1 < length && unicode.charAt(i + 1) == 'u') {
                int codePoint = Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
                utf8Content.appendCodePoint(codePoint);
                i += 6;
            } else {
                utf8Content.append(currentChar);
                i++;
            }
        }

        return utf8Content.toString();
    }

    public static String talk(String completeUrl,String json) throws Exception{
        URL obj = new URL(completeUrl);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write(json);
        writer.flush();
        writer.close();

        int responseCode = conn.getResponseCode();

        main.getLogger().info("§3响应代码:§a"+responseCode);

        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
            main.getServer().broadcastMessage(prefix+"§7:§c请求的ConversationId不存在,调整成新页面发送(可以理解gpt之前的记忆清除了)");
            JsonObject jsonObject = gson.fromJson(json,JsonObject.class);
            jsonObject.remove("conversation_id");
            jsonObject.addProperty("parent_message_id", UUID.randomUUID().toString());
            return talk(completeUrl,gson.toJson(jsonObject));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        String decodedContent = unicodeToUtf8(response.toString());
        return decodedContent;
    }
}