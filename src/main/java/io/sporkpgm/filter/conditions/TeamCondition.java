package io.sporkpgm.filter.conditions;

import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;

import static io.sporkpgm.filter.other.State.*;

public class TeamCondition extends Filter {

	SporkTeam team;

	public TeamCondition(String name, State state) {
		super(name, state);
	}

	public TeamCondition(String name, State state, SporkTeam team) {
		super(name, state);
		this.team = team;
	}

	public State filter(Context context) {
		if(!context.hasPlayer()) {
			return ABSTAIN;
		}

		SporkPlayer player = context.getPlayer();
		if(team == null) {
			return DENY;
		} else if(!player.getTeam().equals(team)) {
			return DENY;
		} else if(player.getTeam().equals(team)) {
			return ALLOW;
		}

		return ABSTAIN;
	}

}