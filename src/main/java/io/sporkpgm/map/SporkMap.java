package io.sporkpgm.map;

import io.sporkpgm.Spork;
import io.sporkpgm.filter.AppliedRegion;
import io.sporkpgm.filter.Filter;
import io.sporkpgm.filter.FilterBuilder;
import io.sporkpgm.filter.exceptions.InvalidFilterException;
import io.sporkpgm.map.debug.VisibleRegion;
import io.sporkpgm.map.exception.MapLoadException;
import io.sporkpgm.map.generator.NullChunkGenerator;
import io.sporkpgm.match.Match;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleAbout;
import io.sporkpgm.module.ModuleStage;
import io.sporkpgm.module.builder.BuilderAbout;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.module.extras.InitModule;
import io.sporkpgm.module.extras.TaskedModule;
import io.sporkpgm.module.modules.info.Contributor;
import io.sporkpgm.module.modules.info.InfoModule;
import io.sporkpgm.module.modules.timer.TimerBuilder;
import io.sporkpgm.module.modules.timer.TimerModule;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.objective.scored.ScoredObjective;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.region.Region;
import io.sporkpgm.region.RegionBuilder;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.region.types.SearchRegion;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.team.SporkTeamBuilder;
import io.sporkpgm.team.spawns.SporkSpawn;
import io.sporkpgm.team.spawns.SporkSpawnBuilder;
import io.sporkpgm.team.spawns.kits.SporkKit;
import io.sporkpgm.team.spawns.kits.SporkKitBuilder;
import io.sporkpgm.util.FileUtil;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NMSUtil;
import io.sporkpgm.util.NumberUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SporkMap {

	protected MapBuilder builder;
	protected Document document;
	protected File folder;

	protected TimerModule timer;
	protected List<Module> modules;

	protected SporkTeam observers;
	protected List<SporkTeam> teams;
	protected List<SporkSpawn> spawns;
	protected List<SporkKit> kits;
	protected List<Region> regions;
	protected List<Filter> filters;

	protected World world;
	protected List<VisibleRegion> visible;
	protected Scoreboard scoreboard;
	protected Objective objective;
	protected SporkTeam winner;
	protected boolean ended;

	public SporkMap(MapBuilder builder) throws MapLoadException, ModuleLoadException, InvalidRegionException, InvalidFilterException {
		this.builder = builder;
		this.document = builder.getDocument();
		this.folder = builder.getFolder();
		Element root = document.getRootElement();

		this.scoreboard = Spork.get().getServer().getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("Objectives", "dummy");

		this.teams = SporkTeamBuilder.build(this);
		this.observers = SporkTeamBuilder.observers(this);

		this.filters = FilterBuilder.build(this);
		// filters();

		this.regions = new ArrayList<>();
		if(root.element("regions") != null) {
			this.regions = RegionBuilder.parseSubRegions(root.element("regions"));
			this.regions.addAll(filtered());
			search();
		}
		// regions();

		this.visible = visible(false);

		this.kits = builder.getKits();
		this.spawns = SporkSpawnBuilder.build(this);
		for(SporkTeam team : teams) {
			if(team.getSpawns().size() == 0) {
				throw new MapLoadException("No Spawns specified for " + team.getName());
			}
		}

		this.modules = builder.getModules();
		this.timer = ((TimerModule) getModule(TimerModule.class));
		loadModules();

		this.kits = SporkKitBuilder.build(document);
		if(kits == null) {
			this.kits = new ArrayList<>();
		}

		/*
		for(SporkTeam team : teams) {
			List<ObjectiveModule> fetched = team.getObjectives();
			// Log.info(team.getName() + ": " + fetched + " (" + fetched.size() + ")");

			List<ObjectiveModule> objectives = new ArrayList<>();
			for(Module module : modules) {
				// Log.info("Checking " + module.getClass().getSimpleName() + " to see if it is an instance of ObjectiveModule");
				if(module instanceof ObjectiveModule) {
					ObjectiveModule objective = (ObjectiveModule) module;
					// Log.info("'" + objective.getName() + "' has been found (" + objective.getTeam().getName().toUpperCase() + ")");
					if(objective.getTeam().equals(team)) {
						// Log.info("Found '" + objective.getName() + "' for " + objective.getTeam().getName().toUpperCase());
						objectives.add(objective);
					}
				}
			}
			// Log.info(team.getName() + ": " + objectives + " (" + objectives.size() + ")");
		}
		*/
	}

	private void filters() {
		for(Filter filter : filters) {
			Log.info(filter(filter));
		}
	}

	private String filter(Filter filter) {
		return filter.getName() + ": " + filter.getClass().getSimpleName();
	}

	private void search() {
		for(Region region : regions) {
			if(region instanceof SearchRegion) {
				SearchRegion search = (SearchRegion) region;
				search.get(this);
			}
		}
	}

	private void regions() {
		for(Region region : regions) {
			String message = region(region);
			if(region instanceof AppliedRegion) {
				AppliedRegion filtered = (AppliedRegion) region;
				try {
					message = message + " (" + filtered.getValues().size() + ")";
				} catch(NullPointerException e) {
					message = message + " (Unknown)";
				}
			}

			Log.info(message);
		}
	}

	private String region(Region region) {
		return region.getName() + ": " + region.getClass().getSimpleName();
	}

	private List<AppliedRegion> filtered() throws InvalidRegionException {
		List<AppliedRegion> filtered = new ArrayList<>();

		Element regions = document.getRootElement().element("regions");
		for(Element region : XMLUtil.getElements(regions, "apply")) {
			AppliedRegion applied = RegionBuilder.parseFiltered(this, region);
			filtered.add(applied);
			// Log.info("Found an AppliedRegion (" + (applied instanceof AppliedRegion) + ") current total = " + filtered.size());
		}

		return filtered;
	}

	public boolean load(Match match) {
		String name = "match-" + match.getID();
		File copyto = new File(name);
		try {
			FileUtil.copy(getFolder(), copyto);
			File sessionLock = new File(copyto.getAbsolutePath() + "/session.lock");
			if(sessionLock.exists()) {
				FileUtil.delete(sessionLock);
			}
			File uid = new File(copyto.getAbsolutePath() + "/uid.dat");
			if(uid.exists()) {
				FileUtil.delete(uid);
			}
			File old = new File(copyto.getAbsolutePath() + "/level.dat_old");
			if(old.exists()) {
				FileUtil.delete(old);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}

		WorldCreator creator = new WorldCreator(name);
		creator.generator(new NullChunkGenerator());
		this.world = creator.createWorld();

		startModules(ModuleStage.LOAD);
		this.timer.setMatch(match);

		scoreboard();
		for(VisibleRegion region : visible) {
			region.set(world);
		}

		for(VisibleRegion region : visible) {
			Log.info("Using " + region.getMaterial().name() + ":" + region.getDye().name() + " for " + region.getRegion());
		}

		return true;
	}

	public boolean hasModule(Class<? extends Module> type) {
		for(Module module : modules)
			if(module.getClass() == type)
				return true;

		return false;
	}

	public List<ObjectiveModule> getObjectives() {
		// Log.info("There are " + modules.size() + " Modules to search through");
		List<ObjectiveModule> objectives = new ArrayList<>();

		for(Module module : modules) {
			if(module instanceof ObjectiveModule) {
				// Log.info("Found '" + ((ObjectiveModule) module).getName() + "' and registering it as an Objective");
				objectives.add((ObjectiveModule) module);
			}
		}

		return objectives;
	}

	public void scoreboard() {
		this.objective.setDisplayName(ChatColor.GOLD + "Objectives");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		int score = 1;
		for(SporkTeam team : getTeams()) {
			if(hasModule(ScoredObjective.class)) {
				this.objective.setDisplayName(ChatColor.GOLD + "Score");
				for(ObjectiveModule objective : team.getObjectives()) {
					if(objective.getClass() == ScoredObjective.class) {
						ScoredObjective scored = (ScoredObjective) objective;
						scored.setScore(1, 0);
					}
				}
			} else {
				if(score != 1) {
					String spaces = " ";
					String space = spaces;
					while(ScoreAPI.isSet(this.objective.getScore(space))) {
						spaces += " ";
						space = spaces;
					}

					this.objective.getScore(space).setScore(score + 1);
					this.objective.getScore(space).setScore(score);
					score++;
				}

				// Log.info(team.getName() + ": " + team.getObjectives().size() + " Objectives");
				List<ObjectiveModule> objectives = team.getObjectives();
				// Log.info(team.getName() + ": " + objectives);
				for(ObjectiveModule objective : objectives) {
					// Log.info("Setting score of " + ChatColor.stripColor(objective.getPlayer().getName()) + " to " + score);
					// Log.info(ChatColor.stripColor(objective.getPlayer().getName()) + ": " + objective);
					if(objective.getPlayer() != null) {
						this.objective.getScore(objective.getPlayer()).setScore(score);
						score++;
					}

				}
				objective.getScore(team.getPlayer()).setScore(score);
				score++;
			}
		}
	}

	public void start() {
		startModules(ModuleStage.START);
	}

	public void startModules(ModuleStage stage) {
		for(Module module : modules) {
			try {
				if(new BuilderAbout(module.builder()).getStage() != stage) {
					continue;
				}
			} catch(Exception e) { continue; }

			if(module.getInfo().isListener()) {
				Spork.registerListener(module);
			}

			if(module instanceof TaskedModule) {
				((TaskedModule) module).setTasks(true);
			}

			if(module instanceof InitModule) {
				((InitModule) module).start();
			}
		}
	}

	public boolean unload(Match match) {
		String name = "match-" + match.getID();
		File delete = new File(name);

		stopModules(ModuleStage.LOAD);
		World world = Spork.get().getServer().getWorld(name);
		Spork.get().getServer().unloadWorld(world, false);
		FileUtil.delete(delete);

		return true;
	}

	public void stop() {
		stopModules(ModuleStage.START);
	}

	public void stopModules(ModuleStage stage) {
		for(Module module : modules) {
			try {
				if(new BuilderAbout(module.builder()).getStage() != stage) {
					continue;
				}
			} catch(Exception e) { continue; }

			if(module.getInfo().isListener()) {
				Spork.unregisterListener(module);
			}

			if(module instanceof TaskedModule) {
				((TaskedModule) module).setTasks(false);
			}

			if(module instanceof InitModule) {
				((InitModule) module).stop();
			}
		}
	}

	public void loadModules() {
		modules.addAll(Spork.get().getModules(this));

		List<Module> remove = new ArrayList<>();
		for(Module module : modules) {
			ModuleAbout info = module.getInfo();
			if(info.getRequires().size() > 0) {
				boolean ignore = false;
				for(Class<? extends Module> type : info.getRequires()) {
					if(!remove.contains(module) && !ignore && !hasModule(type)) {
						remove.add(module);
						Log.warning("Removing " + module.getInfo().getName() + " because " + type.getName() + " was missing!");
					}
				}
			}
		}

		for(Module toRemove : remove) {
			modules.remove(toRemove);
		}
	}

	public Document getDocument() {
		return document;
	}

	public File getFolder() {
		return folder;
	}

	public MapBuilder getBuilder() {
		return builder;
	}

	public InfoModule getInfo() {
		return builder.getInfo();
	}

	public String getName() {
		return getInfo().getName();
	}

	public String getVersion() {
		return getInfo().getVersion();
	}

	public List<Contributor> getAuthors() {
		return getInfo().getAuthors();
	}

	public List<String> getAuthorNames() {
		List<String> names = new ArrayList<>();
		for(Contributor author : getAuthors()) {
			names.add(author.getUsername());
		}

		return names;
	}

	public List<Contributor> getContributors() {
		return getInfo().getContributors();
	}

	public SporkTeam getObservers() {
		return observers;
	}

	public TimerModule getTimer() {
		return timer;
	}

	public World getWorld() {
		return world;
	}

	public Objective getObjective() {
		return objective;
	}

	public List<SporkTeam> getTeams() {
		return teams;
	}

	public List<SporkTeam> getTeams(String string) {
		List<SporkTeam> test = new ArrayList<>();
		test.addAll(getTeams());
		test.add(getObservers());

		List<SporkTeam> teams = new ArrayList<>();
		for(SporkTeam team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.equalsIgnoreCase(string) || colour.equalsIgnoreCase(string)) {
					// Log.info("'" + string + "' equals ignore case '" + name + "' or '" + colour + "'");
					teams.add(team);
				}
			}
		}

		if(teams.size() > 0) {
			return teams;
		}

		for(SporkTeam team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.startsWith(string.toLowerCase()) || colour.startsWith(string.toLowerCase())) {
					// Log.info("'" + string.toLowerCase() + "' starts with '" + name + "' or '" + colour + "'");
					teams.add(team);
				}
			}
		}

		if(teams.size() > 0) {
			return teams;
		}

		for(SporkTeam team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.contains(string.toLowerCase()) || colour.contains(string.toLowerCase())) {
					// Log.info("'" + string.toLowerCase() + "' contains '" + name + "' or '" + colour + "'");
					teams.add(team);
				}
			}
		}

		return teams;
	}

	public SporkTeam getTeam(String string) {
		List<SporkTeam> teams = getTeams(string);
		if(teams.size() == 0)
			return null;
		return teams.get(0);
	}

	public List<SporkTeam> getLowestTeams() {
		List<SporkTeam> teams = new ArrayList<>();

		teams.add(this.teams.get(0));
		int low = this.teams.get(0).getPlayers().size();

		for(SporkTeam team : this.teams)
			if(!teams.contains(team))
				if(team.getPlayers().size() <= low)
					if(team.getPlayers().size() == low)
						teams.add(team);
					else {
						teams = new ArrayList<>();
						teams.add(team);
					}

		return teams;
	}

	public SporkTeam getLowestTeam() {
		List<SporkTeam> teams = getLowestTeams();
		if(teams.size() == 1)
			return teams.get(0);
		if(teams.size() == 0)
			return null;

		return teams.get(NumberUtil.getRandom(0, teams.size() - 1));
	}

	public List<SporkKit> getKits() {
		return kits;
	}

	public SporkKit getKit(String string) {
		if(kits == null) {
			return null;
		}

		for(SporkKit kit : kits) {
			String name = kit.getName();
			if(kit.getName() != null && name.equalsIgnoreCase(string))
				return kit;
		}

		return null;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public List<Region> getContainingRegions(Location location) {
		/*
		if(getName().equalsIgnoreCase("Race for Victory 2")) {
			String[] checks = new String[]{"blue-wool-rooms", "red-wool-rooms"};
			for(String check : checks) {
				try {
					UnionRegion region = (UnionRegion) getRegion(check);
					if(region == null) {
						Log.info(check + " does not exist!");
						continue;
					}

					Log.info("Checking '" + region.getName() + "' (" + region.getClass().getSimpleName() + ") for " + block);
					boolean inside = region.isInside(block, true);
					Log.info((inside ? "Found " : "Could not find ") + block + " inside '" + region.getName() + "'");
				} catch(ClassCastException e) {
					Log.info("Checked Region was not a Union Region");
				}
			}
		}
		*/

		BlockRegion block = new BlockRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		List<Region> regionList = new ArrayList<>();
		for(Region region : regions) {
			if(region.isInside(block)) {
				// Log.info(block + " was found inside '" + region.getName() + "' (" + region.getClass().getSimpleName() + ")");
				regionList.add(region);
			}
		}
		return regionList;
	}

	public Region getRegion(String string) {
		for(Region region : regions) {
			String name = region.getName();
			if(region.getName() != null && name.equalsIgnoreCase(string))
				return region;
		}

		return null;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public Filter getFilter(String string) {
		for(Filter filter : filters) {
			String name = filter.getName();
			if(filter.getName() != null && name.equalsIgnoreCase(string))
				return filter;
		}

		return null;
	}

	public List<SporkPlayer> getPlayers() {
		List<SporkPlayer> players = new ArrayList<>();

		for(SporkTeam team : teams)
			players.addAll(team.getPlayers());

		return players;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public List<SporkSpawn> getSpawns() {
		return spawns;
	}

	public SporkSpawn getSpawn(String name) {
		for(SporkSpawn spawn : getSpawns()) {
			if(spawn.getName().equalsIgnoreCase(name)) {
				return spawn;
			}
		}

		return null;
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Module> getModules(Class<? extends Module> clazz) {
		List<Module> modules = new ArrayList<>();

		for(Module module : this.modules) {
			if(module.getClass() == clazz) {
				modules.add(module);
			}
		}

		return modules;
	}

	public Module getModule(Class<? extends Module> clazz) {
		return getModules(clazz).get(0);
	}

	public void checkEnded() {
		if(!ended && getWinner() != null && !getWinner().isObservers()) {
			setEnded(true);
		}
	}

	public void setEnded(boolean ended) {
		this.ended = ended;

		if(this.winner == null) {
			this.winner = getObservers();
		}

	}

	public boolean hasEnded() {
		return ended;
	}

	public void setWinner(SporkTeam winner) {
		this.winner = winner;
	}

	public SporkTeam getWinner() {
		if(winner != null) {
			return winner;
		}


		for(SporkTeam team : getTeams()) {
			if(team.complete()) {
				this.winner = team;
				return team;
			}
		}

		return null;
	}

	public static SporkMap getMap() {
		return RotationSlot.getRotation().getCurrent();
	}

	public static class ScoreAPI {

		private static Class<?> CRAFT_SCORE = NMSUtil.getClassBukkit("scoreboard.CraftScore");
		private static Class<?> CRAFT_OBJECTIVE = NMSUtil.getClassBukkit("scoreboard.CraftObjective");
		private static Class<?> CRAFT_SCOREBOARD = NMSUtil.getClassBukkit("scoreboard.CraftScoreboard");
		private static Class<?> CRAFT_SCOREBOARD_COMPONENT = NMSUtil.getClassBukkit("scoreboard.CraftScoreboardComponent");
		private static Class<?> SCOREBOARD = NMSUtil.getClassNMS("Scoreboard");
		private static Class<?> SCOREBOARD_SCORE = NMSUtil.getClassNMS("ScoreboardScore");
		private static Class<?> SCOREBOARD_OBJECTIVE = NMSUtil.getClassNMS("ScoreboardObjective");

		public static boolean isSet(Score score) {
			try {
				return isSetException(score);
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		private static boolean isSetException(Score score) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
			Object craftScore = CRAFT_SCORE.cast(score);

			Object craftObjective = CRAFT_OBJECTIVE.cast(score.getObjective());
			Method craftHandle = CRAFT_OBJECTIVE.getDeclaredMethod("getHandle");
			craftHandle.setAccessible(true);
			Object craftObjectiveHandle = craftHandle.invoke(craftObjective);

			Field objective = CRAFT_SCORE.getDeclaredField("objective");
			objective.setAccessible(true);
			Object craftScoreboard = checkState(objective.get(craftScore));

			Field craftBoard = CRAFT_SCOREBOARD.getDeclaredField("board");
			craftBoard.setAccessible(true);
			Object scoreboard = craftBoard.get(craftScoreboard);
			Method playerObjectives = SCOREBOARD.getDeclaredMethod("getPlayerObjectives", String.class);
			playerObjectives.setAccessible(true);

			Field playerField = CRAFT_SCORE.getDeclaredField("playerName");
			playerField.setAccessible(true);
			String playerName = (String) playerField.get(craftScore);
			Map map = (Map) playerObjectives.invoke(scoreboard, playerName);

			// return objective.checkState().board.getPlayerObjectives(playerName).containsKey(objective.getHandle());
			return map.containsKey(craftObjectiveHandle);
		}

		public static void reset(Score score) {
			try {
				resetException(score);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		private static void resetException(Score score) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
			Object craftScore = CRAFT_SCORE.cast(score);

			Object craftObjective = CRAFT_OBJECTIVE.cast(score.getObjective());
			Method craftHandle = CRAFT_OBJECTIVE.getDeclaredMethod("getHandle");
			craftHandle.setAccessible(true);
			Object craftObjectiveHandle = craftHandle.invoke(craftObjective);

			Field objective = CRAFT_SCORE.getDeclaredField("objective");
			objective.setAccessible(true);
			Object craftScoreboard = checkState(objective.get(craftScore));

			/*
			Method checkState = CRAFT_OBJECTIVE.getDeclaredMethod("checkState");
			checkState.setAccessible(true);
			craftScoreboard = checkState.invoke(CRAFT_SCORE.getDeclaredField("objective").get(craftScore));
			*/

			Field scoreboardField = CRAFT_SCOREBOARD.getDeclaredField("board");
			scoreboardField.setAccessible(true);
			Object scoreboard = scoreboardField.get(craftScoreboard);
			Method playerObjectives = SCOREBOARD.getDeclaredMethod("getPlayerObjectives", String.class);
			playerObjectives.setAccessible(true);

			Field playerField = CRAFT_SCORE.getDeclaredField("playerName");
			playerField.setAccessible(true);
			String playerName = (String) playerField.get(craftScore);
			Map map = (Map) playerObjectives.invoke(scoreboard, playerName);

			if(map.remove(craftObjectiveHandle) == null) {
				// If they don't have a score to delete, don't delete it.
				return;
			}

			Method resetScores = SCOREBOARD.getDeclaredMethod("resetPlayerScores", String.class);
			resetScores.setAccessible(true);
			resetScores.invoke(scoreboard, playerName);

			for(Object key : map.keySet()) {
				Object value = map.get(key);
				Method playerScoreMethod = SCOREBOARD.getDeclaredMethod("getPlayerScoreForObjective", String.class, SCOREBOARD_OBJECTIVE);
				playerScoreMethod.setAccessible(true);
				Object scoreboardScore = playerScoreMethod.invoke(scoreboard, playerName, key);

				Method getScore = SCOREBOARD_SCORE.getDeclaredMethod("getScore");
				getScore.setAccessible(true);
				int setScoreTo = (int) getScore.invoke(value);

				Method setScore = SCOREBOARD_SCORE.getDeclaredMethod("setScore", int.class);
				setScore.setAccessible(true);
				setScore.invoke(scoreboardScore, setScoreTo);
			}

			/*
			CraftScoreboard myBoard = objective.checkState();
			Map<ScoreboardObjective, ScoreboardScore> savedScores = myBoard.board.getPlayerObjectives(playerName);
			if(savedScores.remove(objective.getHandle()) == null) {
				// If they don't have a score to delete, don't delete it.
				return;
			}
			myBoard.board.resetPlayerScores(playerName);
			for(Map.Entry<ScoreboardObjective, ScoreboardScore> e : savedScores.entrySet()) {
				myBoard.board.getPlayerScoreForObjective(playerName, e.getKey()).setScore(e.getValue().getScore());
			}
			*/
		}

		public static Object checkState(Object craftObjective) {
			try {
				return checkStateException(craftObjective);
			} catch(Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		public static Object checkStateException(Object craftObjective) throws NoSuchFieldException, IllegalAccessException {
			Object boardComponent = CRAFT_SCOREBOARD_COMPONENT.cast(craftObjective);

			Field scoreboard = CRAFT_SCOREBOARD_COMPONENT.getDeclaredField("scoreboard");
			scoreboard.setAccessible(true);
			Object craftBoard = scoreboard.get(boardComponent);

			if(craftBoard == null) {
				throw new IllegalStateException("Unregistered scoreboard component");
			}

			return craftBoard;

			/*
			CraftScoreboard scoreboard = this.scoreboard;
			if(scoreboard == null) {
				throw new IllegalStateException("Unregistered scoreboard component");
			}
			return scoreboard;
			*/
		}

	}

	public List<VisibleRegion> visible(boolean provide) {
		List<VisibleRegion> visible = new ArrayList<>();

		if(provide) {
			int material = 0;
			Material[] materials = new Material[]{Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.WOOL};

			short damage = 0;
			for(Region region : regions) {
				if(damage > 15) {
					material++;
					if(material >= materials.length) {
						material = 0;
					}
				}

				Material type = materials[material];
				visible.add(new VisibleRegion(region, type, damage));

				damage++;
			}
		}

		return visible;
	}

	@Override
	public String toString() {
		return "SporkMap{name=" + getName() + ",loaded=" + (world != null) + "}";
	}

}