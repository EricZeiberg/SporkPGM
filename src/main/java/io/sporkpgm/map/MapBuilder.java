package io.sporkpgm.map;

import io.sporkpgm.Spork;
import io.sporkpgm.filter.exceptions.InvalidFilterException;
import io.sporkpgm.map.exception.MapLoadException;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.module.modules.info.Contributor;
import io.sporkpgm.module.modules.info.InfoBuilder;
import io.sporkpgm.module.modules.info.InfoModule;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.RegionBuilder;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.team.spawns.kits.SporkKit;
import io.sporkpgm.team.spawns.kits.SporkKitBuilder;
import io.sporkpgm.util.Log;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapBuilder {

	private Document document;
	private File folder;

	private InfoModule info;
	private List<Module> modules;
	private List<Region> regions;
	private List<SporkKit> kits;

	public MapBuilder(Document document, File folder) throws ModuleLoadException, InvalidRegionException {
		this.document = document;
		this.folder = folder;

		Element root = document.getRootElement();
		this.info = (InfoModule) new InfoBuilder(document).build().get(0);

		if(root.element("regions") != null) {
			this.regions = RegionBuilder.parseSubRegions(root.element("regions"));
		}

		this.modules = Spork.get().getModules(document);
		// Log.info("Loaded " + modules.size() + " Modules: " + modules);

		this.kits = SporkKitBuilder.build(document);
		if(kits == null) {
			this.kits = new ArrayList<>();
		}

		this.regions = new ArrayList<>();
	}

	public Document getDocument() {
		return document;
	}

	public File getFolder() {
		return folder;
	}

	public InfoModule getInfo() {
		return info;
	}

	public String getName() {
		return info.getName();
	}

	public String getDescription() {
		return info.getShortDescription();
	}

	public String getVersion() {
		return info.getVersion();
	}

	public String getObjective() {
		return info.getObjective();
	}

	public List<String> getRules() {
		return info.getRules();
	}

	public List<Contributor> getAuthors() {
		return info.getAuthors();
	}

	public List<String> getAuthorNames() {
		List<String> names = new ArrayList<>();
		for(Contributor author : getAuthors()) {
			names.add(author.getUsername());
		}

		return names;
	}

	public List<Contributor> getContributors() {
		return info.getContributors();
	}

	public int getMaxPlayers() {
		return info.getMaxPlayers();
	}

	public String getFolderName() {
		return folder.getName();
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public List<SporkKit> getKits() {
		return kits;
	}

	public SporkMap getMap() throws MapLoadException, ModuleLoadException, InvalidRegionException, InvalidFilterException {
		return new SporkMap(this);
	}

	@Override
	public String toString() {
		return info.getName();
	}

	public static MapBuilder getLoader(String string) {
		List<MapBuilder> loaders = Spork.getMaps();

		for(MapBuilder loader : loaders)
			if(loader.getName().equalsIgnoreCase(string))
				return loader;

		for(MapBuilder loader : loaders)
			if(loader.getName().toLowerCase().contains(string.toLowerCase()))
				return loader;

		return null;
	}

}
