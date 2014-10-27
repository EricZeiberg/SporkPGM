package io.sporkpgm.player.event;

import io.sporkpgm.player.SporkPlayer;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChatEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	SporkPlayer player;
	String message;
	boolean team;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public PlayerChatEvent(SporkPlayer player, String message, boolean team) {
		this(player.getPlayer(), message, team);
	}

	public PlayerChatEvent(Player player, String message, boolean team) throws NullArgumentException {
		this.player = SporkPlayer.getPlayer(player);
		this.message = message;
		this.team = team;
		if(this.player == null)
			throw new NullArgumentException("player");
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public boolean isTeam() {
		return team;
	}

}
