package io.sporkpgm.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtil {

	/**
	 * Shorthand for listToEnglishCompound(list, "", "")
	 *
	 * @see StringUtil#listToEnglishCompound
	 */
	public static String listToEnglishCompound(Collection<String> list) {
		return listToEnglishCompound(list, "", "");
	}

	/**
	 * Converts a list of strings to make a nice English list as a string.
	 *
	 * @param list   List of strings to concatenate.
	 * @param prefix Prefix to add before each element in the resulting string.
	 * @param suffix Suffix to add after each element in the resulting string.
	 * @return String version of the list of strings.
	 */
	public static String listToEnglishCompound(Collection<?> list, String prefix, String suffix) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(Object str : list) {
			if(i != 0) {
				if(i == list.size() - 1) {
					builder.append(" and ");
				} else {
					builder.append(", ");
				}
			}

			builder.append(prefix).append(str).append(suffix);
			i++;
		}

		return builder.toString();
	}

	public static DyeColor convertStringToDyeColor(String string) {
		if(string == null)
			return null;
		for(DyeColor dye : DyeColor.values()) {
			if(dye.name().replaceAll("_", " ").equalsIgnoreCase(string) || dye.name().equalsIgnoreCase(string)) {
				return dye;
			}
		}

		return null;
	}

	public static ChatColor convertDyeColorToChatColor(DyeColor dye) {
		switch(dye) {
			case WHITE:
				return ChatColor.WHITE;
			case ORANGE:
				return ChatColor.GOLD;
			case MAGENTA:
				return ChatColor.LIGHT_PURPLE;
			case LIGHT_BLUE:
				return ChatColor.AQUA;
			case YELLOW:
				return ChatColor.YELLOW;
			case LIME:
				return ChatColor.GREEN;
			case PINK:
				return ChatColor.LIGHT_PURPLE;
			case GRAY:
				return ChatColor.GRAY;
			case SILVER:
				return ChatColor.GRAY;
			case CYAN:
				return ChatColor.DARK_AQUA;
			case PURPLE:
				return ChatColor.DARK_PURPLE;
			case BLUE:
				return ChatColor.BLUE;
			case BROWN:
				return ChatColor.GOLD;
			case GREEN:
				return ChatColor.DARK_GREEN;
			case RED:
				return ChatColor.DARK_RED;
			case BLACK:
				return ChatColor.BLACK;
		}

		return ChatColor.WHITE;
	}

	public static ChatColor convertStringToChatColor(String string) {
		if(string == null)
			return null;
		for(ChatColor color : ChatColor.values()) {
			if(color.name().replaceAll("_", " ").equalsIgnoreCase(string) || color.name().equalsIgnoreCase(string)) {
				return color;
			}
		}

		return null;
	}

	public static String genPrefix(String prefix, ChatColor color) {
		return ChatColor.WHITE + "[" + color + prefix + ChatColor.WHITE + "] ";
	}

	public static Integer convertStringToInteger(String string) throws NumberFormatException {
		if(string == null) {
			throw new NumberFormatException(string);
		}

		return Integer.parseInt(string);
	}

	public static Integer convertStringToInteger(String string, int fallback) {
		if(string == null)
			return fallback;
		try {
			return convertStringToInteger(string);
		} catch(NumberFormatException e) {
			return fallback;
		}
	}

    public static boolean convertStringToBoolean(String string, boolean def) {
        if(string == null) {
            return def;
        }

        return string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("on") || string.equalsIgnoreCase("true");
    }

	public static EntityType convertStringToEntityType(String string) {
		if(string == null)
			return null;

		for(EntityType type : EntityType.values()) {
			if(type.name().replaceAll("_", " ").equalsIgnoreCase(string) || type.name().equalsIgnoreCase(string)) {
				return type;
			}
		}

		return null;
	}

    public static String staffList(List<String> values, ChatColor suffix) {
        String text = "";
        if(values.size() == 1)
            text += values.get(0);
        else if(values.size() >= 2) {
            int index = 0;
            while(index < values.size()) {
                if(index > 0) {
                    text += suffix;
                    if(index == (values.size() -1))
                        text += ", ";
                    else text += ", ";
                }

                text += ChatColor.RED + values.get(index);
                index++;
            }
        }

        return text + suffix;
    }

	public static Material convertStringToMaterial(String string) {
		if(string == null)
			return null;

		for(Material option : Material.values()) {
			if(option.name().replaceAll("_", " ").equalsIgnoreCase(string) || option.name().equalsIgnoreCase(string)) {
				return option;
			}
		}

		return Material.AIR;
	}

	public static Enchantment convertStringToEnchantment(String string) {
		if(string == null)
			return null;

		for(Enchantment option : Enchantment.values()) {
			if(option.getName().replaceAll("_", " ").equalsIgnoreCase(string) || option.getName().equalsIgnoreCase(string)) {
				return option;
			}
		}

		return null;
	}

	public static PotionEffectType convertStringToPotionEffectType(String string) {
		if(string == null)
			return null;

		for(PotionEffectType option : PotionEffectType.values()) {
			if(option != null && option.getName() != null && (option.getName().replaceAll("_",
					" ").equalsIgnoreCase(string) || option.getName().equalsIgnoreCase(string))) {
				return option;
			}
		}

		return null;
	}

	public static Color convertHexStringToColor(String string) {
		if(string == null)
			return null;
		if(!string.substring(0, 1).equals("#"))
			string = "#" + string;
		return Color.fromRGB(Integer.valueOf(string.substring(1, 3), 16), Integer.valueOf(string.substring(3, 5), 16),
				Integer.valueOf(string.substring(5, 7), 16));
	}

	public static String commaList(List<String> values) {
		return commaList("", values, "");
	}

	public static String commaList(ChatColor prefix, List<String> values, ChatColor suffix) {
		return commaList("" + prefix, values, "" + suffix);
	}

	public static String commaList(String prefix, List<String> values, String suffix) {
		String text = "";
		if(values.size() == 1) {
			text += prefix + values.get(0);
		} else if(values.size() >= 2) {
			/*
			 * index 0 should prefix ""
			 * index 1 to (max index - 1) should prefix ", "
			 * index max index should prefix " and "
			 */

			int index = 0;
			while(index < values.size()) {
				if(index > 0) {
					text += suffix;
					if(index == (values.size() - 1)) {
						text += " and ";
					} else {
						text += ", ";
					}
				}

				text += prefix + values.get(index);
				index++;
			}
		}

		return text + suffix;
	}

	public static String formatTime(int originalTime) {
		int time = originalTime;
		int hours = (time - time % (60 * 60)) / 60 / 60;
		String hS = "" + hours;
		if(hours < 10) {
			hS = "0" + hours;
		}

		time = time - (hours * 60 * 60);
		int minutes = (time - time % 60) / 60;
		String mS = "" + minutes;
		if(minutes < 10) {
			mS = "0" + minutes;
		}

		time = time - (minutes * 60);
		int seconds = time;
		String sS = "" + seconds;
		if(seconds < 10) {
			sS = "0" + seconds;
		}

		String text = mS + ":" + sS;
		if(hours > 0) {
			text = hS + ":" + text;
		}

		return text;
	}

	public static String technicalName(String original) {
		return original.toUpperCase().replace(" ", "_");
	}

	public static String fromTechnicalName(String original) {
		return original.replace("_", " ").toLowerCase();
	}

	public static String colorize(String string) {
		return string.replace("&", "§").replace("`", "§");
	}

	/**
	 * String color formatting: use lowercase ChatColor name prepended by '$'
	 * Example:
	 * ChatColor.RED + "Welcome to Athena MC!" + ChatColor.BLUE + " Check out our website at" + ChatColor.GREEN + " mcath.com"
	 * can be written as
	 * "$red Welcome to Athena MC! $blue Check out our website at $green mcath.com"
	 * (43 chars. shorter!)
	 */
	public static String colorFormat(String string) {
		Map<String, ChatColor> conversion = new HashMap<>();
		for(ChatColor color : ChatColor.values()) {
			conversion.put("$" + color.name().toLowerCase(), color);
		}
		for(String key : conversion.keySet()) {
			string = string.replace(key, conversion.get(key).toString());
		}
		return string;
	}

	public static String trim(String string, int length) {
		if(string.length() > 16) {
			string = string.substring(0, 16);
		}

		return string;
	}

}
