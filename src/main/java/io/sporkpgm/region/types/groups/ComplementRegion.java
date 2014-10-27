package io.sporkpgm.region.types.groups;

import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ComplementRegion extends Region {

	List<BlockRegion> blocks;
	Region base;
	List<Region> complements;

	public ComplementRegion(Region base, List<Region> complements) {
		this(null, base, complements);
	}

	public ComplementRegion(String name, Region base, List<Region> complements) {
		super(name);
		this.base = base;
		this.complements = complements;
		this.blocks = new ArrayList<>();
	}

	@Override
	public List<BlockRegion> getValues() {
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
		int count = 0;
		for(Region region : complements) {
			if(region.isInside(block)) {
				count++;
				if(count > 1) {
					return false;
				}
			}
		}

		return count == 1;
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
		for(Region region : complements) {
			if(!region.isInside(material, world) && base.isInside(material, world)) {
				return true;
			}
		}

		return false;
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
