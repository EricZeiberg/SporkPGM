package io.sporkpgm.listeners;


import io.sporkpgm.Spork;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.player.rank.Rank;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.Chars;
import io.sporkpgm.util.SchedulerUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ConnectionListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final SporkPlayer player = SporkPlayer.getPlayer(event.getPlayer());
		final SporkTeam obs = Spork.get().getRotation().getCurrent().getObservers();

		Rank admin = Rank.getRank("Administrator");
		if(player.getPlayer().isOp() && !player.hasRank(admin)) {
			player.addRank(admin);
		}

		for(Rank rank : Spork.get().getRanks(player)) {
			player.addRank(rank);
		}
		player.setTeam(obs, false, false, false);

		new SchedulerUtil(new Runnable() {

			@Override
			public void run() {
				player.setTeam(obs, false, true, true);
				player.updateInventory();
			}

		}, false).delay(1);

		event.setJoinMessage(player.getFullName() + ChatColor.YELLOW + " joined the game");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		SporkPlayer player = SporkPlayer.getPlayer(event.getPlayer());
		event.setQuitMessage(player.getFullName() + ChatColor.YELLOW + " left the game");

		SporkPlayer.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		SporkPlayer player = SporkPlayer.getPlayer(event.getPlayer());
		event.setLeaveMessage(player.getFullName() + ChatColor.YELLOW + " left the game");

		SporkPlayer.remove(event.getPlayer());
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		MatchPhase phase = RotationSlot.getRotation().getCurrentMatch().getPhase();
		ChatColor status = null;
		if(phase == MatchPhase.WAITING) {
			status = ChatColor.GRAY;
		} else if(phase == MatchPhase.STARTING) {
			status = ChatColor.GREEN;
		} else if(phase == MatchPhase.PLAYING) {
			status = ChatColor.GOLD;
		} else if(phase == MatchPhase.CYCLING) {
			status = ChatColor.RED;
		}
		event.setMotd(status + "" + Chars.RAQUO + "" + ChatColor.AQUA + " " + RotationSlot.getRotation().getCurrent().getName() + " " + status + Chars.LAQUO);
	}

}
