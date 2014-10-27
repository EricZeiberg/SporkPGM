package io.sporkpgm.region.types.groups;

import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntersectRegion extends Region {

	private List<Region> regions;

	public IntersectRegion(String name, Region... regs) {
		super(name);
		regions = new ArrayList<>();
		regions = Arrays.asList(regs);
	}

	public IntersectRegion(String name, List<Region> regions) {
		this(name, (Region[]) regions.toArray());
	}

	@Override
	public List<BlockRegion> getValues() {
		List<BlockRegion> blocks = new ArrayList<>();
		for(Region r : regions) {
			for(BlockRegion b : r.getValues()) {
				for(Region other : regions) {
					if(other.isInside(b) && !blocks.contains(b)) blocks.add(b);
				}
			}
		}
		return blocks;
	}

	@Override
	public List<BlockRegion> getValues(Material material, World world) {
		List<BlockRegion> values = new ArrayList<>();
		for(BlockRegion region : getValues()) {
			if(region.getMaterial(world) == material) {
				values.add(region);
			}
		}
		return values;
	}

	@Override
	public boolean isInside(BlockRegion block) {
		for(Region r : regions) {
			if(!r.isInside(block)) return false;
		}
		return true;
	}

	@Override
	public boolean isInside(Location location) {
		return isInside(new BlockRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	@Override
	public boolean isInside(Vector vector) {
		return isInside(new BlockRegion(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
	}

	@Override
	public boolean isInside(Material material, World world) {
		for(Region r : regions) {
			if(!r.isInside(material, world)) return false;
		}
		return true;
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
