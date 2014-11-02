package io.sporkpgm.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.objective.scored.ScoredObjective;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.player.event.PlayerChatEvent;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.SchedulerUtil;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

public class MatchCommands {

	@Command(aliases = {"join"}, desc = "Join the match", usage = "[team]", max = 1)
	public static void join(CommandContext cmd, CommandSender sender) throws CommandException {
		if(!(sender instanceof Player))
			throw new CommandException("Only Players can use this command");

		SporkMap map = RotationSlot.getRotation().getCurrent();
		SporkPlayer player = SporkPlayer.getPlayer((Player) sender);
        if (map.hasEnded()){
            sender.sendMessage(ChatColor.RED + "The match has ended. Please wait for the server to cycle");
            return;
        }
		if(cmd.argsLength() == 1) {
			List<SporkTeam> teams = map.getTeams(cmd.getString(0));
			if(teams.size() > 1) {
				List<String> names = new ArrayList<>();
				for(SporkTeam team : teams) {
					names.add(team.getColoredName());
				}
				sender.sendMessage(ChatColor.RED + "Too many teams matched that query!");
				sender.sendMessage(ChatColor.GRAY + "Returned: " + StringUtil.listToEnglishCompound(names, "", ChatColor.GRAY.toString()));
				return;
			} else if(teams.size() == 0) {
				throw new CommandException("No teams matched query");
			}

			SporkTeam team = teams.get(0);
			/*
			if(!team.isObservers()) {
				if(!player.hasPermission("spork.team.select")) {
					throw new CommandPermissionsException();
				}
			}
			*/

			if(!team.canJoin(player)) {
				team.reasonJoin(player, ChatColor.GRAY);
				return;
			}

			player.setTeam(team);
			return;
		}

		SporkTeam lowest = map.getLowestTeam();
		if(!lowest.canJoin(player)) {
			player.getPlayer().sendMessage(lowest.reasonJoin(player, ChatColor.GRAY));
			return;
		}

		player.setTeam(lowest);
	}

	@Command(aliases = {"match", "matchinfo"}, desc = "View match information")
	public static void match(CommandContext cmd, CommandSender sender) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("Only Players can use this command");
		}

		Match match = Spork.get().getMatch();
		String bar = RED + "" + STRIKETHROUGH + "-----------";
		sender.sendMessage(" " + bar + DARK_AQUA + " Match Info " + GRAY + "(" + match.getID() + ") " + bar + " ");
		if(match.isRunning()) {
			String time = match.getMatchTime();
			sender.sendMessage(DARK_PURPLE + "Time: " + ChatColor.GOLD + (time.length() == 2 ? "00:" : "") + time);
		}

		SporkMap map = match.getMap();
		StringBuilder builder = new StringBuilder();
		for(SporkTeam team : map.getTeams()) {
			builder.append(team.getColoredName() + GRAY + ": " + WHITE + "" + team.getPlayers().size() + GRAY + "/" + team.getMax() + AQUA + " | ");
		}

		SporkTeam obs = map.getObservers();
		builder.append(obs.getColoredName() + GRAY + ": " + WHITE + "" + obs.getPlayers().size());
		sender.sendMessage(builder.toString());

		if(!map.hasModule(ScoredObjective.class)) {
			sender.sendMessage(RED + "---- Goals ----");
			for(SporkTeam team : map.getTeams()) {
				StringBuilder objectives = new StringBuilder();
				objectives.append(team.getColoredName() + GRAY + ":");
				for(ObjectiveModule module : team.getObjectives()) {
					objectives.append(" " + module.getStatusColour() + module.getName());
				}
				sender.sendMessage(objectives.toString());
			}
		}

		if(!match.isRunning()) {
			match.getMessage();
		}
	}

	@Command(aliases = {"g", "!", "all"}, desc = "Global chat", usage = "[message]", min = 1)
	public static void global(CommandContext cmd, CommandSender sender) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("Only player can use global chat");
		}

		SporkPlayer player = SporkPlayer.getPlayer((Player) sender);
		PlayerChatEvent pce = new PlayerChatEvent(player, cmd.getJoinedStrings(0), false);
		Spork.callEvent(pce);
	}

	@Command(aliases = {"start"}, desc = "Start the map with the specified countdown", usage = "[time]", min = 1, max = 1)
	@CommandPermissions("spork.match.start")
	public static void start(CommandContext cmd, CommandSender sender) throws CommandException {
		Match match = RotationSlot.getRotation().getCurrentMatch();
		MatchPhase phase = match.getPhase();
		if(phase == MatchPhase.WAITING) {
			match.setPhase(MatchPhase.STARTING, cmd.getInteger(0), true);
			return;
		}

		if(phase == MatchPhase.STARTING) {
			match.setDuration(cmd.getInteger(0));
			match.start();
			return;
		}
		sender.sendMessage(ChatColor.RED + "Server must be waiting or starting to set start time");
	}


	@Command(aliases = {"ready"}, desc = "Set a team's readiness state", usage = "{team}", max = 1)
	@CommandPermissions("spork.match.ready")
	public static void ready(CommandContext cmd, CommandSender sender) throws CommandException {
		Match match = Spork.get().getRotation().getCurrentMatch();
		if(cmd.argsLength() == 1) {
			if(!sender.hasPermission("spork.match.ready.force"))
				throw new CommandException("You don't have permission");
			List<SporkTeam> matchingTeams = match.getMap().getTeams(cmd.getString(1));
			if(matchingTeams.size() == 0) throw new CommandException("No matching teams found");
			SporkTeam matched = matchingTeams.get(0);
			matched.setReady(true);
			Bukkit.broadcastMessage(matched.getColoredName() + ChatColor.WHITE + " has been toggled ready!");
			boolean ready = true;
			for(SporkTeam team : match.getMap().getTeams()) {
				if(!team.isReady()) ready = false;
			}
			if(ready) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "All teams are ready, starting match!");
				match.setPhase(MatchPhase.STARTING);
			}
		} else {
			if(!(sender instanceof Player)) throw new CommandException("Only a player may use this command");
			SporkTeam player_team = SporkPlayer.getPlayer((Player) sender).getTeam();
			player_team.setReady(true);
			Bukkit.broadcastMessage(player_team.getColoredName() + ChatColor.WHITE + " has been toggled ready!");
			boolean ready = true;
			for(SporkTeam team : match.getMap().getTeams()) {
				if(!team.isReady()) ready = false;
			}
			if(ready) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "All teams are ready, starting match!");
				match.setPhase(MatchPhase.STARTING);
			}
		}
	}

	@Command(aliases = {"end"}, desc = "Ends the match", max = 1)
	@CommandPermissions("spork.match.end")
	public static void end(CommandContext cmd, CommandSender sender) throws CommandException {
		Match match = RotationSlot.getRotation().getCurrentMatch();
		MatchPhase phase = match.getPhase();

		if(phase == MatchPhase.PLAYING) {
			if(cmd.argsLength() == 1) {
				List<SporkTeam> teams = match.getMap().getTeams(cmd.getString(0));
				if(teams.size() > 1) {
					List<String> names = new ArrayList<>();
					for(SporkTeam team : teams) {
						names.add(team.getColoredName());
					}
					sender.sendMessage(ChatColor.RED + "Too many teams matched that query!");
					sender.sendMessage(ChatColor.GRAY + "Returned: " + StringUtil.listToEnglishCompound(names, "", ChatColor.GRAY.toString()));
					return;
				} else if(teams.size() == 0) {
					throw new CommandException("No teams matched query");
				}

				SporkTeam team = teams.get(0);
				match.getMap().setWinner(team);
			}
			match.getMap().setEnded(true);
			return;
		}

		sender.sendMessage(ChatColor.RED + "There is no match currently playing");
	}

	@Command(aliases = {"cancel"}, desc = "Cancels all active Spork countdowns", max = 0)
	@CommandPermissions("spork.match.cancel")
	public static void cancel(CommandContext cmd, CommandSender sender) throws CommandException {
		Match match = RotationSlot.getRotation().getCurrentMatch();
		MatchPhase phase = match.getPhase();

		if(phase != MatchPhase.WAITING && phase != MatchPhase.PLAYING) {
			match.stop();
			sender.sendMessage(ChatColor.GREEN + "Countdowns cancelled");
			return;
		}

		sender.sendMessage(ChatColor.RED + "There are no countdowns while a match is waiting or playing");
	}

	@Command(aliases = {"cycle"}, desc = "Cycle the map with the specified countdown", usage = "[time]", min = 1, max = 1)
	@CommandPermissions("spork.match.cycle")
	public static void cycle(final CommandContext cmd, final CommandSender sender) throws CommandException {
		Match match = RotationSlot.getRotation().getCurrentMatch();
		MatchPhase phase = match.getPhase();

		final int duration = cmd.getInteger(0);
		int delay = 0;
		if(phase == MatchPhase.PLAYING) {
			match.getMap().setEnded(true);
			match.setDuration(duration);
			delay = 2;
		} else if(phase == MatchPhase.WAITING || phase == MatchPhase.STARTING) {
			match.stop();
			match.setPhase(MatchPhase.CYCLING, duration);
			return;
		}

		Runnable run = new Runnable() {
			@Override
			public void run() {
				Match match = RotationSlot.getRotation().getCurrentMatch();
				MatchPhase phase = match.getPhase();
				if(phase == MatchPhase.CYCLING) {
					match.setDuration(duration);
					match.start();
					return;
				}

				sender.sendMessage(ChatColor.RED + "Invalid MatchPhase? " + phase.name());
			}
		};

		new SchedulerUtil(run, false).delay(delay);
	}

}
