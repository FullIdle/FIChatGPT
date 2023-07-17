package me.fullIdle.fichatgpt.FIChatGPT.command;

import me.fullIdle.fichatgpt.FIChatGPT.Util.Delete;
import me.fullIdle.fichatgpt.FIChatGPT.Util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.fullIdle.fichatgpt.FIChatGPT.Main.*;

public class CMD implements CommandExecutor, TabCompleter {
    List<String> subCmd = Arrays.asList(
            "help",
            "reload",
            "delete",
            "remove",
            "chat");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1){
            if (subCmd.contains(args[0])){
                String arg = args[0];
                switch (arg) {
                    case "reload": {
                        if (!sender.hasPermission("fichatgpt.command.reload")) {
                            return false;
                        }
                        main.reload(true);
                        sender.sendMessage(prefix + ":§3All settings and configurations have been reloaded!");
                        break;
                    }
                    case "delete": {
                        if (!sender.hasPermission("fichatgpt.command.delete")) {
                            return false;
                        }
                        if (deleteAllGPT()) {
                            sender.sendMessage(prefix + ":§3All GPT records have been removed!");
                        } else {
                            sender.sendMessage(prefix + ":§cError");
                        }
                        break;
                    }
                    case "remove": {
                        if (!sender.hasPermission("fichatgpt.command.delete")) {
                            return false;
                        }
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(prefix + ":§3You are not a player!");
                            return false;
                        }
                        Player player = (Player) sender;
                        String conId = main.getConfig().getString("PlayerData." + player.getName() + ".ConversationId");
                        if (Util.isValidUUID(conId)) {
                            if (deleteGPT(player,conId)) {
                                sender.sendMessage(prefix + ":§3Successfully cleared your GPT record");
                            } else {
                                sender.sendMessage(prefix + ":§cDeletion failed. Error");
                            }
                        } else {
                            sender.sendMessage(prefix + ":§3No record to delete");
                        }
                        break;
                    }
                    case "chat": {
                        if (sender instanceof Player) {
                            sender.sendMessage(prefix + "§cThis command can only be run by the console");
                            return false;
                        }
                        if (args.length < 2) {
                            sender.sendMessage(prefix + ":§cPlease add ask content");
                            return false;
                        }
                        String prompt = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                        Util.askQuestion(sender,prompt);
                        break;
                    }
                }
                return false;
            }
        }
        sender.sendMessage(
                "§3args↓"+prefix+" HELP\n" +
                "§7  help      帮助\n" +
                "§7  reload    重载配置\n" +
                "§7  delete    删除所有玩家的记录\n" +
                "§7  remove    清理你gpt的记忆\n" +
                "§7================\n"
        );
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }

        if (args.length == 0) {
            return subCmd;
        }

        return subCmd.stream()
                .filter(s -> s.startsWith(args[0]))
                .collect(Collectors.toList());
    }

    public static boolean deleteAllGPT(){
        main.getConfig().set("PlayerData",null);
        main.getConfig().set("Server",null);
        main.saveConfig();
        try {
            Delete.deleteAllConversation(conUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteGPT(Player player,String conId){
        main.getConfig().set("PlayerData."+player.getName(),null);
        main.saveConfig();
        try {
            Delete.deleteConversation(conUrl,conId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
