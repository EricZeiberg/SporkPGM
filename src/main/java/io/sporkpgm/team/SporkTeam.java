package io.sporkpgm.team;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.SporkMap.ScoreAPI;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.objective.scored.ScoredObjective;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.spawns.SporkSpawn;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class SporkTeam {

	SporkMap map;
	Team team;
	ScoredObjective scored;

	Team scoreboard;
	String player;

	String name;
	ChatColor color;
	ChatColor overhead;
	int max;
	int overfill;
	boolean observers;
	String display;
	boolean capped;
	boolean closed;
	boolean ready;
	List<SporkSpawn> spawns;

	SporkTeam(SporkMap map, String name, ChatColor color) {
		this(map, name, color, color, 0, 0, true);
	}

	SporkTeam(SporkMap map, String name, ChatColor color, int max) {
		this(map, name, color, color, max, max + max / 4, false);
	}

	SporkTeam(SporkMap map, String name, ChatColor color, int max, int overfill) {
		this(map, name, color, color, max, overfill, false);
	}

	SporkTeam(SporkMap map, String name, ChatColor color, ChatColor overhead, int max, int overfill, boolean observers) {
		this.map = map;
		this.name = name;
		this.color = color;
		this.overhead = overhead;
		this.max = max;
		this.overfill = overfill;
		this.observers = observers;
		this.capped = true;
		this.closed = false;
		this.spawns = new ArrayList<>();
		this.team = map.getScoreboard().registerNewTeam(name);
		this.team.setPrefix(getColor().toString());
		this.team.setDisplayName(getColoredName());
		this.team.setCanSeeFriendlyInvisibles(true);

		name();
	}

	public SporkMap getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		update();
	}

	public ChatColor getColor() {
		return color;
	}

	public ChatColor getOverhead() {
		return overhead;
	}

	public int getMax() {
		return max;
	}

	public int getOverfill() {
		return overfill;
	}

	public boolean isObservers() {
		return observers;
	}

	public boolean isReady() {
		return ready;
	}

	public boolean isCapped() {
		return capped;
	}

	public void setCapped(boolean capped) {
		this.capped = capped;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getColoredName() {
		return getColor() + getName();
	}

	public Team getTeam() {
		return team;
	}

	public String getPlayer() {
		return player;
	}

	public ScoredObjective getScored() {
		return scored;
	}

	public void setScored(ScoredObjective objective) {
		this.scored = objective;
	}

	public void setReady(boolean value) {
		ready = value;
	}

	public List<SporkSpawn> getSpawns() {
		return spawns;
	}

	public Location getSpawn() {
		return spawns.get(NumberUtil.getRandom(0, spawns.size() - 1)).getSpawn();
	}

	public SporkSpawn getSporkSpawn() {
		return spawns.get(NumberUtil.getRandom(0, spawns.size() - 1));
	}

	public SporkTeam getOpposite() {
		for(SporkTeam spork : map.getTeams()) {
			if(!spork.equals(this) && !spork.isObservers()) {
				return spork;
			}
		}

		return null;
	}

	public boolean canJoin(SporkPlayer player) {
		if(isObservers()) {
			return true;
		}

		if(isClosed()) {
			return false;
		}

		/*
		if(size() >= getOverfill() && isCapped()) {
			return player.getPlayer().hasPermission("manager.join.overflow");
		} else if(size() >= getMax() && isCapped()) {
			return player.getPlayer().hasPermission("manager.join.max");
		}
		*/

		return true;
	}

	public String reasonJoin(SporkPlayer player, ChatColor colour) {
		/*
		if(size() >= getOverfill() && isCapped() && !player.getPlayer().hasPermission("manager.join.overflow")) {
			return colour + "You can't join " + getColoredName() + colour + " because it has reached overflow capacity.";
		} else if(size() >= getMax() && isCapped() && !player.getPlayer().hasPermission("manager.join.max")) {
			return colour + "The teams on this map are full!";
		}
		*/

		return "";
	}

	public List<SporkPlayer> getPlayers() {
		List<SporkPlayer> players = new ArrayList<>();
		for(SporkPlayer player : SporkPlayer.getPlayers()) {
			if(player.getTeam() == this) {
				players.add(player);
			}
		}

		return players;
	}

	public int size() {
		return getPlayers().size();
	}

	public List<ObjectiveModule> getObjectives() {
		// Log.info(map.getName() + ": " + map.getObjectives().size() + " Objectives");

		List<ObjectiveModule> objectives = new ArrayList<>();
		for(ObjectiveModule objective : map.getObjectives())
			if(objective.getTeam() == this)
				objectives.add(objective);
		return objectives;
	}

	public boolean complete() {
		boolean yes = true;
		for(ObjectiveModule module : getObjectives())
			if(!module.isComplete()) {
				yes = false;
				break;
			}

		return yes;
	}

	public void update() {
		Objective objective = team.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		Score score = objective.getScore(player);
		int value = score.getScore();

		name();

		Score newScore = objective.getScore(player);
		ScoreAPI.reset(score);
		newScore.setScore(value);
	}

	public void name() {
		String original = getColoredName();
		String sb = getName();

		String prefix = "";
		String title = getColoredName();
		String suffix = "";

		if(sb.length() > 4) {
			sb = sb.substring(0, 4);
		}

		if(title.length() > 32) {
			prefix = original.substring(0, 16);
			title = original.substring(16, 32);
			Log.info(original + ": " + original.length());
			suffix = original.substring(32, (original.length() > 48 ? 48 : original.length()));
		} else if(title.length() > 16) {
			prefix = original.substring(0, 16);
			title = original.substring(16, original.length());
		}

		player = title;

		if(scoreboard == null) {
			scoreboard = map.getScoreboard().registerNewTeam(sb + "-obj");
		}

		scoreboard.setPrefix(prefix);
		scoreboard.setDisplayName(title);
		scoreboard.setSuffix(suffix);

		if(!scoreboard.has(player)) {
			scoreboard.add(player);
		}
	}

	@Override
	public String toString() {
		return "SporkTeam{name=" + name + ",color=" + color.name() + ",overhead=" + overhead.name() + "," +
				"max=" + max + ",overfill=" + overfill + ",size=" + size() + "}";
	}

}
