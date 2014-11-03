package io.sporkpgm.player.event;

import io.sporkpgm.player.SporkPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDealPlayerDamageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	boolean cancelled;
	Event event;
	SporkPlayer player;

	private SporkPlayer victim;

	public PlayerDealPlayerDamageEvent(Event event, SporkPlayer player, SporkPlayer victim) {
		this.event = event;
		this.player = player;
		this.victim = victim;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		if(event instanceof Cancellable) {
			((Cancellable) event).setCancelled(cancelled);
			return;
		}
	}

	public SporkPlayer getVictim() {
		return victim;
	}

    public SporkPlayer getPlayer() {return player; }

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
