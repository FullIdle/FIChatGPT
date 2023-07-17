package me.fullIdle.fichatgpt.FIChatGPT.Util;

import com.google.gson.JsonObject;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static me.fullIdle.fichatgpt.FIChatGPT.Main.*;

public class Util {
    public static void askQuestion(CommandSender sender, String prompt){
        addRunnableAndTryToStart(getChatRunnable(sender, prompt));
    }

    public static boolean isValidUUID(String uuidString) {
        if (uuidString == null) {
            return false;
        }
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //获取回复
    public static String getReplay(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        JsonObject contentObject = jsonObject.getAsJsonObject("message").getAsJsonObject("content");
        String[] parts = gson.fromJson(contentObject.get("parts"), String[].class);

        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part);
        }
        return builder.toString();
    }

    public static String getConversationId(String jsonString){
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String conversationId = jsonObject.get("conversation_id").getAsString();
        return conversationId;
    }
    public static String getParentMessageId(String jsonString){
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String parentMessageId = jsonObject.get("message").getAsJsonObject().get("id").getAsString();
        return parentMessageId;
    }
    public static boolean addRunnableAndTryToStart(BukkitRunnable runnable){
        runnableList.add(runnable);
        if (runnableList.size() == 1){
            runnableList.get(0).runTaskAsynchronously(main);
            return true;
        }else{
            return false;
        }
    }

    public static BukkitRunnable getChatRunnable(CommandSender sender,String prompt){
        Player player = sender instanceof Player ? ((Player) sender) : null;
        String playerName = player == null ? null : player.getName();
        FileConfiguration config = main.getConfig();
        boolean current = config.getBoolean("Current");
        String parentPath = current ? "Server.ParentMessageId" : player == null ? "Server.ParentMessageId"
                : ("PlayerData."+player.getName()+".ParentMessageId");
        String conPath = current ? "Server.ConversationId": player == null ? "Server.ConversationId"
                : ("PlayerData."+player.getName()+".ConversationId");
        String messageID = UUID.randomUUID().toString();
        return new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String parentUID = config.getString(parentPath);
                    String conversationUID = config.getString(conPath);
                    String jsonString = Talk.ContinueToTalk(conUrl, prompt, messageID,parentUID, conversationUID, false);
                    config.set(parentPath,getParentMessageId(jsonString));
                    config.set(conPath,getConversationId(jsonString));
                    if (saveInRealTime) {
                        main.saveConfig();
                    }
                    int delay = config.getInt("ContinuousDialogue");
                    if (player != null)if (delay > 0){
                        setContinuousRunnable(player);
                    }
                    String replay = getMsg(config.getString("Format"), getReplay(jsonString));
                    if (player != null) {
                        main.getServer().broadcastMessage(replay);
                    }else{
                        main.getLogger().info(replay);
                    }
                } catch (Exception e) {
                    main.getServer().broadcastMessage(prefix + ":§c回答玩家:§e" + playerName + "§c的内容出问题了\n" +
                            "请该玩家尝试使用/figpt remove后再试试");
                    e.printStackTrace();
                }
                if (runnableList.size() >= 2) {
                    runnableList.get(1).runTaskAsynchronously(main);
                }
                runnableList.remove(0);
            }
        };
    }

    public static BukkitRunnable setContinuousRunnable(Player player){
        BukkitRunnable runnable = continuousRunnable.get(player.getUniqueId());
        if (runnable != null){
            runnable.cancel();
            continuousRunnable.remove(player.getUniqueId());
        }
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                continuousRunnable.remove(player.getUniqueId());
            }
        };
        continuousRunnable.put(player.getUniqueId(),runnable);
        runnable.run();
        return runnable;
    }
}
