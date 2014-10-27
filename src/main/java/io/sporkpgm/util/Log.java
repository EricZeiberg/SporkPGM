package io.sporkpgm.util;

import io.sporkpgm.Spork;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ALL")
public class Log {

	private static Logger log;

	private static boolean debug = false;

	static {
		Log.log = Spork.get().getLogger();
	}

	private static void log(Level lvl, String... msg) {
		for(String s : msg)
			log.log(lvl, s);
	}

	private static void log(String... msg) {
		log(Level.INFO, msg);
	}

	private static void log(Exception e) {
		e.printStackTrace();
	}

	public static void exception(Exception e) {
		log(e);
	}

	public static void info(String... msg) {
		log(msg);
	}

	public static void warning(String... msg) {
		log(Level.WARNING, msg);
	}

	public static void severe(String... msg) {
		log(Level.SEVERE, msg);
	}

	public static void debug(String msg) {
		if(debug)
			log("[DEBUG] " + msg);
	}

	public static void setDebugging(boolean debug) {
		Log.debug = debug;
		if(debug)
			log("Debugging on.");
	}

}