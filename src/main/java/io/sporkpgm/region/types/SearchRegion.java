package io.sporkpgm.region.types;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.util.Log;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SearchRegion extends Region {

	private SporkMap map;

	private String search;
	private Region region;

	public SearchRegion(String name, String search) {
		super((name != null ? name : "search-" + search));
		this.search = search;
	}

	@Override
	public String getName() {
		try {
			get();
			return region.getName();
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return "unkown-search";
		} catch(NullPointerException e) {
			return "unkown-search";
		}
	}

	public List<BlockRegion> getValues() {
		try {
			get();
			return region.getValues();
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return new ArrayList<>();
		} catch(NullPointerException e) {
			return new ArrayList<>();
		}
	}

	public boolean isInside(BlockRegion block) {
		try {
			get();
			return region.isInside(block);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return false;
		} catch(NullPointerException e) {
			return false;
		}
	}

	public boolean isAbove(BlockRegion block) {
		try {
			get();
			return region.isAbove(block);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return false;
		} catch(NullPointerException e) {
			return false;
		}
	}

	public boolean isBelow(BlockRegion block) {
		try {
			get();
			return region.isBelow(block);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return false;
		} catch(NullPointerException e) {
			return false;
		}
	}

	@Override
	public double distance(BlockRegion block) {
		try {
			get();
			return region.distance(block);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return 0;
		} catch(NullPointerException e) {
			return 0;
		}
	}

	@Override
	public double distance(BlockRegion block, Material type, World world) {
		try {
			get();
			return region.distance(block, type, world);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return 0;
		} catch(NullPointerException e) {
			return 0;
		}
	}

	@Override
	public double distance(BlockRegion block, Material[] types, World world) {
		try {
			get();
			return region.distance(block, types, world);
		} catch(InvalidRegionException e) {
			Log.info(e.getMessage());
			return 0;
		} catch(NullPointerException e) {
			return 0;
		}
	}

	public void get(SporkMap map) {
		this.map = map;
		this.region = map.getRegion(search);
	}

	private void get() throws InvalidRegionException {
		// Log.info(toString());
		if(region != null && map == Match.getMatch().getMap()) {
			// Log.info("Already found the Region and the map hasn't changed");
			return;
		}

		try {
			get(Match.getMatch().getMap());
		} catch(NullPointerException e) {
			throw new InvalidRegionException(null, "Unable to fetch info about the map just yet!");
		}
		// Log.info("Set new Region for " + toString());
	}

	@Override
	public String toString() {
		return "SearchRegion{map=" + map + ",search=" + search + ",region=" + region + "}";
	}

}
