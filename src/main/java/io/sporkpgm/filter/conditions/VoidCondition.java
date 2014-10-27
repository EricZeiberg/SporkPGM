package io.sporkpgm.filter.conditions;

import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.region.types.VoidRegion;
import org.bukkit.Material;

import static io.sporkpgm.filter.other.State.*;

public class VoidCondition extends Filter {

	VoidRegion region;

	public VoidCondition(String name, State state) {
		super(name, state);
		this.region = new VoidRegion();
	}

	public State filter(Context context) {
		if(!context.hasTransformation() && !context.hasBlock()) {
			return ABSTAIN;
		}

		BlockChangeEvent event = (context.hasBlock() ? context.getBlock() : context.getTransformation());
		boolean inside = region.isInside(event.getRegion());

		if(!inside) {
			return DENY;
		} else if(inside) {
			return ALLOW;
		}

		return ABSTAIN;
	}

}