package io.sporkpgm.player.rank;

import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.util.Chars;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Rank {

	static List<Rank> ranks = new ArrayList<>();

	String prefix;
	String name;
	int priority;
	boolean show;

	List<Permission> perms;

	public Rank(String prefix, String name, int priority, boolean show, List<Permission> perms) {
		this.prefix = prefix;
		this.name = name;
		this.priority = priority;
		this.perms = perms;
		this.show = show;
	}

	public Rank(ChatColor pre_color, String prefix, String name, int priority, boolean show, List<Permission> perms) {
		this(pre_color + prefix, name, priority, show, perms);
	}

	public Rank(ChatColor pre_color, String prefix, String name, int priority, boolean show) {
		this(pre_color + prefix, name, priority, show, new ArrayList<Permission>());
	}

	public Rank(ChatColor pre_color, Chars character, String name, int priority, boolean show, List<Permission> perms) {
		this(pre_color, character + "", name, priority, show, perms);
	}

	public Rank(ChatColor pre_color, Chars character, String name, int priority, boolean show) {
		this(pre_color, character + "", name, priority, show, new ArrayList<Permission>());
	}

	public String getPrefix() {
		return show ? prefix : "";
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isShown() {
		return show;
	}

	public boolean add() {
		if(ranks.contains(this)) {
			return false;
		}

		ranks.add(this);
		return true;
	}

	public boolean remove() {
		if(!ranks.contains(this)) {
			return false;
		}

		ranks.remove(this);
		return true;
	}

	public List<Permission> getPermissions() {
		return perms;
	}

	public boolean addPermission(Permission permission) {
		if(getPermissions().contains(permission)) {
			return false;
		}

		// AthenaManager.getLog().info("Added Permission '" + permission.getPermission() + "' to '" + getName() + "'");
		getPermissions().add(permission);
		for(SporkPlayer player : getPlayers()) {
			player.addPermission(permission);
		}

		return true;
	}

	public boolean removePermission(Permission permission) {
		if(!getPermissions().contains(permission)) {
			return false;
		}

		// AthenaManager.getLog().info("Removed Permission '" + permission.getPermission() + "' from '" + getName() + "'");
		getPermissions().remove(permission);
		return true;
	}

	public List<SporkPlayer> getPlayers() {
		List<SporkPlayer> players = new ArrayList<>();

		for(SporkPlayer player : SporkPlayer.getPlayers()) {
			if(player.hasRank(this)) {
				players.add(player);
			}
		}

		return players;
	}

	public boolean hasPermission(String name) {
		for(Permission perm : getPermissions()) {
			if(perm.getPermission().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	public static List<SporkPlayer> getPlayers(Rank... ranks) {
		List<SporkPlayer> players = new ArrayList<>();

		for(Rank rank : ranks) {
			for(SporkPlayer player : SporkPlayer.getPlayers()) {
				if(player.hasRank(rank) && !players.contains(player)) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public static List<Rank> getRanks(int priority) {
		List<Rank> ranks = new ArrayList<>();

		for(Rank rank : Rank.ranks) {
			if(rank.getPriority() >= priority) {
				ranks.add(rank);
			}
		}

		return ranks;
	}

	public static Rank getRank(String name) {
		for(Rank rank : ranks) {
			if(rank.getName().equalsIgnoreCase(name)) {
				return rank;
			}
		}

		for(Rank rank : ranks) {
			if(rank.getName().toLowerCase().contains(name.toLowerCase())) {
				return rank;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "Rank{prefix=" + prefix + ", name=" + name + ", priority=" + priority + ", show=" + show + ", permissions=" + perms.size() + "}";
	}
}
