package io.sporkpgm.region.types;

import com.google.common.collect.Lists;
import io.sporkpgm.region.Region;
import io.sporkpgm.util.RegionUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CuboidRegion extends Region {

	protected BlockRegion one;
	protected BlockRegion two;

	public CuboidRegion(BlockRegion one, BlockRegion two) {
		this(null, one, two);
	}

	public CuboidRegion(String name, BlockRegion one, BlockRegion two) {
		super(name);
		double xMin, xMax, yMin, yMax, zMin, zMax = 0;
		xMin = Math.min(one.getX(), two.getX());
		xMax = Math.max(one.getX(), two.getX()) - 1;
		yMin = Math.min(one.getY(), two.getY());
		yMax = Math.max(one.getY(), two.getY()) - 1;
		zMin = Math.min(one.getZ(), two.getZ());
		zMax = Math.max(one.getZ(), two.getZ()) - 1;

		this.one = new BlockRegion(xMax, yMax, zMax);
		this.two = new BlockRegion(xMin, yMin, zMin);
	}

	public BlockRegion[] getPoints() {
		return new BlockRegion[]{one, two};
	}

	public List<BlockRegion> getValues() {
		return RegionUtil.cuboid(one, two);
	}

	@Override
	public boolean isInside(BlockRegion block) {
		Vector point = new Vector(block.getX(), block.getY(), block.getZ());
		Vector min = two.getVector();
		Vector max = one.getVector();

		return point.isInAABB(min, max);
	}

	public boolean isAboveOrBelow(BlockRegion region) {
		BlockRegion block = new BlockRegion(region.getX(), getPoints()[0].getY(), region.getZ());
		if(isInside(block)) {
			double yMin = getPoints()[1].getY();
			double yMax = getPoints()[0].getY();
			if(region.getY() > yMax || region.getY() < yMin) {
				return true;
			}
		}

		return false;
	}

	public boolean isAbove(BlockRegion region) {
		BlockRegion block = new BlockRegion(region.getX(), getPoints()[0].getY(), region.getZ());
		if(isInside(block)) {
			double yMax = getPoints()[0].getY();
			if(region.getY() > yMax) {
				return true;
			}
		}

		return false;
	}

	public boolean isBelow(BlockRegion region) {
		BlockRegion block = new BlockRegion(region.getX(), getPoints()[0].getY(), region.getZ());
		if(isInside(block)) {
			double yMin = getPoints()[1].getY();
			if(region.getY() < yMin) {
				return true;
			}
		}

		return false;
	}

	@Override
	public double distance(BlockRegion block) {
		List<BlockRegion> regions = values(block, true);
		return RegionUtil.distance(block, regions);
	}

	@Override
	public double distance(BlockRegion block, Material type, World world) {
		List<BlockRegion> regions = values(block, false);
		List<BlockRegion> matched = new ArrayList<>();

		for(BlockRegion region : regions) {
			if(region.getBlock(world).getType() == type) {
				matched.add(region);
			}
		}

		return RegionUtil.distance(block, matched);
	}

	@Override
	public double distance(BlockRegion block, Material[] types, World world) {
		List<BlockRegion> regions = values(block, false);
		List<BlockRegion> matched = new ArrayList<>();

		List<Material> materials = Lists.newArrayList(types);
		for(BlockRegion region : regions) {
			if(materials.contains(region.getBlock(world).getType())) {
				matched.add(region);
			}
		}

		return RegionUtil.distance(block, matched);
	}

	public List<BlockRegion> values(BlockRegion block, boolean hollow) {
		BlockRegion pos1 = getPoints()[0];
		BlockRegion pos2 = getPoints()[1];
		return RegionUtil.cuboid(pos1, pos2, hollow);
	}

	@Override
	public String toString() {
		return "CuboidRegion{name=" + getName() + ",min=[" + one + "],max=[" + two + "]}";
	}

}
