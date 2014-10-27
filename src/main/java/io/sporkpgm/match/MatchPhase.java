package io.sporkpgm.match;

import io.sporkpgm.match.phase.ServerCycling;
import io.sporkpgm.match.phase.ServerPhase;
import io.sporkpgm.match.phase.ServerPlaying;
import io.sporkpgm.match.phase.ServerStarting;
import io.sporkpgm.match.phase.ServerWaiting;

public enum MatchPhase {

	WAITING(0),
	STARTING(1, 30),
	PLAYING(2),
	CYCLING(3, 30);

	int duration;
	int index;

	MatchPhase(int index) {
		this(index, -1);
	}

	MatchPhase(int index, int duration) {
		this.index = index;
		this.duration = duration;
	}

	public MatchPhase getNextPhase() {
		if((values().length - 1) == index)
			return values()[0];

		return values()[index + 1];
	}

	public int getDuration() {
		return duration;
	}

	public boolean hasDuration() {
		return duration > 0;
	}

	public ServerPhase getPhase(Match match) {
		if(this == STARTING)
			return new ServerStarting(match, this);
		else if(this == PLAYING)
			return new ServerPlaying(match, this);
		else if(this == CYCLING)
			return new ServerCycling(match, this);
		return new ServerWaiting(match, this);
	}

	public String toString() {
		switch(this) {
			case WAITING:
				return "Waiting";
			case STARTING:
				return "Starting";
			case PLAYING:
				return "Playing";
			case CYCLING:
				return "Cycling";
			default:
				return "";
		}
	}

}
