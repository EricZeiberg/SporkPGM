package io.sporkpgm.filter.conditions;

import com.google.common.collect.Lists;
import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.Modifier;
import io.sporkpgm.filter.other.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiCondition extends Filter {

	private Modifier modifier;
	private List<Filter> filters;

	public MultiCondition(String name, State state, Modifier modifier, Filter... filters) {
		super(name, state);
		this.modifier = modifier;
		this.filters = Lists.newArrayList(filters);
	}

	private Map<State, Integer> usages(Context context) {
		Map<State, Integer> usages = new HashMap<>();
		for(State state : State.values()) {
			usages.put(state, 0);
		}

		for(Filter filter : filters) {
			State state = filter.result(context);
			int value = usages.get(state);
			value++;
			usages.remove(state);
			usages.put(state, value);
		}

		return usages;
	}

	private List<State> states(Context context) {
		List<State> states = new ArrayList<>();

		for(Filter filter : filters) {
			states.add(filter.result(context));
		}

		return states;
	}

	public State filter(Context context) {
		if(modifier == Modifier.ANY) {
			List<State> states = states(context);

			if(states.contains(State.ALLOW)) {
				return State.ALLOW;
			} else if(!states.contains(State.ALLOW) && !states.contains(State.DENY)) {
				return State.ABSTAIN;
			} else {
				return State.DENY;
			}
		} else if(modifier == Modifier.NOT) {
			return filters.get(0).result(context).reverse();
		} else if(modifier == Modifier.ONE) {
			Map<State, Integer> usages = usages(context);

			if(usages.get(State.ALLOW) == 1) {
				return State.ALLOW;
			} else if(usages.get(State.ALLOW) > 1 || usages.get(State.DENY) > 0) {
				return State.DENY;
			} else {
				return State.ABSTAIN;
			}
		} else if(modifier == Modifier.ALL) {
			Map<State, Integer> usages = usages(context);

			if(usages.get(State.ALLOW) == filters.size()) {
				return State.ALLOW;
			} else if(usages.get(State.DENY) > 0) {
				return State.DENY;
			} else {
				return State.ABSTAIN;
			}
		}

		return State.ABSTAIN;
	}

}