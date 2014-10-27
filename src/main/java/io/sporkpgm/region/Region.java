package io.sporkpgm.region;

import com.google.common.collect.Lists;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.util.NumberUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class Region {

	private String name;

	public Region(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean isInside(BlockRegion block);

	public boolean isInside(BlockRegion block, boolean log) {
		return isInside(block);
	}

	public boolean isInside(double x, double y, double z) {
		return isInside(x, y, z, false);
	}

	public boolean isInside(double x, double y, double z, boolean log) {
		return isInside(new BlockRegion(x, y, z), log);
	}

	public boolean isInside(Location location) {
		return isInside(location, false);
	}

	public boolean isInside(Location location, boolean log) {
		return isInside(location.getX(), location.getY(), location.getZ(), log);
	}

	public boolean isInside(Vector vector) {
		return isInside(vector, false);
	}

	public boolean isInside(Vector vector, boolean log) {
		return isInside(vector.getX(), vector.getY(), vector.getZ(), log);
	}

	public boolean isInside(Material material, World world) {
		for(BlockRegion region : getValues()) {
			if(region.getMaterial(world) == material) {
				return true;
			}
		}

		return false;
	}

	public boolean isAboveOrBelow(BlockRegion check) {
		return isAbove(check) || isBelow(check);
	}

	public boolean isAboveOrBelow(double x, double y, double z) {
		return isAboveOrBelow(new BlockRegion(x, y, z));
	}

	public boolean isAboveOrBelow(Location location) {
		return isAboveOrBelow(location.getX(), location.getY(), location.getZ());
	}

	public abstract boolean isAbove(BlockRegion check);

	public boolean isAbove(double x, double y, double z) {
		return isAbove(new BlockRegion(x, y, z));
	}

	public boolean isAbove(Location location) {
		return isAbove(location.getX(), location.getY(), location.getZ());
	}

	public abstract boolean isBelow(BlockRegion check);

	public boolean isBelow(double x, double y, double z) {
		return isBelow(new BlockRegion(x, y, z));
	}

	public boolean isBelow(Location location) {
		return isBelow(location.getX(), location.getY(), location.getZ());
	}

	public double distance(BlockRegion region) {
		List<Double> doubles = new ArrayList<>();
		for(BlockRegion block : getValues()) {
			doubles.add(region.distance(block));
		}

		return NumberUtil.getLowest(doubles);
	}

	public double distance(BlockRegion region, Material type, World world) {
		List<Double> doubles = new ArrayList<>();
		for(BlockRegion block : getValues(type, world)) {
			doubles.add(region.distance(block));
		}

		return NumberUtil.getLowest(doubles);
	}

	public double distance(BlockRegion region, Material[] types, World world) {
		List<Double> doubles = new ArrayList<>();
		for(BlockRegion block : getValues(types, world)) {
			doubles.add(region.distance(block));
		}

		return NumberUtil.getLowest(doubles);
	}

	public abstract List<BlockRegion> getValues();

	public List<BlockRegion> getValues(Material type, World world) {
		List<BlockRegion> blocks = new ArrayList<>();

		for(BlockRegion region : getValues()) {
			Block block = region.getBlock(world);
			if(block.getType() == type) {
				blocks.add(region);
			}
		}

		return blocks;
	}

	public List<BlockRegion> getValues(Material[] types, World world) {
		List<BlockRegion> blocks = new ArrayList<>();

		for(BlockRegion region : getValues()) {
			Block block = region.getBlock(world);
			for(Material material : types) {
				if(material == block.getType()) {
					blocks.add(region);
					break;
				}
			}
		}

		return blocks;
	}

	public List<BlockRegion> getValues(Material type, int damage, World world) {
		List<BlockRegion> blocks = new ArrayList<>();

		for(BlockRegion region : getValues()) {
			Block block = region.getBlock(world);
			if(block.getType() == type && block.getData() == damage) {
				blocks.add(region);
			}
		}

		return blocks;
	}

	public List<Entity> getEntities(World world) {
		List<Entity> entities = new ArrayList<>();

		for(Entity entity : world.getEntities()) {
			if(isInside(entity.getLocation())) {
				entities.add(entity);
			}
		}

		return entities;
	}

	public List<Entity> getEntities(Class<? extends Entity> type, World world) {
		return getEntities(new Class[]{type}, world);
	}

	public List<Entity> getEntities(List<Class<? extends Entity>> types, World world) {
		List<Entity> entities = new ArrayList<>();

		for(Entity entity : world.getEntities()) {
			if(types.contains(entity.getClass()) && isInside(entity.getLocation())) {
				entities.add(entity);
			}
		}

		return entities;
	}

	public List<Entity> getEntities(Class<? extends Entity>[] classes, World world) {
		return getEntities(Lists.newArrayList(classes), world);
	}

}
