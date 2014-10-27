package io.sporkpgm.team;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.exception.InvalidTeamException;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.ChatColor;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class SporkTeamBuilder {

	public static List<SporkTeam> build(SporkMap map) {
		Document document = map.getDocument();
		List<SporkTeam> teams = new ArrayList<>();

		Element teamsElement = document.getRootElement().element("teams");
		for(Element team : XMLUtil.getElements(teamsElement, "team")) {
			Log.info("Found Team XML: " + team.asXML());
			try {
				String nameAttribute = team.getText();
				Attribute colorAttribute = team.attribute("color");
				Attribute maxAttribute = team.attribute("max");
				Attribute overflowAttribute = team.attribute("max-overfill");
				Attribute overheadAttribute = team.attribute("overhead-color");

				if(colorAttribute == null || maxAttribute == null || nameAttribute == null) {
					String name = "name[" + (nameAttribute != null ? nameAttribute : "null") + "]";
					String color = "color[" + (colorAttribute != null ? colorAttribute.getText() : "null") + "]";
					String max = "max[" + (maxAttribute != null ? maxAttribute.getText() : "null") + "]";

					throw new InvalidTeamException("A team was missing required arguments: " + name + ", " + color + ", " + max);
				}

				ChatColor color = StringUtil.convertStringToChatColor(colorAttribute.getText());
				if(color == null)
					throw new InvalidTeamException("A ChatColor for a team was invalid");

				int max = -1;
				try {
					max = StringUtil.convertStringToInteger(maxAttribute.getText());
				} catch(Exception ignored) {
				}

				if(max < 1)
					throw new InvalidTeamException("A max players for a team was invalid");

				int overfill = -1;
				try {
					overfill = StringUtil.convertStringToInteger(overflowAttribute.getText());
				} catch(Exception e) {
					overfill = max + (max / 4);
				}

				ChatColor overhead = color;
				if(overheadAttribute != null) {
					ChatColor possible = StringUtil.convertStringToChatColor(overheadAttribute.getText());
					if(possible != null)
						overhead = possible;
				}

				teams.add(new SporkTeam(map, nameAttribute, color, overhead, max, overfill, false));
			} catch(InvalidTeamException e) {
				Log.warning("Invalid Team: " + e.getMessage());
			}
		}

		return teams;
	}

	public static SporkTeam observers(SporkMap map) {
		return new SporkTeam(map, "Observers", ChatColor.AQUA, ChatColor.AQUA, 0, 0, true);
	}

}
