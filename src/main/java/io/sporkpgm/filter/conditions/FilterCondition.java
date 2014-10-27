package io.sporkpgm.filter.conditions;

import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.map.SporkMap;

import static io.sporkpgm.filter.other.State.ABSTAIN;

public class FilterCondition extends Filter {

	String title;
	Filter filter;

	public FilterCondition(String title, State state) {
		super("search-" + title, state);

		this.title = title;
	}

	public State filter(Context context) {
		boolean fetch = fetch();
		if(!fetch) {
			return ABSTAIN;
		}

		return filter.result(context);
	}

	public boolean fetch() {
		if(filter != null) {
			return true;
		}

		SporkMap map = SporkMap.getMap();
		this.filter = map.getFilter(title);

		if(filter != null) {
			return true;
		}

		return false;
	}

}
