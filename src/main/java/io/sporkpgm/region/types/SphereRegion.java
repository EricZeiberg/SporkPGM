package io.sporkpgm.region.types;

import io.sporkpgm.region.Region;
import io.sporkpgm.util.RegionUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SphereRegion extends Region {

	Vector origin;
	BlockRegion center;
	double radius;
	boolean hollow;

	List<BlockRegion> values;

	public SphereRegion(BlockRegion center, double radius, boolean hollow) {
		this(null, center, radius, hollow);
	}

	public SphereRegion(String name, BlockRegion center, double radius, boolean hollow) {
		super(name);
		this.values = new ArrayList<>();
		if(!(center.isXInfinite() || center.isYInfinite() || center.isZInfinite())) {
			this.values = RegionUtil.sphere(center, radius, hollow);
		}

		this.center = center;
		this.origin = new Vector(center.getX(), center.getY(), center.getZ());

		this.radius = radius;
		this.hollow = hollow;
	}

	public List<BlockRegion> getValues() {
		return values;
	}

	@Override
	public boolean isInside(BlockRegion block) {
		Vector vector = new Vector(block.getX(), block.getY(), block.getZ());
		return vector.isInSphere(origin, radius);
	}

	public boolean matchesXZ(BlockRegion region) {
		BlockRegion block = new BlockRegion(region.getX(), center.getY(), region.getZ());
		CylinderRegion cylinder = new CylinderRegion(center, radius, 1, hollow);
		return cylinder.isInside(block);
	}

	public boolean isAbove(BlockRegion region) {
		if(!matchesXZ(region) || isInside(region)) {
			return false;
		}

		return center.getY() < region.getY();
	}

	public boolean isBelow(BlockRegion region) {
		if(!matchesXZ(region) || isInside(region)) {
			return false;
		}

		return center.getY() > region.getY();
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
		return "SphereRegion{name=" + getName() + ",center=[" + center + "],radius=" + radius + ",hollow=" + hollow + "}";
	}

}
