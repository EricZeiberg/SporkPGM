package io.sporkpgm.rotation;

import com.google.common.base.Charsets;
import io.sporkpgm.Spork;
import io.sporkpgm.filter.exceptions.InvalidFilterException;
import io.sporkpgm.map.MapBuilder;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.exception.MapLoadException;
import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.rotation.exceptions.RotationLoadException;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.util.Config;
import io.sporkpgm.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Rotation {

	static int attempts = 0;
	static int maxAttempts = 5;

	boolean started;
	boolean cancel;

	int id = 0;
	int list = 0;
	int slot = 0;
	int current = 0;
	List<List<RotationSlot>> slots;

	Rotation(List<RotationSlot> slots) {
		this.slots = new ArrayList<>();
		this.id = 0;
		for(int i = 0; i < 5; i++) {
			List<RotationSlot> slotSet = new ArrayList<>();
			for(RotationSlot slot : slots) {
				id++;
				slotSet.add(new RotationSlot(slot.getLoader(), id));
			}
			this.slots.add(slotSet);
		}
	}

	public static Rotation provide() throws RotationLoadException, IOException {
		File rotation = Config.Rotation.ROTATION;
		if(rotation.isDirectory()) {
			throw new RotationLoadException("Unable to parse '" + rotation.getPath() + "' because it is a directory.");
		}

		if(!rotation.exists()) {
			create(rotation);
		}

		List<String> lines = Files.readAllLines(rotation.toPath(), Charsets.UTF_8);
		List<MapBuilder> loaders = new ArrayList<>();
		for(String rawLine : lines) {
			if(MapBuilder.getLoader(rawLine) == null) {
				Log.warning("Failed to find a map for '" + rawLine + "' in the rotation file");
				continue;
			}

			loaders.add(MapBuilder.getLoader(rawLine));
		}

		int id = 0;
		List<RotationSlot> slots = new ArrayList<>();
		for(MapBuilder builder : loaders) {
			slots.add(new RotationSlot(builder, id));
			id++;
		}

		if(lines.size() == 0 || slots.size() == 0) {
			if(lines.size() == 0) {
				Log.warning("Creating a new rotation.txt because the old one was empty");
			} else {
				Log.warning("Creating a new rotation.txt because the old one had no valid entries");
			}

			rotation.delete();
			create(rotation);
			attempts++;
			if(attempts > maxAttempts) {
				throw new RotationLoadException("Attempted to create the Rotation 5 times and failed every time");
			}
			return provide();
		}

		return new Rotation(slots);
	}

	private static void create(File rotation) throws IOException {
		FileWriter write = new FileWriter(rotation, false);
		PrintWriter printer = new PrintWriter(write);
		for(MapBuilder loader : Spork.getMaps()) {
			Log.info("Printing out " + loader.getName() + " into the Rotation file");
			printer.printf("%s" + "%n", loader.getName());
		}

		printer.close();
	}

	public void start() throws MapLoadException, ModuleLoadException, RotationLoadException, InvalidRegionException, InvalidFilterException {
		Match match = getCurrentSlot().load();
		this.started = true;
		match.start();
	}

	public void stop() {
		for(SporkPlayer player : SporkPlayer.getPlayers()) {
			player.getPlayer().kickPlayer("Server Restarting!");
		}

		getCurrentSlot().getMatch().stop();
	}

	public List<RotationSlot> getPreviousList() {
		return slots.get(list - 1);
	}

	public List<RotationSlot> getCurrentList() {
		return slots.get(list);
	}

	public List<RotationSlot> getNextList() {
		return slots.get(list + 1);
	}

	public RotationSlot getPreviousSlot() {
		try {
			return getCurrentList().get(slot - 1);
		} catch(IndexOutOfBoundsException e) {
			if(list == 0)
				return null;
			List<RotationSlot> slots = getPreviousList();
			return slots.get(slots.size() - 1);
		}
	}

	public Match getPreviousMatch() {
		return getPreviousSlot().getMatch();
	}

	public SporkMap getPrevious() {
		return getPreviousSlot().getMap();
	}

	public RotationSlot getCurrentSlot() {
		return getCurrentList().get(slot);
	}

	public Match getCurrentMatch() {
		return getCurrentSlot().getMatch();
	}

	public SporkMap getCurrent() {
		return getCurrentSlot().getMap();
	}

	public RotationSlot getNextSlot() {
		try {
			return getCurrentList().get(slot + 1);
		} catch(IndexOutOfBoundsException e) {
			if(list == (slots.size() - 1))
				return null;
			List<RotationSlot> slots = getNextList();
			return slots.get(0);
		}
	}

	public Match getNextMatch() {
		return getNextSlot().getMatch();
	}

	public SporkMap getNext() {
		return getNextSlot().getMap();
	}

	public List<MapBuilder> getMaps() {
		List<MapBuilder> maps = new ArrayList<>();

		for(List<RotationSlot> slots : this.slots)
			for(RotationSlot slot : slots)
				if(!maps.contains(slot.getLoader()))
					maps.add(slot.getLoader());

		return maps;
	}

	public int getMatchId() {
		return current;
	}

	public void cycle() {
		current++;

		try {
			slot++;
			getCurrentList().get(slot);
		} catch(IndexOutOfBoundsException e) {
			if(list == (slots.size() - 1))
				return;
			slot = 0;
			list++;
		}

		SporkTeam obs = getCurrent().getObservers();
		for(SporkPlayer player : SporkPlayer.getPlayers()) {
			player.setTeam(obs, false, true, true);
		}

		getPrevious().unload(getPreviousMatch());
	}

	public void setNext(MapBuilder map) {
		if(getCurrentMatch().getPhase() == MatchPhase.CYCLING) {
			getNextMatch().getMap().unload(getNextMatch());
		}

		id++;
		int current = this.current + 1;
		List<RotationSlot> list = getCurrentList().subList(0, current);
		// Log.info("list[" + 0 + ", " + (current) + "] = {" + list.size() + ": " + list + "}");

		list.add(new RotationSlot(map, id));
		try {
			if(getNext() != null) {
				list.addAll(getCurrentList().subList(current + 1, getCurrentList().size() - 1));
			}
		} catch(Exception e) { /* nothing */ }
		// Log.info("list = {" + list.size() + ": " + list + "}");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{started=" + started + ",cancel=" + cancel
				+ ",id=" + id + ",list=" + list + ",slot=" + slot + ",current=" + current + ",slots=(" + slots.size() + ") " + slots.toString() + "}";
	}

}
