package io.sporkpgm.objective.monument;

import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.extras.InitModule;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "MonumentObjective", description = "Objective which tracks Block breaks to be completed")
public class MonumentObjective extends ObjectiveModule implements InitModule {

	Material[] materials;
	ChatColor color;
	Region region;
	int completion;

	List<MonumentBlock> blocks;
	boolean completed;

	public MonumentObjective(String name, Material[] materials, Region region, SporkTeam team, int completion) {
		super(name, team);
		this.color = team.getColor();
		this.materials = materials;
		this.region = region;
		this.completion = completion;
	}

	public Material[] getMaterials() {
		return materials;
	}

	public Region getRegion() {
		return region;
	}

	public List<MonumentBlock> getBlocks() {
		return blocks;
	}

	public List<MonumentBlock> getBrokenBlocks() {
		List<MonumentBlock> blocks = new ArrayList<>();

		for(MonumentBlock block : this.blocks) {
			if(block.isBroken()) {
				blocks.add(block);
			}
		}

		return blocks;
	}

	public MonumentBlock getBlock(Location location) {
		return getBlock(new BlockRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	public MonumentBlock getBlock(BlockRegion region) {
		if(blocks == null) return null;
		for(MonumentBlock block : blocks) {
			if(block.getBlock().isInside(region, false)) {
				return block;
			}
		}

		return null;
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

		StringBuilder builder = new StringBuilder();
		builder.append(color).append(name).append(ChatColor.GRAY).append(" was completed by ");

		int i = 1;
		int max = 3;
		Map<String, List<MonumentBlock>> ordered = getOrderedPlayerBlocks();

		List<String> toEnglish = new ArrayList<>();
		for(String string : ordered.keySet()) {
			if(i <= max) {
				String user = team.getColor() + string;
				String percentage = " " + ChatColor.GRAY + "(" + getCompletionPercentage(string) + "%)";
				toEnglish.add(user + percentage);
			} else if(i == max + 1) {
				int remaining = ordered.keySet().size() - max;
				String others = ChatColor.GRAY + "" + remaining + " others";
				toEnglish.add(others);
			}
		}

		builder.append(StringUtil.listToEnglishCompound(toEnglish));

		Bukkit.broadcastMessage(builder.toString());
		update();
	}


	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		MonumentBlock broken = getBlock(event.getLocation());

		if(event.isPlace()) {
			return;
		}

		/*
		for(MonumentBlock block : blocks) {
			Log.info("Monument handling Block change, @" + event.getRegion() + " compared to " + block.getBlock());
		}
		*/

		if(broken == null) {
			return;
		}

		if(isComplete()) {
			return;
		}

		if(!event.hasPlayer()) {
			event.setCancelled(true);
			return;
		}

		if(event.getPlayer().getTeam() != getTeam()) {
			event.setCancelled(true);
			event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "You can't break your own monument");
			return;
		}

		broken.setComplete(event.getPlayer(), true);
		setComplete(getCompletionPercentage() >= completion);
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
		return MonumentBuilder.class;
	}

	@Override
	public void start() {
		/*
		Thread.dumpStack();

		Log.info("Adding blocks");
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < materials.length; i++) {
			if(i != 0) {
				builder.append(", ");
			}

			builder.append(materials[i].name());
		}
		Log.info(builder.toString());
		*/

		blocks = new ArrayList<>();
		for(BlockRegion block : region.getValues(materials, team.getMap().getWorld())) {
			Log.info(block.toString());
			Log.info(String.valueOf(blocks.size()));
			blocks.add(new MonumentBlock(block));
		}
	}

	@Override
	public void stop() { /* nothing */ }

	public Map<String, List<MonumentBlock>> getOrderedPlayerBlocks() {
		Map<String, List<MonumentBlock>> map = new HashMap<>();
		Map<String, List<MonumentBlock>> list = getPlayerBlocks();

		while(list.size() > 0) {
			List<String> names = getHighest(list);
			for(String name : names) {
				map.put(name, list.get(name));
				list.remove(name);
			}
		}

		return map;
	}

	public List<String> getHighest(Map<String, List<MonumentBlock>> placements) {
		Object[] objectNames = placements.keySet().toArray();
		final String[] allNames = new String[objectNames.length];
		for(int i = 0; i < objectNames.length; i++) {
			allNames[i] = (String) objectNames[i];
		}

		List<String> names = new ArrayList<String>() {{
			add(allNames[0]);
		}};
		int highest = placements.get(names.get(0)).size();

		for(String name : allNames) {
			if(!names.contains(name)) {
				int score = placements.get(name).size();
				if(score > highest) {
					highest = score;
					names.clear();
					names.add(name);
				} else if(score == highest) {
					names.add(name);
				}
			}
		}

		return names;
	}

	public Map<String, List<MonumentBlock>> getPlayerBlocks() {
		Map<String, List<MonumentBlock>> map = new HashMap<>();

		for(MonumentBlock place : blocks) {
			if(map.containsKey(place.getPlayer().getName())) {
				map.get(place.getPlayer().getName()).add(place);
			} else {
				List<MonumentBlock> places = new ArrayList<>();
				places.add(place);
				map.put(place.getPlayer().getName(), places);
			}
		}

		return map;
	}

	public int getBrokenBlocks(SporkPlayer player) {
		return getBrokenBlocks(player.getName());
	}

	public int getBrokenBlocks(String name) {
		int i = 0;

		for(MonumentBlock place : blocks) {
			boolean match = place.getPlayer().getName().equalsIgnoreCase(name);
			// Log.info("Viewing placement by " + place.getPlayer().getName() + " attempting to match to " + name + " (" + match + ")");
			if(match) {
				i++;
			}
		}

		return i;
	}

	public int getCompletionPercentage() {
		double completed = getBrokenBlocks().size();
		double possible = getBlocks().size();

		double complete = completed / possible;
		double percent = complete * 100;
		return (int) percent;
	}

	public int getCompletionPercentage(SporkPlayer player) {
		return getCompletionPercentage(player.getName());
	}

	public int getCompletionPercentage(String name) {
		double completed = getBrokenBlocks(name);
		double possible = getBlocks().size();

		double complete = completed / possible;

		double percent = complete * 100;
		return (int) percent;
	}

}
