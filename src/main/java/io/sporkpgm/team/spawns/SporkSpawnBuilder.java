package io.sporkpgm.team.spawns;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.RegionBuilder;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.team.spawns.kits.SporkKit;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.XMLUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class SporkSpawnBuilder {

	public static List<SporkSpawn> build(SporkMap map) throws ModuleLoadException, InvalidRegionException {
		List<SporkSpawn> sporks = new ArrayList<>();
		Document document = map.getDocument();
		Element root = document.getRootElement();
		sporks.addAll(spawns(map, root));
		return sporks;
	}

	public static List<SporkSpawn> spawns(SporkMap map, Element element) throws ModuleLoadException, InvalidRegionException {
		List<SporkSpawn> sporks = new ArrayList<>();

		sporks.addAll(parseSpawns(map, XMLUtil.getElements(element, "spawn", "default")));
		if(element.element("spawns") != null) {
			for(Element spawns : XMLUtil.getElements(element, "spawns")) {
				sporks.addAll(spawns(map, spawns));
			}
		}

		return sporks;
	}

	public static List<SporkSpawn> parseSpawns(SporkMap map, List<Element> spawns) throws ModuleLoadException, InvalidRegionException {
		List<SporkSpawn> sporks = new ArrayList<>();

		for(Element element : spawns) {
			SporkSpawn spawn = parseSpawn(map, element);
			sporks.add(spawn);
		}

		return sporks;
	}

	public static SporkSpawn parseSpawn(SporkMap map, Element element) throws ModuleLoadException, InvalidRegionException {
		String nameS = null;

		// Log.info("Parsing " + element.asXML());
		String teamS = element.getParent().attributeValue("team");
		String yawS = element.getParent().attributeValue("yaw");
		String pitchS = element.getParent().attributeValue("pitch");
		String kitS = element.getParent().attributeValue("kit");

		teamS = (element.attributeValue("team") != null ? element.attributeValue("team") : teamS);
		if(element.getName().equalsIgnoreCase("default")) {
			teamS = map.getObservers().getName();
		}

		yawS = (element.attributeValue("yaw") != null ? element.attributeValue("yaw") : yawS);
		pitchS = (element.attributeValue("pitch") != null ? element.attributeValue("pitch") : pitchS);
		kitS = (element.attributeValue("kit") != null ? element.attributeValue("kit") : kitS);

		String name = (nameS == null ? "noname" : nameS);
		SporkTeam team = map.getTeam(teamS);

		List<Region> regions = RegionBuilder.parseSubRegions(element);
		float yaw = 0;
		float pitch = 0;

		try {
			yaw = Float.parseFloat(yawS);
		} catch(Exception ignored) {
		}

		try {
			pitch = Float.parseFloat(pitchS);
		} catch(Exception ignored) {
		}

		SporkKit match = null;
		if(kitS != null) {
			for(SporkKit ak : map.getKits()) {
				if(ak.getName().equalsIgnoreCase(kitS)) {
					match = ak;
					break;
				}
			}
			if(match == null)
				throw new ModuleLoadException("Kit `" + kitS + "` not found while parsing `" + teamS + "` spawn!");
		}

		SporkSpawn spawn = new SporkSpawn(name, regions, match, yaw, pitch);
		team.getSpawns().add(spawn);
		return spawn;
	}

}
