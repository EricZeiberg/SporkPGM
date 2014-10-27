package io.sporkpgm.match.phase;

import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.player.SporkPlayer;

import java.util.List;

public class ServerWaiting extends ServerPhase {

	public ServerWaiting(Match match, MatchPhase phase) {
		this.match = match;
		this.phase = phase;
	}

	@Override
	public void run() {
		if(complete)
			return;
		List<SporkPlayer> players = match.getMap().getPlayers();
		if(players.size() < 2 && !match.isForced())
			return;

		match.setPhase(getPhase().getNextPhase());
		complete = true;
	}

}
