package io.sporkpgm.player.event;

import io.sporkpgm.player.SporkPlayer;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAddEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	SporkPlayer player;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public PlayerAddEvent(SporkPlayer player) {
		this(player.getPlayer());
	}

	public PlayerAddEvent(Player player) throws NullArgumentException {
		this.player = SporkPlayer.getPlayer(player);

		if(this.player == null) {
			throw new NullArgumentException("player");
		}
	}

	public SporkPlayer getPlayer() {
		return player;
	}

}
