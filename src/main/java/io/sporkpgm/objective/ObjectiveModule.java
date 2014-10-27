package io.sporkpgm.objective;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.team.SporkTeam;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("ALL")
public abstract class ObjectiveModule extends Module {

	protected String name;
	protected StringBuilder spaces;
	protected SporkTeam team;
	protected String player;

	public ObjectiveModule(String name, SporkTeam team) {
		this.name = name;
		this.team = team;
		update();
	}

	public String getName() {
		return name;
	}

	public SporkTeam getTeam() {
		return team;
	}

	public abstract boolean isComplete();

	public abstract void setComplete(boolean complete);

	public abstract String getPlayer();

	public void update() {
		int score = 0;
		if(this.player != null) {
			score = team.getMap().getObjective().getScore(player).getScore();
			SporkMap.ScoreAPI.reset(team.getMap().getObjective().getScore(player));
		}

		this.spaces = new StringBuilder();

		String title = getStatusColour() + name + spaces.toString();
		if(title.length() > 16) {
			title = title.substring(0, 16);
		}
		this.player = title;

		while(SporkMap.ScoreAPI.isSet(team.getMap().getObjective().getScore(player))) {
			spaces.append(" ");

			title = getStatusColour() + name + spaces.toString();
			if(title.length() > 16) {
				String coloured = (getStatusColour() + name).substring(0, 15 - spaces.length());
				title = coloured + spaces.toString();
			}

			player = title;
		}

		team.getMap().getObjective().getScore(player).setScore(score);
	}

	public abstract ChatColor getStatusColour();

}
