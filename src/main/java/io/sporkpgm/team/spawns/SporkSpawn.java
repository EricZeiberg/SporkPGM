package io.sporkpgm.team.spawns;

import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.region.types.groups.UnionRegion;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.team.spawns.kits.SporkKit;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NumberUtil;
import io.sporkpgm.util.RegionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class SporkSpawn extends UnionRegion {

	private boolean safe;
	private String name;
	private SporkKit kit;

	private float yaw;
	private float pitch;

	public SporkSpawn(String name, List<Region> regions) {
		this(name, regions, null, 0, 0);
	}

	public SporkSpawn(String name, List<Region> regions, float yaw) {
		this(name, regions, null, yaw, 0);
	}

	public SporkSpawn(String name, List<Region> regions, float yaw, float pitch) {
		this(name, regions, null, yaw, pitch);
	}

	public SporkSpawn(String name, List<Region> regions, SporkKit kit) {
		this(name, regions, kit, 0, 0);
	}

	public SporkSpawn(String name, List<Region> regions, SporkKit kit, float yaw) {
		this(name, regions, kit, yaw, 0);
	}

	public SporkSpawn(String name, List<Region> regions, SporkKit kit, float yaw, float pitch) {
		super(name, regions);
		this.name = name;
		this.kit = kit;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getName() {
		return name;
	}

	public List<Region> getRegion() {
		return regions;
	}

	public Location getSpawn() {
		World world = RotationSlot.getRotation().getCurrent().getWorld();
		List<BlockRegion> values = (safe ? getValues(Material.AIR, world) : getValues());
		if(values.size() == 0) {
			Log.warning("'" + getName() + "' has no values - error likely");
		}

		Location spawn = RegionUtil.getRandom(values).getLocation(world);
		spawn.setYaw(yaw);
		spawn.setPitch(pitch);
		return spawn;
	}

	public SporkKit getKit() {
		return kit;
	}

	public boolean hasKit() {
		return kit != null;
	}

	public void setKit(SporkKit kit) {
		this.kit = kit;
	}

}
