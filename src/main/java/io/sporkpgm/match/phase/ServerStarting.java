package io.sporkpgm.match.phase;

import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.List;

public class ServerStarting extends ServerPhase {

	public ServerStarting(Match match, MatchPhase phase) {
		this.match = match;
		this.phase = phase;
		this.duration = phase.getDuration() * 20;
	}

	@Override
	public void run() {
		if(complete)
			return;
		List<SporkPlayer> players = match.getMap().getPlayers();
		if(players.size() < 2) {
			//match.setPhase(MatchPhase.WAITING);
			//return;
		}

		if(duration <= 0) {
			broadcast(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # # ");
			broadcast(ChatColor.DARK_PURPLE + "# # " + ChatColor.GREEN + "The match has started!" + ChatColor.DARK_PURPLE + " # #");
			broadcast(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # #");
			match.setPhase(MatchPhase.PLAYING);

			for(SporkTeam team : match.getMap().getTeams())
				for(SporkPlayer player : team.getPlayers())
					player.setTeam(team, false, true, true);
			match.getMap().start();

			complete = true;
			return;
		}

		boolean show = false;
		if(getTicks() % 20 == 0) {
			if(getSeconds() % 30 == 0)
				show = true;
			else if(getSeconds() < 30 && getSeconds() % 15 == 0)
				show = true;
			else if(getSeconds() < 15 && getSeconds() % 5 == 0)
				show = true;
			else if(getSeconds() < 5 || getSeconds() == 0)
				show = true;
		}

		if(isFullSecond() && getSeconds() <= 3) {
			for(SporkPlayer player : match.getMap().getPlayers()) {
				Sound sound = Sound.NOTE_BASS;
				if(getSeconds() == 0) {
					sound = Sound.NOTE_PLING;
				}
				player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1, 1);
			}
		}

		if(show)
			broadcast(getMessage());
		setTicks(getTicks() - 1);
	}

	@Override
	public String getMessage() {
		return ChatColor.GREEN + "Match starting in " + ChatColor.RED + getSeconds() + ChatColor.GREEN + " second" + (getSeconds() != 0 ? "s" : "") + "!";
	}

}
