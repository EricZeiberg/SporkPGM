package io.sporkpgm.filter.conditions;

import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.map.event.BlockChangeEvent;
import org.bukkit.Material;

import static io.sporkpgm.filter.other.State.*;

public class BlockCondition extends Filter {

	Material material;

	public BlockCondition(String name, State state) {
		super(name, state);
	}

	public BlockCondition(String name, State state, Material material) {
		super(name, state);
		this.material = material;
	}

	public State filter(Context context) {
		if(!context.hasTransformation() && !context.hasBlock()) {
			return ABSTAIN;
		}

		BlockChangeEvent event = (context.hasBlock() ? context.getBlock() : context.getTransformation());
		boolean match = event.getNewState().getType().equals(material);

		if(material == null) {
			return DENY;
		} else if(!match) {
			return DENY;
		} else if(match) {
			return ALLOW;
		}

		return ABSTAIN;
	}

}