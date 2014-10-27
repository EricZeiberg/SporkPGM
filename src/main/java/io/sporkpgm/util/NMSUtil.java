package io.sporkpgm.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtil {

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", "," + "" + "").split(",")[3];
	}

	public static String getNMS() {
		return "net.minecraft.server." + getVersion();
	}

	public static String getCraftBukkit() {
		return "org.bukkit.craftbukkit." + getVersion();
	}

	public static Class<?> getClassNMS(String clazz) {
		String pack = getNMS() + (clazz.startsWith(".") ? clazz : "." + clazz);
		return getClass(pack);
	}

	public static Class<?> getClassBukkit(String clazz) {
		String pack = getCraftBukkit() + (clazz.startsWith(".") ? clazz : "." + clazz);
		return getClass(pack);
	}

	public static Class<?> getClass(String pack) {
		// Log.info("Package: " + pack);
		try {
			return Class.forName(pack);
		} catch(ClassNotFoundException ex) { /* nothing */ }
		return null;
	}

	public static Object getCraftPlayer(Player player) {
		try {
			return player.getClass().getMethod("getHandle").invoke(player);
		} catch(Exception ex) {
			return null;
		}
	}
}
