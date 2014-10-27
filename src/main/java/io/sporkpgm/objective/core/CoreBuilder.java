package io.sporkpgm.objective.core;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleStage;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.RegionBuilder;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Material;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = false, stage = ModuleStage.LOAD)
public class CoreBuilder extends Builder {

	public CoreBuilder(SporkMap map) {
		super(map);
	}

	@Override
	public List<Module> build() throws ModuleLoadException, InvalidRegionException {
		List<Module> modules = new ArrayList<>();
		Element root = getRoot();
		modules.addAll(cores(map, root));
		return modules;
	}

	public List<CoreObjective> cores(SporkMap map, Element element) throws ModuleLoadException, InvalidRegionException {
		List<CoreObjective> sporks = new ArrayList<>();

		sporks.addAll(parseCores(map, XMLUtil.getElements(element, "core")));
		if(element.element("cores") != null) {
			for(Element spawns : XMLUtil.getElements(element, "cores")) {
				sporks.addAll(cores(map, spawns));
			}
		}

		return sporks;
	}

	public List<CoreObjective> parseCores(SporkMap map, List<Element> elements) throws ModuleLoadException, InvalidRegionException {
		List<CoreObjective> objectives = new ArrayList<>();
		for(Element element : elements) {
			objectives.add(parseCore(map, element));
		}
		return objectives;
	}

	public CoreObjective parseCore(SporkMap map, Element element) throws ModuleLoadException, InvalidRegionException {
		String name = XMLUtil.getElementOrParentValue(element, "name");
		if(name == null) {
			name = "Core";
		}

		String type = XMLUtil.getElementOrParentValue(element, "material");
		Material material = StringUtil.convertStringToMaterial(type);

		String value = XMLUtil.getElementOrParentValue(element, "leak");
		int leak;
		try {
			leak = Integer.parseInt(value);
		} catch(Exception e) {
			throw new ModuleLoadException((value != null ? value : "null") + " is not a valid integer");
		}

		SporkTeam other = null;
		String team = XMLUtil.getElementOrParentValue(element, "team");
		if(team != null) {
			other = map.getTeam(team);
		}

		Region region = RegionBuilder.parseCuboid(((Element) element.elements().get(0)));
		SporkTeam owner = other.getOpposite();

		if(name == null) {
			throw new ModuleLoadException("A Core name could not be found");
		} else if(material == null) {
			throw new ModuleLoadException("Invalid Material supplied '" + type + "'");
		} else if(leak <= 0) {
			throw new ModuleLoadException("Leak distance must be greater than 0");
		} else if(owner == null) {
			throw new ModuleLoadException("The owner of a Core can't be null");
		} else if(region == null) {
			throw new ModuleLoadException("The region of a Core can't be null");
		}

		return new CoreObjective(name, material, region, owner, leak);
	}

}
