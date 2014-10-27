package io.sporkpgm.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import io.sporkpgm.Spork;
import io.sporkpgm.map.MapBuilder;
import io.sporkpgm.module.modules.info.Contributor;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.util.PaginatedResult;
import io.sporkpgm.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MapCommands {

	private static String name(Contributor contributor) {
		String contribution = (contributor.hasContribution() ? ChatColor.GREEN + "" + ChatColor.ITALIC + " " + "(" + contributor.getContribution() + ")" : "");
		return ChatColor.RED + contributor.getUsername() + contribution;
	}

	private static void print(Contributor contributor, CommandSender sender) {
		sender.sendMessage(" " + ChatColor.WHITE + " * " + name(contributor));
	}

	public static void sendInfo(CommandSender sender, MapBuilder loader) {
		String bold = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
		String bar = ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------";
		sender.sendMessage(" " + bar + ChatColor.DARK_AQUA + " " + loader.getName() + " " + ChatColor.GRAY + loader.getVersion() + " " + bar + " ");
		if(loader.getObjective() != null) {
			sender.sendMessage(bold + "Objective: " + ChatColor.GOLD + loader.getObjective());
		}

		if(loader.getAuthors().size() == 1) {
			sender.sendMessage(bold + "Author: " + name(loader.getAuthors().get(0)));
		} else {
			sender.sendMessage(bold + "Authors:");
			for(Contributor contributor : loader.getAuthors()) {
				print(contributor, sender);
			}
		}

		if(loader.getContributors().size() > 0) {
			if(loader.getContributors().size() == 1) {
				sender.sendMessage(bold + "Contributor: " + name(loader.getContributors().get(0)));
			} else {
				sender.sendMessage(bold + "Contributors:");
				for(Contributor contributor : loader.getContributors()) {
					print(contributor, sender);
				}
			}
		}

		if(loader.getRules().size() > 0) {
			sender.sendMessage(bold + "Rules:");

			int i = 1;
			for(String rule : loader.getRules()) {
				sender.sendMessage(ChatColor.WHITE + " " + i + ") " + ChatColor.GOLD + rule);
				i++;
			}
		}

		sender.sendMessage(bold + "Max players: " + ChatColor.GOLD + loader.getMaxPlayers());
	}

	@Command(aliases = {"map", "mapinfo"}, desc = "View information on the current map or map supplied", usage = "[map name]", max = 1)
	public static void map(CommandContext cmd, CommandSender sender) throws CommandException {
		if(cmd.argsLength() == 0) {
			MapBuilder currentmap = RotationSlot.getRotation().getCurrent().getBuilder();
			sendInfo(sender, currentmap);
		} else {
			MapBuilder result = Spork.get().getMap(cmd.getJoinedStrings(0));
			if(result == null)
				throw new CommandException("No map matched query!");
			sendInfo(sender, result);
		}
	}

	@Command(aliases = {"maps", "maplist"}, desc = "View all the currently loaded maps", usage = "[page]", max = 1)
	public static void maps(CommandContext cmd, CommandSender sender) throws CommandException {
		String bar = ChatColor.RED + "" + ChatColor.STRIKETHROUGH + " ----------- ";
		String loaded = ChatColor.DARK_AQUA + "Loaded Maps (" + ChatColor.AQUA + "[page]" + ChatColor.DARK_AQUA + " " +
				"of" + " " + ChatColor.AQUA + "[pages]" + ChatColor.DARK_AQUA + ")";
		String header = bar + loaded + bar;
		List<String> rows = new ArrayList<>();
		for(MapBuilder loader : Spork.getMaps()) {
			rows.add(loader.getDescription());
		}

		int results = 8;

		PaginatedResult result = new PaginatedResult(header, rows, results, true);
		result.display(sender, cmd.getInteger(0, 1));
	}

}
