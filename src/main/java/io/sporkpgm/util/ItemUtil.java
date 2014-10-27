package io.sporkpgm.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class ItemUtil {

	public static Material stringToMaterial(String material) {
		Material mat = Material.matchMaterial(material.trim().replaceAll(" ", "_"));
		if(mat != null) {
			return mat;
		} else {
			return null;
		}
	}

	public static Color hexToColor(String hex) {
		return Color.fromRGB(java.awt.Color.decode("#" + hex).getRGB());
	}

	public static boolean isLeatherArmor(Material mat) {
		return (mat == Material.LEATHER_BOOTS || mat == Material.LEATHER_LEGGINGS ||
				mat == Material.LEATHER_CHESTPLATE || mat == Material.LEATHER_HELMET);
	}

	public static void setName(ItemStack item, String name) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
	}

	public static void setLore(ItemStack item, List<String> lore) {
		ItemMeta im = item.getItemMeta();
		im.setLore(lore);
		item.setItemMeta(im);
	}

	public static void setColor(ItemStack item, Color color) {
		if(!isLeatherArmor(item.getType()))
			return;
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		im.setColor(color);
		item.setItemMeta(im);
	}
}
