package io.sporkpgm.match.event;

import io.sporkpgm.match.Match;
import io.sporkpgm.match.phase.ServerPhase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchPhaseChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	Match match;
	ServerPhase from;
	ServerPhase to;

	public MatchPhaseChangeEvent(Match match, ServerPhase from, ServerPhase to) {
		this.match = match;
		this.from = from;
		this.to = to;
	}

	public Match getMatch() {
		return match;
	}

	public ServerPhase getFrom() {
		return from;
	}

	public ServerPhase getTo() {
		return to;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
