package me.fullIdle.fichatgpt.FIChatGPT;

import com.google.gson.Gson;
import me.fullIdle.fichatgpt.FIChatGPT.command.CMD;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    public static boolean continuous = false;
    public static List<BukkitRunnable> runnableList;
    public static Main main;
    public static Gson gson;
    public static String conUrl;
    public static String prefix;
    public static boolean saveInRealTime;
    @Override
    public void onEnable() {
        main = this;
        gson = new Gson();
        reload();
        if (getConfig().getBoolean("CleanUpAtStartup")) {
            CMD.deleteAllGPT();
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);

        CMD cmd = new CMD();
        getCommand("fichatgpt").setExecutor(cmd);
        getCommand("fichatgpt").setTabCompleter(cmd);

        getLogger().info("Plugin loaded!");
    }

    private void setPrefix() throws NoSuchFieldException, IllegalAccessException {
        getLogger();
        Field prefixField = this.getDescription().getClass().getDeclaredField("prefix");
        prefixField.setAccessible(true);
        prefixField.set(this.getDescription(),getMsg(getConfig().getString("Prefix"),null));
    }

    public static String getMsg(String str,String replay){
        String msg = str.replace("&","§");
        return replay==null?msg : msg.replace("{REPLY}",replay);
    }

    public void reload(boolean... reload){
        saveDefaultConfig();
        if (reload.length > 0)if (reload[0]) {
            reloadConfig();
        }
        try {
            setPrefix();
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        main = this;
        gson = new Gson();
        conUrl = getConfig().getString("URL");
        runnableList = new ArrayList<>();
        prefix = getDescription().getPrefix();
        saveInRealTime = getConfig().getBoolean("SaveInRealTime");
    }

    @Override
    public void onDisable() {
        if (!saveInRealTime) {
            getLogger().info("§e进行同步中...");
            main.saveConfig();
            getLogger().info("§a同步完成!");
        }
    }
}