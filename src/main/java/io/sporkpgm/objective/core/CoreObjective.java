package io.sporkpgm.objective.core;

import com.google.common.collect.Lists;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.extras.InitModule;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.region.types.groups.UnionRegion;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.*;

@ModuleInfo(name = "CoreObjective", description = "Objective where liquid flowing from a region is tracked until a specified distance")
public class CoreObjective extends ObjectiveModule implements InitModule {

	UnionRegion core;
	UnionRegion casing;
	Liquid liquid;
	Material material;
	ChatColor color;
	Region region;
	int leak;

	boolean completed;

	public CoreObjective(String name, Material material, Region region, SporkTeam team, int leak) {
		super(name, team);
		this.color = team.getColor();
		this.material = material;
		this.region = region;
		this.leak = leak;
	}

	public Material getMaterial() {
		return material;
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public boolean isComplete() {
		return completed;
	}

	@Override
	public void setComplete(boolean complete) {
		this.completed = complete;
		if(!complete) {
			return;
		}

		SporkTeam opposite = team.getOpposite();
		Bukkit.broadcastMessage(opposite.getColoredName() + "'s " + ChatColor.DARK_AQUA + name + ChatColor.GRAY + " has leaked!");
		update();
	}

	public void start() {
		this.liquid = Liquid.getLiquid(region, team.getMap().getWorld());

		List<Region> regions = new ArrayList<>();
		regions.addAll(region.getValues(liquid.getMaterials(), team.getMap().getWorld()));
		this.core = new UnionRegion(name, regions);

		regions = new ArrayList<>();
		regions.addAll(region.getValues(material, team.getMap().getWorld()));
		this.casing = new UnionRegion(name, regions);
	}

	public void stop() { /* nothing */ }

	@EventHandler
	public void onCoreLeak(BlockChangeEvent event) {
		if(isComplete()) {
			return;
		}

		BlockState old = event.getOldState();
		BlockState now = event.getNewState();
		if(!Liquid.matches(liquid, old.getType()) && !Liquid.matches(liquid, now.getType())) {
			//Log.info(liquid.name() + " doesn't match " + old.getType().name() + " or " + now.getType().name());
			return;
		}

		BlockRegion location = event.getRegion();
		if(event.getEvent() instanceof BlockFromToEvent) {
			BlockFromToEvent from = (BlockFromToEvent) event.getEvent();
			if(Liquid.matches(liquid, old.getType())) {
				location = new BlockRegion(from.getBlock().getLocation());
			} else {
				location = new BlockRegion(from.getToBlock().getLocation());
			}
		}

		/*
		if(!event.isPlace()) {
			// Thread.dumpStack();
			Log.info("Ignoring event because it was not a block being changed from air (" + event.getOldState().getType() + " => " + event.getNewState().getType() + ")");
			return;
		}
		*/

		if(event.hasPlayer()) {
			Log.info("Ignoring event because it was caused by a player");
			return;
		}

		double distance = region.distance(location);
		if(distance >= leak && distance <= leak + 4) {
			setComplete(true);
		}
	}

	@EventHandler
	public void onTeamDamage(BlockChangeEvent event) {
		if(isComplete()) {
			return;
		}

		if(event.isPlace()) {
			if(core.isBelow(event.getRegion())) {
				event.setCancelled(true);
				if(event.hasPlayer()) {
					event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "You can't modify the contents of the core");
				}
			}
			return;
		}

		if(!event.hasPlayer()) {
			return;
		}

		if(event.getPlayer().getTeam() != getTeam() && casing.isInside(event.getRegion())) {
			event.setCancelled(true);
			event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "You can't leak your own core");
			return;
		}
	}

	@Override
	public String getPlayer() {
		return player;
	}

	@Override
	public ChatColor getStatusColour() {
		return (isComplete() ? ChatColor.GREEN : ChatColor.RED);
	}

	@Override
	public Class<? extends Builder> builder() {
		return CoreBuilder.class;
	}

	public enum Liquid {

		LAVA(new Material[]{Material.LAVA, STATIONARY_LAVA}),
		WATER(new Material[]{Material.WATER, STATIONARY_WATER}),
		NONE(new Material[0]);

		Material[] materials;

		Liquid(Material[] materials) {
			this.materials = materials;
		}

		public Material[] getMaterials() {
			return materials;
		}

		public boolean matches(Material material) {
			return Lists.newArrayList(materials).contains(material);
		}

		public boolean contains(Region region, World world) {
			for(Material material : materials) {
				if(region.isInside(material, world)) {
					return true;
				}
			}

			return false;
		}

		public static Liquid getLiquid(Region region, World world) {
			for(Liquid liquid : values()) {
				if(liquid.contains(region, world)) {
					return liquid;
				}
			}

			return NONE;
		}

		public static boolean matches(Liquid liquid, Material material) {
			return liquid.matches(material);
		}

	}

}
