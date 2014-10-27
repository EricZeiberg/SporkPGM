package io.sporkpgm.team.spawns.kits;

import com.google.common.collect.Lists;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.util.ItemUtil;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SporkKitBuilder {

	public static List<SporkKit> build(Document document) throws ModuleLoadException {
		Element root = document.getRootElement();
		List<SporkKit> sporks = new ArrayList<>();

		Element kits = root.element("kits");
		for(Element element : XMLUtil.getElements(kits, "kit")) {
			sporks.add(parseKit(element));
		}

		for(SporkKit kit : sporks) {
			String parents = kit.getElement().attributeValue("parents");
			if(parents == null) {
				kit.setParents(new ArrayList<SporkKit>());
				continue;
			}

			String[] names;
			if(parents.contains(";")) {
				names = parents.split(";");
			} else {
				names = new String[]{parents};
			}

			List<SporkKit> parents1 = new ArrayList<>();
			for(String name : names) {
				SporkKit parent = getKit(name, sporks);
				if(parent == null) {
					throw new ModuleLoadException("Could not find kit with the name '" + name + "' for '" + kit.getName() + "'");
				}

				parents1.add(parent);
			}

			kit.setParents(parents1);
		}

		return sporks;
	}

	public static SporkKit getKit(String name, List<SporkKit> kits) {
		for(SporkKit kit : kits) {
			if(kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

	public static SporkKit parseKit(Element root) throws ModuleLoadException {
		String name = root.attributeValue("name");

		String[] item = new String[]{"item"};
		List<KitItem> items = new ArrayList<>();

		String[] potion = new String[]{"potion"};
		List<PotionEffect> potions = new ArrayList<>();

		String[] armour = new String[]{"helmet", "chestplate", "leggings", "boots"};
		List<KitArmor> armours = new ArrayList<>();

		for(Element element : XMLUtil.getElements(root, item)) {
			int slot = parseSlot(element, name);
			ItemStack stack = parseItem(element, name);
			items.add(new KitItem(slot, stack));
		}

		for(Element element : XMLUtil.getElements(root, armour)) {
			KitArmorSlot slot = parseArmorSlot(element, name);
			ItemStack stack = parseItem(element, name);
			armours.add(new KitArmor(slot, stack));
		}

		for(Element element : XMLUtil.getElements(root, potion)) {
			PotionEffect effect = parsePotion(element, name);
			potions.add(effect);
		}

		return new SporkKit(root, name, items, potions, armours);
	}

	public static ItemStack parseItem(Element element, String name) throws ModuleLoadException {
		short damage = parseDamage(element, name);
		int amount = parseAmount(element, name);
		Material material = parseMaterial(element, name);
		Map<Enchantment, Integer> enchantments = parseEnchantments(element, name);

		ItemStack stack = new ItemStack(material, amount, damage);
		if(enchantments != null)
			stack.addUnsafeEnchantments(enchantments);

		parseColor(stack, element, name);
		parseLore(stack, element);
		return stack;
	}

	public static PotionEffect parsePotion(Element element, String name) throws ModuleLoadException {
		PotionEffectType type = StringUtil.convertStringToPotionEffectType(element.getText());
		if(type == null) {
			throw new ModuleLoadException(element, "Invalid potion type: '" + element.getText() + "' for '" + name + "'");
		}
		String rawDuration = element.attributeValue("duration");
		String rawAmplifier = element.attributeValue("amplifier");
		if(rawDuration == null) {
			throw new ModuleLoadException(element, "Potion duration or amplifier cannot be blank for '" + name + "'");
		}
		int duration = (rawDuration.equals("oo") ? Integer.MAX_VALUE : Integer.parseInt(rawDuration));
		int amplifier = (rawAmplifier != null ? Integer.parseInt(rawAmplifier) : 0);
		boolean ambient = XMLUtil.parseBoolean(element.attributeValue("ambient"), false);
		return new PotionEffect(type, duration, amplifier, ambient);
	}

	public static void parseColor(ItemStack stack, Element element, String name) throws ModuleLoadException {
		if(element.attributeValue("color") == null) {
			return;
		}

		if(!supportsColor().contains(stack.getType())) {
			String material = StringUtils.capitalize(stack.getType().name().replace("_", " "));
			throw new ModuleLoadException(element, "'" + material + "' can't have a color set for '" + name + "'");
		}

		String attribute = element.attributeValue("color");
		Color color;

		try {
			color = StringUtil.convertHexStringToColor(attribute);
		} catch(Exception e) {
			throw new ModuleLoadException(element, "Invalid Color supplied for '" + name + "'");
		}

		ItemUtil.setColor(stack, color);
	}

	public static List<Material> supportsColor() {
		Material[] supports = new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};
		return Lists.newArrayList(supports);
	}

	public static KitArmorSlot parseArmorSlot(Element element, String name) throws ModuleLoadException {
		KitArmorSlot slot = KitArmorSlot.getSlot(element.getName());

		if(slot == null) {
			throw new ModuleLoadException(element, "Unable to find armour slot named '" + element.getName() + "' for '" + name + "'");
		}

		return slot;
	}

	public static int parseSlot(Element element, String name) throws ModuleLoadException {
		int slot;
		try {
			slot = Integer.parseInt(element.attributeValue("slot"));
		} catch(NumberFormatException | NullPointerException e) {
			throw new ModuleLoadException(element, "Could not parse kit because slot was not a valid number for '" + name + "'");
		}

		return slot;
	}

	public static short parseDamage(Element element, String name) throws ModuleLoadException {
		short damage;
		try {
			String string = element.attributeValue("damage");
			if(string == null) {
				throw new NullPointerException(string);
			}

			damage = (short) Integer.parseInt(string);
		} catch(NumberFormatException e) {
			throw new ModuleLoadException(element, "Could not parse kit because damage was provided but was not a valid number for '" + name + "'");
		} catch(NullPointerException e) {
			damage = 0;
		}

		return damage;
	}

	public static int parseAmount(Element element, String name) throws ModuleLoadException {
		int amount;
		try {
			String string = element.attributeValue("amount");
			if(string == null) {
				throw new NullPointerException(string);
			}

			amount = Integer.parseInt(string);
		} catch(NumberFormatException e) {
			throw new ModuleLoadException(element, "Could not parse kit because amount was provided but was not a valid number for '" + name + "'");
		} catch(NullPointerException e) {
			amount = 1;
		}

		return amount;
	}

	public static Material parseMaterial(Element element, String name) throws ModuleLoadException {
		Material material = StringUtil.convertStringToMaterial(element.getText());
		if(material == null) {
			throw new ModuleLoadException(element, "Invalid Material name supplied for '" + name + "'");
		}

		return material;
	}

	public static Map<Enchantment, Integer> parseEnchantments(Element element, String name) throws ModuleLoadException {
		if(element.attributeValue("enchantment") == null) {
			return new HashMap<>();
		}

		String enchants = element.attributeValue("enchantment");
		Map<Enchantment, Integer> res = new HashMap<>();
		for(String string : enchants.split(";")) {
			Enchantment enchantment;
			int level;

			if(string.split(":").length < 2) {
				enchantment = StringUtil.convertStringToEnchantment(string);
				level = 1;
			} else {
				enchantment = StringUtil.convertStringToEnchantment(string.split(":")[0]);
				level = XMLUtil.parseInteger(string.split(":")[1]);
			}

			if(enchantment == null) {
				throw new ModuleLoadException(element, "Invalid enchantment: '" + string + "' for '" + name + "'");
			}

			res.put(enchantment, level);
		}
		return res;
	}

	public static void parseLore(ItemStack stack, Element element) {
		String rawLore = element.attributeValue("lore");
		String name = element.attributeValue("name");

		if(name != null) {
			name = StringUtil.colorize(name);
			ItemUtil.setName(stack, name);
		}

		if(rawLore != null) {
			List<String> lore = new ArrayList<>();
			for(String s : rawLore.split("|")) {
				lore.add(StringUtil.colorize(s));
			}
			ItemUtil.setLore(stack, lore);
		}
	}

}
