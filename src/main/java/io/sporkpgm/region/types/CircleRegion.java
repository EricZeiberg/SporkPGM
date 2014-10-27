package io.sporkpgm.region.types;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CircleRegion extends CylinderRegion {

	public CircleRegion(String name, BlockRegion centre, double radius, boolean hollow) {
		super(name, new BlockRegion(centre.getStringX(), "-oo", centre.getStringZ()), radius, 1, hollow);
	}

	@Override
	public boolean isInside(BlockRegion block) {
		Vector point = new Vector(block.getX(), block.getY(), block.getZ());
		return Math.pow(point.getX() - origin.getX(), 2.0D) + Math.pow(point.getZ() - origin.getZ(), 2.0D) < (radius * radius);
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
		return "CircleRegion{name=" + getName() + ",center=[" + center + "],radius=" + radius + ",hollow=" + hollow + "}";
	}

}
