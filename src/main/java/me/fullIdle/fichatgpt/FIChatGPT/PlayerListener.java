package me.fullIdle.fichatgpt.FIChatGPT;

import me.fullIdle.fichatgpt.FIChatGPT.Util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import static me.fullIdle.fichatgpt.FIChatGPT.Main.*;

public class PlayerListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if (!e.getPlayer().hasPermission("fichatgpt.use")) {
            return;
        }
        String message = e.getMessage().toLowerCase();
        String chatStart = main.getConfig().getString("ChatStart").toLowerCase();

        if (continuousRunnable.get(e.getPlayer().getUniqueId()) == null){
            if (!message.contains(chatStart)) {
                return;
            }
        }

        String askMsg = e.getMessage().replace(getMsg(main.getConfig().getString("ChatStart"),null),"");
        if (!main.getConfig().getBoolean("MultitaskConversation")){
            if (runnableList.size() != 0){
                e.getPlayer().sendMessage(prefix+":§3GPT正在处理别的问题,请等待回答后在提问");
                e.setCancelled(true);
                return;
            }
        }
        Util.askQuestion(e.getPlayer(), askMsg);
    }
}
