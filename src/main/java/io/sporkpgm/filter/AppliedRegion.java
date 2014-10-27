package io.sporkpgm.filter;

import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.player.event.PlayingPlayerMoveEvent;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.groups.UnionRegion;
import io.sporkpgm.team.spawns.kits.SporkKit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppliedRegion extends UnionRegion {

	private Map<AppliedValue, Object> values;
	private List<Filter> filters;

	public AppliedRegion(String name, Map<AppliedValue, Object> values, List<Region> regions, List<Filter> filters) {
		super("filtered-" + name + "-union", regions);
		this.values = values;
		this.filters = filters;
	}

	public void apply(Context context) {
		apply(context, false);
	}

	public void apply(Context context, boolean log) {
		List<AppliedValue> applied = new ArrayList<>();
		if(context.hasMovement()) {
			PlayingPlayerMoveEvent move = context.getMovement();
			if(isInside(move.getFrom(), log) && isInside(move.getTo(), log)) {
				return;
			} else if(!isInside(move.getFrom(), log) && !isInside(move.getTo(), log)) {
				return;
			} else if(!isInside(move.getFrom(), log) && isInside(move.getTo(), log)) {
				applied.add(AppliedValue.ENTER);
				applied.add(AppliedValue.KIT);
				applied.add(AppliedValue.VELOCITY);
			} else if(isInside(move.getFrom(), log) && !isInside(move.getTo(), log)) {
				applied.add(AppliedValue.LEAVE);
			}
		}

		if(context.hasModification()) {
			BlockChangeEvent block = context.getModification();

			if(isInside(block.getLocation(), log)) {
				applied.add(AppliedValue.BLOCK);
				if(block.isPlace()) {
					applied.add(AppliedValue.BLOCK_PLACE);
				} else if(block.isBreak()) {
					applied.add(AppliedValue.BLOCK_BREAK);
				}
			}
		}

		/*
		if(applied.size() > 0) {
			Log.info(getName() + ": " + applied);
		}
		*/

		String message = null;
		if(hasValue(AppliedValue.MESSAGE)) {
			message = (String) values.get(AppliedValue.MESSAGE);
			message = ChatColor.RED + message.replace("`", "§").replace("&", "§");
		}

		for(AppliedValue value : AppliedValue.getValues(Filter.class)) {
			handle(message, context, applied, value);
		}

		if(applied.contains(AppliedValue.KIT)) {
			if(hasValue(AppliedValue.KIT)) {
				SporkKit sporkKit = (SporkKit) values.get(AppliedValue.KIT);
				sporkKit.apply(context.getPlayer());
			}
		}

		// TODO: add support for velocities
	}

	public void handle(String message, Context context, List<AppliedValue> applied, AppliedValue value) {
		if(applied.contains(value) && hasValue(value)) {
			Filter filter = (Filter) getValue(value);
			if(filter.result(context) == State.DENY) {
				boolean ignore = false;
				if(context.hasModification()) {
					BlockChangeEvent change = context.getModification();
					if(change.isLocked()) {
						ignore = true;
					}
				}

				if(!ignore) {
					context.deny();
					if(message != null && !context.isMessaged() && context.getPlayer() != null) {
						context.setMessaged(true);
						context.getPlayer().getPlayer().sendMessage(message);
					}
				}
			}
		}
	}

	public Map<AppliedValue, Object> getHashMap() {
		return values;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public boolean hasValue(AppliedValue value) {
		return getValue(value) != null;
	}

	public Object getValue(AppliedValue value) {
		return values.get(value);
	}

	@Override
	public String toString() {
		return "AppliedRegion{name=" + getName() + ",values=" + values + "}";
	}

	public enum AppliedValue {

		ENTER("enter", Filter.class),
		LEAVE("leave", Filter.class),
		BLOCK("block", Filter.class),
		BLOCK_PLACE("block-place", Filter.class),
		BLOCK_BREAK("block-break", Filter.class),
		USE("use", Filter.class),
		KIT("kit", SporkKit.class),
		MESSAGE("message", String.class),
		VELOCITY("velocity", String.class),
		UNKNOWN("unknown", Object.class);

		private String attribute;
		private Class<?> returns;

		AppliedValue(String attribute, Class<?> returns) {
			this.attribute = attribute;
			this.returns = returns;
		}

		public String getAttribute() {
			return attribute;
		}

		public Class<?> getReturns() {
			return returns;
		}

		public static String[] getAttributes() {
			String[] attributes = new String[values().length];
			for(int i = 0; i < values().length; i++) {
				attributes[i] = values()[i].name();
			}
			return attributes;
		}

		public static List<AppliedValue> getValues(Class<?> returns) {
			List<AppliedValue> values = new ArrayList<>();
			for(AppliedValue value : values()) {
				if(value.getReturns() == returns) {
					values.add(value);
				}
			}
			return values;
		}

	}

}