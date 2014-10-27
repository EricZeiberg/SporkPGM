package io.sporkpgm.player.event;

import com.google.common.base.Preconditions;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinTeamEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private SporkPlayer player;
	private SporkTeam team;
	private String reason = "No reason";
	private boolean cancelled = false;

	public PlayerJoinTeamEvent(SporkPlayer player, SporkTeam team) {
		this.player = player;
		this.team = team;
	}

	public PlayerJoinTeamEvent(Player player, SporkTeam team) throws NullArgumentException {
		this.player = Preconditions.checkNotNull(SporkPlayer.getPlayer(player), "Player cannot be null");
		this.team = team;
	}

	public void setCancelled(boolean cancelled, String reason) {
		setCancelled(cancelled);
		setReason(reason);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public SporkTeam getTeam() {
		return team;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
