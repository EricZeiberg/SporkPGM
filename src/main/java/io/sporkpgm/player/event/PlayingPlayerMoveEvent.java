package io.sporkpgm.player.event;

import io.sporkpgm.player.SporkPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayingPlayerMoveEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	boolean cancelled;
	Event event;
	SporkPlayer player;

	Location from;
	Location to;

	public PlayingPlayerMoveEvent(Event event, SporkPlayer player, Location from, Location to) {
		this.event = event;
		this.player = player;
		this.from = from;
		this.to = to;
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

	public Event getEvent() {
		return event;
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	public void cancel() {
		if(!(getEvent() instanceof PlayerMoveEvent)) {
			return;
		}

		PlayerMoveEvent event = (PlayerMoveEvent) getEvent();
		event.getPlayer().teleport(event.getFrom());
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
