package io.sporkpgm.region.types;

import io.sporkpgm.Spork;
import io.sporkpgm.region.Region;
import io.sporkpgm.util.RegionUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CylinderRegion extends Region {

	protected Vector origin;
	protected BlockRegion center;
	protected double radius;
	protected double height;
	protected boolean hollow;

	List<BlockRegion> values;
	boolean infinite;

	public CylinderRegion(BlockRegion center, double radius, double height, boolean hollow) {
		this(null, center, radius, height, hollow);
	}

	public CylinderRegion(String name, BlockRegion center, double radius, double height, boolean hollow) {
		super(name);
		this.values = new ArrayList<>();
		if(!(center.isXInfinite() || center.isYInfinite() || center.isZInfinite())) {
			this.values = RegionUtil.cylinder(center, radius, height, hollow, false);
		} else if(center.isYInfinite() && !(center.isXInfinite() || center.isZInfinite())) {
            //Spork.get().getLogger().info("This @ method was called!");
			this.values = RegionUtil.cylinder(new BlockRegion(center.getX() + "", center.getY() + "", center.getZ() + ""), radius, 1, hollow, false);
            this.infinite = true;
		}

		this.center = center;
		this.origin = new Vector(center.getX(), center.getY(), center.getZ());

		this.radius = radius;
		this.height = height;
		this.hollow = hollow;
	}

	public List<BlockRegion> getValues() {
		return values;
	}

	@Override
	public boolean isInside(BlockRegion block) {
		Vector point = new Vector(block.getX(), block.getY(), block.getZ());
		if(point.getY() >= origin.getY() && point.getY() <= origin.getY() + height) {
			return Math.pow(point.getX() - origin.getX(), 2.0D) + Math.pow(point.getZ() - origin.getZ(), 2.0D) < (radius * radius);
		}

		return false;

		/*
		if(infinite) {
			return matchesXZ(block);
		}

		BlockRegion check = new BlockRegion(block.getStringX(), values.get(0).getStringY(), block.getStringZ());
		double max = check.getDoubleY() + height;

		for(BlockRegion region : getValues()) {
			if(region.isInside(check) && block.getDoubleY() <= max) {
				return true;
			}
		}

		return false;
		*/
	}

	public boolean matchesXZ(BlockRegion region) {
		BlockRegion block = new BlockRegion(region.getX(), center.getY(), region.getZ());
		CylinderRegion cyl = new CylinderRegion(center, radius, 1, hollow);
		return cyl.isInside(block);
	}

	public boolean isAbove(BlockRegion region) {
		if(infinite || !matchesXZ(region)) {
			return false;
		}

		CuboidRegion cube = new CuboidRegion(region, new BlockRegion(region.getX(), region.getY() + height, region.getZ()));
		return cube.isAbove(region);
	}

	public boolean isBelow(BlockRegion region) {
		if(infinite || !matchesXZ(region)) {
			return false;
		}

		CuboidRegion cube = new CuboidRegion(region, new BlockRegion(region.getX(), region.getY() + height, region.getZ()));
		return cube.isBelow(region);
	}

	@Override
	public double distance(BlockRegion block) {
		BlockRegion center = new BlockRegion(this.center.getStringX(), block.getStringY(), this.center.getStringZ());
		return center.distance(block) - radius;
	}

	@Override
	public double distance(BlockRegion region, Material type, World world) {
		return distance(region);
	}

	@Override
	public double distance(BlockRegion region, Material[] types, World world) {
		return distance(region);
	}

	@Override
	public String toString() {
		return "CylinderRegion{name=" + getName() + ",center=[" + center + "],radius=" + radius + ",height=" + height + ",hollow=" + hollow + "}";
	}

}
