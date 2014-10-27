package io.sporkpgm.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.sporkpgm.Spork;
import io.sporkpgm.map.MapBuilder;
import io.sporkpgm.match.Match;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.util.PaginatedResult;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RotationCommands {

	@Command(aliases = {"rotation", "rot"}, desc = "View the current rotation", usage = "[page]", max = 1)
	public static void rotation(CommandContext cmd, CommandSender sender) throws CommandException {
		String bar = ChatColor.RED + "" + ChatColor.STRIKETHROUGH + " ----------- ";
		String loaded = ChatColor.DARK_AQUA + "Rotation (" + ChatColor.AQUA + "[page]" + ChatColor.DARK_AQUA + " of "
				+ ChatColor.AQUA + "[pages]" + ChatColor.DARK_AQUA + ")";

		String header = bar + loaded + bar;
		List<String> rows = new ArrayList<>();
		for(RotationSlot slot : Spork.get().getRotation().getCurrentList()) {
			rows.add(ChatColor.GOLD + slot.getLoader().getName() + " " + ChatColor.DARK_PURPLE + "by " + StringUtil
					.listToEnglishCompound(slot.getLoader().getAuthorNames(), ChatColor.RED.toString(), ChatColor.DARK_PURPLE.toString()));
		}

		int results = 8;

		PaginatedResult result = new PaginatedResult(header, rows, results, true);
		result.display(sender, cmd.getInteger(0, 1));
	}

	@Command(aliases = {"setnext", "sn"}, desc = "Set the next map", usage = "[map]", min = 1)
	@CommandPermissions("spork.match.setnext")
	public static void setnext(CommandContext cmd, CommandSender sender) throws CommandException {
		Rotation rotation = RotationSlot.getRotation();
		RotationSlot slot = rotation.getCurrentSlot();
		Match match = slot.getMatch();

		MapBuilder map = Spork.get().getMap(cmd.getJoinedStrings(0));
		if(map == null) {
			sender.sendMessage(ChatColor.RED + "Could not find a map by that name");
			return;
		}

		rotation.setNext(map);
		Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + ChatColor.DARK_PURPLE + " set the next map to " + ChatColor.GOLD + map.getName());
	}

}
