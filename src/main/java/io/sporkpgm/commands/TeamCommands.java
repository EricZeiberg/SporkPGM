package io.sporkpgm.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommands {

	@Command(aliases = {"myteam", "mt"}, desc = "Shows what team you are on", max = 0)
	public static void myteam(CommandContext cmd, CommandSender sender) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("This command is for Players only");
		}

		SporkPlayer player = SporkPlayer.getPlayer((Player) sender);
		SporkTeam team = player.getTeam();
		sender.sendMessage(ChatColor.GRAY + "You are on " + team.getColoredName());
	}

	@Command(aliases = {"team"}, desc = "Commands for working with teams", usage = "[time]", min = 1)
	@NestedCommand(TeamSubCommands.class)
	public static void team(CommandContext cmd, CommandSender sender) throws CommandException { /* nothing */ }

	public static class TeamSubCommands {

		@Command(aliases = {"alias"}, desc = "Change the alias of a team", usage = "<old name> <new name>", min = 1)
		@CommandPermissions("spork.team.alias")
		public static void alias(CommandContext cmd, CommandSender sender) {
			String oldName = cmd.getString(0);
			String newName = cmd.getJoinedStrings(1);

			SporkTeam team = SporkMap.getMap().getTeam(oldName);
			if(team == null) {
				sender.sendMessage(ChatColor.RED + "No teams matched query.");
				return;
			}

			String old = team.getName();
			ChatColor color = team.getColor();
			team.setName(newName);

			Bukkit.broadcastMessage(color + old + ChatColor.GRAY + " has been renamed to " + color + newName);
		}

		@Command(aliases = {"shuffle"}, desc = "Shuffle the teams", min = 0, max = 0)
		@CommandPermissions("spork.team.shuffle")
		public static void shuffle(CommandContext cmd, CommandSender sender) {
			/* shuffle the teams here */
		}

		@Command(aliases = {"force"}, desc = "Force a player onto a team", usage = "<player> <team>", min = 2)
		@CommandPermissions("spork.team.force")
		public static void force(CommandContext cmd, CommandSender sender) {
			SporkPlayer player = SporkPlayer.getPlayer(cmd.getString(0));
			if(player == null) {
				sender.sendMessage(ChatColor.RED + "No players matched query.");
				return;
			}

			SporkTeam team = SporkMap.getMap().getTeam(cmd.getJoinedStrings(1));
			if(team == null) {
				sender.sendMessage(ChatColor.RED + "No teams matched query.");
				return;
			}

			ChatColor old = player.getTeamColour();
			player.setTeam(team);
			sender.sendMessage(old + player.getName() + ChatColor.GRAY + " forced onto " + team.getColoredName());
		}

	}

}
