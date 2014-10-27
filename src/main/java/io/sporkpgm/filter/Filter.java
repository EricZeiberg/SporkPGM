package io.sporkpgm.filter;

import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;

import static io.sporkpgm.filter.other.State.*;

public abstract class Filter {

	protected String name;
	protected State state;

	protected Filter(String name, State state) {
		this.name = name;
		this.state = state;
	}

	protected abstract State filter(Context context);

	public State result(Context context) {
		if(state == DENY) {
			return filter(context).reverse();
		} else if(state == ALLOW) {
			return filter(context);
		}

		return ABSTAIN;
	}

	public String getName() {
		return name;
	}

	public State getState() {
		return state;
	}

}
