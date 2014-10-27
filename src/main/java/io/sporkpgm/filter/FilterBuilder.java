package io.sporkpgm.filter;

import io.sporkpgm.filter.conditions.*;
import io.sporkpgm.filter.exceptions.InvalidFilterException;
import io.sporkpgm.filter.other.Modifier;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Material;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {

	public static List<Filter> build(SporkMap map) throws InvalidFilterException {
		Document document = map.getDocument();
		Element root = document.getRootElement();
		List<Filter> filterList = new ArrayList<>();
		filterList.addAll(defaults());

		Element filters = root.element("filters");
		if(filters == null) {
			return filterList;
		}

		for(Element filter : XMLUtil.getElements(filters, "filter")) {
			filterList.add(parseFilter(filter.attributeValue("name"), filter, map));
		}

		return filterList;
	}

	public static Filter parseFilter(String name, Element element, SporkMap map) throws InvalidFilterException {
		List<Filter> filterList = new ArrayList<>();
		List<Element> conditions = XMLUtil.getElements(element);

		for(Element condition : conditions) {
			if(condition.getName().equalsIgnoreCase("allow") || condition.getName().equalsIgnoreCase("deny")) {
				condition = (Element) condition.elements().get(0);
			}

			Modifier modifier = Modifier.getModifier(condition.getName());
			if(modifier != null) {
				MultiCondition multi = new MultiCondition(null, State.DENY, modifier, parseFilter(name, condition, map));
				filterList.add(multi);
				continue;
			} else if(condition.getName().equalsIgnoreCase("filter")) {
				filterList.add(parseFilter(condition));
			} else if(condition.getName().equalsIgnoreCase("team")) {
				filterList.add(parseTeam(condition, map));
			} else if(condition.getName().equalsIgnoreCase("block")) {
				filterList.add(parseBlock(condition));
			} else if(condition.getName().equalsIgnoreCase("void")) {
				filterList.add(parseVoid(condition));
			} /* else if(condition.getName().equalsIgnoreCase("entity")) {
				filterList.add(parseEntity(condition));
			} */

			String parents = condition.getParent().attributeValue("parents");
			if(parents != null) {
				String[] split;
				if(parents.contains(" ")) {
					split = parents.split(" ");
				} else {
					split = new String[]{parents};
				}

				List<Filter> parentList = new ArrayList<>();
				for(String parent : split) {
					parentList.add(new FilterCondition(parent, State.ALLOW));
				}

				Filter[] filts = new Filter[parentList.size()];
				for(int i = 0; i < parentList.size(); i++) {
					filts[i] = parentList.get(i);
				}

				filterList.add(new MultiCondition(name, State.ALLOW, Modifier.ANY, filts));
			}

			// TODO: needs spawn condition
		}

		Filter[] filts = new Filter[filterList.size()];
		for(int i = 0; i < filterList.size(); i++) {
			filts[i] = filterList.get(i);
		}

		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new MultiCondition(name, state, Modifier.ANY, filts);
	}

	public static FilterCondition parseFilter(Element element) {
		String name = element.attributeValue("name");

		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new FilterCondition(name, state);
	}

	public static TeamCondition parseTeam(Element element, SporkMap map) throws InvalidFilterException {
		SporkTeam team = map.getTeam(element.getText());
		if(team == null) {
			throw new InvalidFilterException("'" + element.getText() + "' is not a valid team for TeamCondition");
		}

		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new TeamCondition(null, state, team);
	}

	public static BlockCondition parseBlock(Element element) throws InvalidFilterException {
		Material material = StringUtil.convertStringToMaterial(element.getText());
		if(material == null) {
			throw new InvalidFilterException("'" + element.getText() + "' is not a valid material for BlockCondition");
		}

		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new BlockCondition(null, state, material);
	}

	public static VoidCondition parseVoid(Element element) throws InvalidFilterException {
		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new VoidCondition(null, state);
	}

	/*
	public static EntityCondition parseEntity(Element element) throws InvalidFilterException {
		EntityType type = StringUtil.convertStringToEntityType(element.getText());
		if(type == null) {
			throw new InvalidFilterException("'" + element.getText() + "' is not a valid entity type for EntityCondition");
		}

		State state = State.ALLOW;
		if(element.getParent().getName().equalsIgnoreCase("deny")) {
			state = State.DENY;
		}

		return new EntityCondition(null, state, type);
	}
	*/

	private static List<Filter> defaults() {
		List<Filter> defaults = new ArrayList<>();

		TeamCondition allowPlayers = new TeamCondition("allow-players", State.DENY, null);
		defaults.add(allowPlayers);
		MultiCondition denyPlayers = new MultiCondition("deny-players", State.DENY, Modifier.NOT, allowPlayers);
		defaults.add(denyPlayers);

		BlockCondition allowBlocks = new BlockCondition("allow-blocks", State.DENY, null);
		defaults.add(allowBlocks);
		MultiCondition denyBlocks = new MultiCondition("deny-blocks", State.DENY, Modifier.NOT, allowBlocks);
		defaults.add(denyBlocks);

		BlockCondition allowWorld = new BlockCondition("allow-world", State.DENY, null);
		defaults.add(allowWorld);
		MultiCondition denyWorld = new MultiCondition("deny-world", State.DENY, Modifier.NOT, allowWorld);
		defaults.add(denyWorld);

		/*
		EntityCondition allowEntities = new EntityCondition("allow-entities", State.DENY, null);
		defaults.add(allowEntities);
		MultiCondition denyEntities = new MultiCondition("deny-entities", State.DENY, Modifier.NOT, allowEntities);
		defaults.add(denyEntities);
		*/

		Filter[] allow = new Filter[]{allowPlayers, allowBlocks, allowWorld};
		MultiCondition allowAll = new MultiCondition("allow-all", State.DENY, Modifier.ANY, allow);
		defaults.add(allowAll);
		Filter[] deny = new Filter[]{denyPlayers, denyBlocks, denyWorld};
		MultiCondition denyAll = new MultiCondition("deny-all", State.DENY, Modifier.ANY, deny);
		defaults.add(denyAll);

		/*
		EntityCondition allowMobs = new EntityCondition("allow-entities", State.DENY, EntityType);
		defaults.add(allowMobs);
		MultiCondition denyMobs = new MultiCondition("deny-entities", State.ALLOW, Modifier.NOT, allowEntities);
		defaults.add(denyMobs);
		*/

		return defaults;
	}

}
