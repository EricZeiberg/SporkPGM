package io.sporkpgm.rotation;

import io.sporkpgm.Spork;
import io.sporkpgm.filter.exceptions.InvalidFilterException;
import io.sporkpgm.map.MapBuilder;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.exception.MapLoadException;
import io.sporkpgm.match.Match;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.rotation.exceptions.RotationLoadException;

public class RotationSlot {

	boolean wasSet;
	MapBuilder loader;
	Match match;
	int id;

	public RotationSlot(MapBuilder loader, int id) {
		this(loader, false, id);
	}

	public RotationSlot(MapBuilder loader, boolean wasSet, int id) {
		this.wasSet = wasSet;
		this.loader = loader;
		this.id = id;
	}

	public boolean wasSet() {
		return wasSet;
	}

	public MapBuilder getLoader() {
		return loader;
	}

	public void setLoader(MapBuilder loader) {
		this.loader = loader;
		this.wasSet = true;
	}

	public Match getMatch() {
		return match;
	}

	public SporkMap getMap() {
		if(match == null) {
			return null;
		}

		return match.getMap();
	}

	private Match setMatch(Match match) {
		if(match == null)
			return this.match;
		this.match = match;
		return match;
	}

	public Match load() throws MapLoadException, RotationLoadException, InvalidRegionException, ModuleLoadException, InvalidFilterException {
		SporkMap map = loader.getMap();
		Match match = new Match(map, id);
		setMatch(match);
		if(!match.load())
			throw new RotationLoadException("Could not load Map on match #" + match.getID());
		return match;
	}

	public Match unload() throws RotationLoadException {
		if(!getMap().unload(match))
			throw new RotationLoadException("Could not unload Map on match #" + match.getID());
		return match;
	}

	public static Rotation getRotation() {
		return Spork.get().getRotation();
	}

	@Override
	public String toString() {
		return "RotationSlot{match=" + (match != null ? match.toString() : "null") + ",wasSet=" + wasSet + ",id=" + id + "}";
	}

}
