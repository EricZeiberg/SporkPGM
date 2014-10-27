package io.sporkpgm.region.types.groups;

import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class NegativeRegion extends Region {

	private UnionRegion region;

	public NegativeRegion(String name, List<Region> regions) {
		super(name);
		region = new UnionRegion(null, regions);
	}

	public NegativeRegion(String name, UnionRegion region) {
		super(name);
		this.region = region;
	}

	public NegativeRegion(List<Region> regions) {
		this(null, regions);
	}

	@Override
	public boolean isInside(BlockRegion block) {
		return !region.isInside(block);
	}

	@Override
	public boolean isInside(Location location) {
		return !region.isInside(location);
	}

	@Override
	public boolean isInside(Vector vector) {
		return !region.isInside(vector);
	}

	@Override
	public boolean isInside(Material material, World world) {
		return !region.isInside(material, world);
	}

	@Override
	public List<BlockRegion> getValues() {
		return new ArrayList<>();
	}

	@Override
	public List<BlockRegion> getValues(Material material, World world) {
		return new ArrayList<>();
	}

	@Override
	public List<BlockRegion> getValues(Material material, int damage, World world) {
		List<BlockRegion> blocks = new ArrayList<>();
		for(BlockRegion region : getValues()) {
			if(region.getMaterial(world) == material && region.getBlock(world).getData() == (byte) damage) {
				blocks.add(region);
			}
		}
		return blocks;
	}

	public boolean isAbove(BlockRegion block) {
		return false;
	}

	public boolean isBelow(BlockRegion block) {
		return false;
	}

}
