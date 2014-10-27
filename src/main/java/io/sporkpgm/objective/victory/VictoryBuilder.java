package io.sporkpgm.objective.victory;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.RegionBuilder;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = false)
public class VictoryBuilder extends Builder {

	public VictoryBuilder(SporkMap map) {
		super(map);
	}

	public List<Module> build() throws ModuleLoadException, InvalidRegionException {
		List<Module> modules = new ArrayList<>();
		Element root = getRoot();

		for(Element element : XMLUtil.getElements(root, "wools")) {
			String teamS = element.attributeValue("team");

			for(Element wool : XMLUtil.getElements(element, "wool")) {
				teamS = (wool.attributeValue("team") != null ? wool.attributeValue("team") : teamS);
				SporkTeam team = map.getTeam(teamS);

				DyeColor dye = StringUtil.convertStringToDyeColor(wool.attributeValue("color"));
				if(dye == null) {
					throw new ModuleLoadException(wool, "'" + wool.attributeValue("color") + "' is not a supported DyeColor");
				}

				String name = WordUtils.capitalizeFully(StringUtil.fromTechnicalName(dye.name()) + " Wool");
				ChatColor color = StringUtil.convertDyeColorToChatColor(dye);
				BlockRegion block = RegionBuilder.parseBlock(wool.element("block"));
				modules.add(new VictoryObjective(name, team, dye, color, block));
			}
		}

		return modules;
	}

}
