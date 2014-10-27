package io.sporkpgm.match.event;

import io.sporkpgm.match.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	Match match;

	public MatchEndEvent(Match match) {
		this.match = match;
	}

	public Match getMatch() {
		return match;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
