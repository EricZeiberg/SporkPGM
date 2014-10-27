package io.sporkpgm.team.spawns.kits;

import com.google.common.collect.Lists;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.dom4j.Element;

import java.util.List;

public class SporkKit {

	private Element element;

	private String name;
	private List<KitItem> items;
	private List<PotionEffect> potions;
	private List<KitArmor> armors;
	private List<SporkKit> parents;

	public SporkKit(Element element, String name, List<KitItem> items) {
		this(element, name, items, null, null, null);
	}

	public SporkKit(Element element, String name, List<KitItem> items, List<PotionEffect> potions) {
		this(element, name, items, potions, null, null);
	}

	public SporkKit(Element element, String name, List<KitItem> items, List<PotionEffect> potions, List<KitArmor> armor) {
		this(element, name, items, potions, null, armor);
	}

	public SporkKit(Element element, String name, List<KitItem> items, List<PotionEffect> potions, List<SporkKit> parents, List<KitArmor> armor) {
		this.element = element;
		this.name = name;
		this.items = items;
		this.potions = potions;
		this.armors = armor;

		if(parents != null && !parents.isEmpty()) {
			this.parents = parents;
		} else {
			this.parents = Lists.newArrayList();
		}
	}

	public void apply(SporkPlayer player) {
		apply(player.getPlayer());
	}

	public void apply(Player player) {
		PlayerInventory inv = player.getInventory();
		Log.info("Actually applying kit..." + name);

		if(items != null) {
			Log.info(items.size() + " items in '" + name + "'");
		}

		for(KitItem item : items) {
			inv.setItem(item.getSlot(), item.getItem().clone());
		}

		if(potions != null) {
			Log.info(potions.size() + " potions in '" + name + "'");
			// player.addPotionEffects(potions);
		}

		if(armors != null) {
			Log.info(armors.size() + " armor slots used in '" + name + "'");
			for(KitArmor armor : armors) {
				switch(armor.getSlot()) {
					case HELMET:
						inv.setHelmet(armor.getItem().clone());
					case CHESTPLATE:
						inv.setChestplate(armor.getItem().clone());
					case LEGGINGS:
						inv.setLeggings(armor.getItem().clone());
					case BOOTS:
						inv.setBoots(armor.getItem().clone());
				}
			}
		}
		if(parents != null) {
			Log.info(parents.size() + " parents applying in " + name);
			for(SporkKit kit : parents) {
				kit.apply(player);
			}
		}
	}

	public void apply(SporkTeam team) {
		for(SporkPlayer p : team.getPlayers()) {
			apply(p);
		}
	}

	public Element getElement() {
		return element;
	}

	public String getName() {
		return name;
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

	public List<KitItem> getItems() {
		return items;
	}

	public List<SporkKit> getParents() {
		return parents;
	}

	public boolean hasParents() {
		return parents.isEmpty();
	}

	public void setParents(List<SporkKit> parents) {
		this.parents = parents;
	}

	public void addParent(SporkKit parent) {
		parents.add(parent);
	}

	public void removeParent(SporkKit parent) {
		parents.remove(parent);
	}

	public void removeParentById(int id) {
		parents.remove(id);
	}

}
