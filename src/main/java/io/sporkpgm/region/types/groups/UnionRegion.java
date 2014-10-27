package io.sporkpgm.region.types.groups;

import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.region.types.RectangleRegion;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class UnionRegion extends Region {

	protected List<Region> regions;

	public UnionRegion(List<Region> regions) {
		this(null, regions);
	}

	public UnionRegion(String name, List<Region> regions) {
		super(name);
		this.regions = regions;
	}

	@Override
	public boolean isInside(BlockRegion block) {
		return isInside(block, false);
	}

	@Override
	public boolean isInside(BlockRegion block, boolean log) {
		// Log.info("'" + getName() + "' (UnionRegion) has " + regions.size() + " Regions inside it");
		for(Region region : regions) {
			if(log) {
				Log.info("Checking '" + region.getName() + "' (" + region.getClass().getSimpleName() + ") for " + block);
			}

			if(region instanceof RectangleRegion) {
				RectangleRegion rectange = (RectangleRegion) region;
				rectange.isInside(block, log);
			}

			if(region.isInside(block, log)) {
				// Log.info(block + " was found inside '" + region.getName() + "'");
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isInside(Material material, World world) {
		for(BlockRegion region : getValues()) {
			if(region.getMaterial(world) == material) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<BlockRegion> getValues() {
		List<BlockRegion> blocks = new ArrayList<>();
		for(Region region : regions) {
			blocks.addAll(region.getValues());
		}
		return blocks;
	}

	@Override
	public List<BlockRegion> getValues(Material material, World world) {
		List<BlockRegion> blocks = new ArrayList<>();
		for(BlockRegion region : getValues()) {
			if(region.getMaterial(world) == material) {
				blocks.add(region);
			}
		}
		return blocks;
	}

	public List<Region> getRegions() {
		return regions;
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

	@Override
	public double distance(BlockRegion block) {
		List<Double> doubles = new ArrayList<>();
		for(Region region : regions) {
			doubles.add(region.distance(block));
		}

		return NumberUtil.getLowest(doubles);
	}

	@Override
	public double distance(BlockRegion block, Material type, World world) {
		List<Double> doubles = new ArrayList<>();
		for(Region region : regions) {
			doubles.add(region.distance(block, type, world));
		}

		return NumberUtil.getLowest(doubles);
	}

	@Override
	public double distance(BlockRegion block, Material[] types, World world) {
		List<Double> doubles = new ArrayList<>();
		for(Region region : regions) {
			doubles.add(region.distance(block, types, world));
		}

		return NumberUtil.getLowest(doubles);
	}

}
