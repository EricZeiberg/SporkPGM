package io.sporkpgm.commands;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.minecraft.util.commands.ChatColor;
import io.sporkpgm.player.SporkPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class NickCommands {
    @Command(aliases = {"nick"}, desc = "Commands for nicknames", usage = "<nick> [target-player>", min = 1)
    public static void nick(CommandContext cmd, CommandSender sender) throws CommandException {

        if (cmd.argsLength() == 0) {
            sender.sendMessage(ChatColor.RED + "/nick <nick> [target-player]");
            return;
        }
        Player playersender = (Player) sender;
        String nickname = cmd.getString(0);
        if (nickname.length() > 15) {
            sender.sendMessage(ChatColor.RED + "Nickname too long!");
            return;
        }

        if (cmd.argsLength() == 1) {
            SporkPlayer cmdsender = SporkPlayer.getPlayer(sender.getName());
            cmdsender.setNickname(nickname);
            sender.sendMessage(cmdsender.getTeamColour() + "Your" + ChatColor.GRAY + "  nickname has changed to " + cmdsender.getTeamColour() + nickname);

            playersender.setPlayerListName(cmdsender.getTeamColour() + nickname);
        } else if (cmd.argsLength() == 2) {
            String target = cmd.getString(1);
            SporkPlayer targetplayer = SporkPlayer.getPlayer(target);
            if(targetplayer == null) {
                sender.sendMessage(org.bukkit.ChatColor.RED + "No players matched query.");
                return;
            }
            targetplayer.setNickname(nickname);
            sender.sendMessage(targetplayer.getTeamColour() + targetplayer.getName() + ChatColor.GRAY + " has changed their nickname to " + targetplayer.getTeamColour() + nickname);
            Player bukkittp = targetplayer.getPlayer();
            bukkittp.setPlayerListName(targetplayer.getTeamColour() + nickname);
        } else {
            sender.sendMessage(ChatColor.RED + "/nick <nick> [target-player]");
        }

    }
}
