package io.sporkpgm.player;

import com.google.common.collect.Lists;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.player.event.PlayerAddEvent;
import io.sporkpgm.player.event.PlayerChatEvent;
import io.sporkpgm.player.event.PlayerInventoryLoadoutEvent;
import io.sporkpgm.player.event.PlayerJoinTeamEvent;
import io.sporkpgm.player.event.PlayerRemoveEvent;
import io.sporkpgm.player.rank.Permission;
import io.sporkpgm.player.rank.Rank;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.team.spawns.SporkSpawn;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NMSUtil;
import io.sporkpgm.util.SchedulerUtil;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffectType;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SporkPlayer implements Listener {

	static List<SporkPlayer> players = new ArrayList<>();

	public static SporkPlayer add(Player player) {
		return add(player, true);
	}

	public static SporkPlayer add(Player player, boolean check) {
		if(check && getPlayer(player) != null) {
			return getPlayer(player);
		}

		SporkPlayer about = new SporkPlayer(player);
		players.add(about);

		about.inventoryCheck.startRepeat(5);
		Spork.registerListener(about);
		Spork.callEvent(new PlayerAddEvent(player));
		return about;
	}

	public static boolean remove(SporkPlayer player) {
		return remove(player.getPlayer());
	}

	public static boolean remove(Player player) {
		if(getPlayer(player) == null) {
			return false;
		}

		SporkPlayer about = getPlayer(player);
		players.remove(getPlayer(player));

		about.inventoryCheck.stopRepeat();
		Spork.unregisterListener(about);
		Spork.callEvent(new PlayerRemoveEvent(player));
		return true;
	}

	public static SporkPlayer getPlayer(Player player) {
		for(SporkPlayer about : players)
			if(about.getPlayer() == player)
				return about;

		return add(player, false);
	}

	public static SporkPlayer getPlayer(String username) {
		for(SporkPlayer about : players)
			if(about.getPlayer().getName().equalsIgnoreCase(username))
				return about;

		for(SporkPlayer about : players)
			if(about.getPlayer().getName().startsWith(username))
				return about;

		for(SporkPlayer about : players)
			if(about.getPlayer().getName().contains(username))
				return about;

		return null;
	}

	public static List<SporkPlayer> getPlayers() {
		return players;
	}

	WeakReference<Player> player;
	String name;
	SporkTeam team;
	Inventory inventory;
    String nickname;
	List<PermissionAttachment> attachments;
	String uuid;

	boolean joined;
	int score;

	SchedulerUtil inventoryCheck;
	Map<Inventory, Inventory> inventories;
	int lives;

	List<Rank> ranks;

	SporkPlayer(Player player) {
		this.player = new WeakReference<>(player);
		this.ranks = new ArrayList<>();
		this.attachments = new ArrayList<>();
		this.name = player.getName();
		this.inventories = new HashMap<>();
		this.inventoryCheck = new SchedulerUtil(new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, false);
        this.nickname = player.getName();
	}

	public Player getPlayer() {
		return player.get();
	}

	public List<Rank> getRanks() {
		return ranks;
	}

	public Inventory getInventory() {
		updateInventory();
		return inventory;
	}

	public void updateInventory() {
		if(inventory == null) {
			inventory = getPlayer().getServer().createInventory(null, 45, getFullName());
		}

		int health = getPlayer().getHealth() <= 0 ? 1 : (int) getPlayer().getHealth();
		ItemStack healthBar = new ItemStack(Material.POTION, health, (short) 16389);
		PotionMeta potionMeta = (PotionMeta) healthBar.getItemMeta();
		potionMeta.setDisplayName(ChatColor.RED + "Health");
		potionMeta.setLore(Lists.newArrayList(new String[]{getPlayer().getFoodLevel() + " Health."}));
		healthBar.setItemMeta(potionMeta);

		int food = getPlayer().getFoodLevel() <= 0 ? 1 : getPlayer().getFoodLevel();
		ItemStack foodBar = new ItemStack(Material.SPECKLED_MELON, food, (short) 59);
		ItemMeta foodMeta = foodBar.getItemMeta();
		foodMeta.setDisplayName(ChatColor.GOLD + "Food");
		foodMeta.setLore(Lists.newArrayList(new String[]{getPlayer().getFoodLevel() + " Food."}));
		foodBar.setItemMeta(foodMeta);

		inventory.setItem(7, healthBar);
		inventory.setItem(8, foodBar);
		inventory.setItem(0, getPlayer().getInventory().getBoots());
		inventory.setItem(1, getPlayer().getInventory().getLeggings());
		inventory.setItem(2, getPlayer().getInventory().getChestplate());
		inventory.setItem(3, getPlayer().getInventory().getHelmet());

		for(int i = 0; i < getPlayer().getInventory().getContents().length; i++) {
			inventory.setItem(i < 9 ? 36 + i : i, getPlayer().getInventory().getContents()[i]);
		}
	}

	public PermissionAttachment getAttachment() {
		PermissionAttachment attachment = getPlayer().addAttachment(Spork.get());
		attachments.add(attachment);
		return attachment;
	}

	public boolean hasPermission(Permission permission) {
		return hasPermission(permission.getPermission());
	}

	public boolean hasPermission(String name) {
		return getPlayer().hasPermission(name);
	}

	public boolean addPermission(Permission permission) {
		if(hasPermission(permission)) {
			return false;
		}

		getAttachment().setPermission(permission.getPermission(), true);

		return true;
	}

	public boolean removePermission(Permission permission) {
		if(!hasPermission(permission)) {
			return false;
		}

		getAttachment().unsetPermission(permission.getPermission());

		return true;
	}

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

	public boolean addRank(Rank rank) {
		if(getRanks().contains(rank)) {
			return false;
		}

		getRanks().add(rank);
		//for(Permission perm : rank.getPermissions()) {
		//	addPermission(perm);
		//}
		return true;
	}

	public boolean removeRank(Rank rank) {
		if(!getRanks().contains(rank)) {
			return false;
		}

		getRanks().remove(rank);
		for(Permission perm : rank.getPermissions()) {
			if(!hasPermission(perm.getPermission())) {
				removePermission(perm);
			}
		}

		return true;
	}

	public boolean hasRank(Rank rank) {
		return getRanks().contains(rank);
	}

	public String getPrefix() {
		List<Rank> remove = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		for(Rank rank : ranks) {
			if(rank == null) {
				remove.add(rank);
				Log.info(getName() + " has a null rank - removing it");
				continue;
			}

			sb.append(rank.getPrefix());
		}

		for(Rank rank : remove) {
			ranks.remove(rank);
		}

		return sb.toString();
	}

	public String getFormat(String message, boolean team) {
		return message;
	}

	public SporkTeam getTeam() {
		return team;
	}

	public SporkTeam setTeam(SporkTeam team) {
		boolean display = getTeam() != null;
		boolean running = Spork.get().getRotation().getCurrentMatch().getPhase() == MatchPhase.PLAYING;
		boolean inventory = running || !display;
		boolean teleport = !display || running;
		return setTeam(team, display, inventory, teleport);
	}

	public SporkTeam setTeam(SporkTeam team, boolean display, boolean inventory, boolean teleport) {
		PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(this, team);
		Spork.callEvent(event);

		if(event.isCancelled()) {
			getPlayer().sendMessage(ChatColor.RED + event.getReason());
			return this.team;
		}

		this.team = team;

		SporkSpawn spawn = team.getSporkSpawn();

		if(display)
			display();
		if(inventory)
			inventory(spawn);
		if(teleport)
			teleport(spawn);

		getPlayer().setDisplayName(getPrefix() + team.getColor() + getPlayer().getName());
		getTeam().getTeam().addPlayer(getPlayer());
		getPlayer().setScoreboard(team.getMap().getScoreboard());

		return team;
	}

	public void display() {
		SporkTeam team = getTeam();
		if(team.isObservers())
			getPlayer().sendMessage(ChatColor.GRAY + "You are now " + ChatColor.AQUA + "Observing" + ChatColor.GRAY + ".");
		else
			getPlayer().sendMessage(ChatColor.GRAY + "You have joined the " + team.getColoredName() + ChatColor.GRAY + ".");
	}

	public void inventory(SporkSpawn spawn) {
		empty();
		boolean update = false;
		GameMode mode = GameMode.CREATIVE;
		String[] perms = {"worldedit.navigation.jump.*", "worldedit.navigation.thru.*", "commandbook.teleport"};
		if(getTeam().isObservers() || !RotationSlot.getRotation().getCurrentMatch().isRunning()) {
			ItemStack compass = new ItemStack(Material.COMPASS);
			ItemMeta meta = compass.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "Teleportation Device");
			compass.setItemMeta(meta);
			getPlayer().getInventory().setItem(0, compass);
			for(String permission : perms)
				addPermission(new Permission(permission, 0));
		} else {
			update = true;
			mode = GameMode.SURVIVAL;
			if(spawn.hasKit()) {
				Log.info("SporkSpawn.hasKit() == true... Applying kit!");
				spawn.getKit().apply(this);
			}
			for(String permission : perms)
				removePermission(new Permission(permission, 0));
		}

		try {
			// getPlayer().setAffectsSpawning(update);
			Method spawning = Player.class.getMethod("setAffectsSpawning", boolean.class);
			spawning.setAccessible(true);
			spawning.invoke(getPlayer(), update);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, skipping affects spawning...");
		}

		try {
			// getPlayer().setCollidesWithEntities(update);
			Method collides = Player.class.getMethod("setCollidesWithEntities", boolean.class);
			collides.setAccessible(true);
			collides.invoke(getPlayer(), update);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, skipping collides with entities...");
		}

		try {
			// getPlayer().setArrowsStuck(0);
			Method arrows = Player.class.getMethod("setArrowsStuck", int.class);
			arrows.setAccessible(true);
			arrows.invoke(getPlayer(), 0);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, attempting to set arrows stuck manually...");

			try {
			/*
			 * CraftPlayer player = (CraftPlayer) getPlayer();
			 * player.getHandle().p(0);
			 *
			 * Set a players Arrows Stuck to 0
			 */

				Player player = getPlayer();
				Object craft = NMSUtil.getClassBukkit("entity.CraftPlayer").cast(player);
				Method method = NMSUtil.getClassBukkit("entity.CraftPlayer").getMethod("getHandle");
				method.setAccessible(true);
				Object handle = method.invoke(craft);
				method = NMSUtil.getClassNMS("EntityLiving").getMethod("p", int.class);
				method.setAccessible(true);
				method.invoke(NMSUtil.getClassNMS("EntityLiving").cast(handle), 0);
			} catch(Exception e2) {
				Log.warning("Failed to set Arrows Stuck manually");
				e2.printStackTrace();
			}
		}

		getPlayer().setCanPickupItems(update);
		getPlayer().setFireTicks(0);
		getPlayer().setFallDistance(0);
		getPlayer().setExp(0);
		getPlayer().setLevel(0);
		getPlayer().setHealth(20);
		getPlayer().setFoodLevel(20);
		getPlayer().setSaturation(20);
		getPlayer().setGameMode(mode);
		PlayerInventoryLoadoutEvent event = new PlayerInventoryLoadoutEvent(this, spawn);
		Spork.callEvent(event);

		vanish();
	}

	public void teleport(SporkSpawn spawn) {
		try {
			getPlayer().teleport(spawn.getSpawn());
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
			teleport(spawn);
		}
	}

	public void empty() {
		ItemStack air = new ItemStack(Material.AIR, 0);

		clearPotionEffects();

		boolean cancel = false;
		for(int i = 0; !cancel; i++) {
			try {
				getPlayer().getInventory().setItem(i, air);
			} catch(Exception e) {
				cancel = true;
			}
		}

		getPlayer().getInventory().setHelmet(air);
		getPlayer().getInventory().setChestplate(air);
		getPlayer().getInventory().setLeggings(air);
		getPlayer().getInventory().setBoots(air);
		getPlayer().updateInventory();
	}

	public void clearPotionEffects() {
		clearPotionEffects(PotionEffectType.values());
	}

	public void clearPotionEffects(PotionEffectType[] types) {
		for(PotionEffectType type : types)
			if(type != null)
				try {
					if(getPlayer().hasPotionEffect(type) && getPlayer() != null)
						getPlayer().removePotionEffect(type);
				} catch(NullPointerException e) {
					Log.warning("NullPointerException thrown when trying to remove '" + type.getName() + "' from '" + getPlayer().getName() + "'");
				}
	}

	public boolean isObserver() {
		return getTeam() == null || getTeam().isObservers();
	}

	public boolean isParticipating() {
		if(getTeam() == null || getTeam().isObservers()) {
			return false;
		}

		if(!RotationSlot.getRotation().getCurrentMatch().isRunning()) {
			return false;
		}

		return true;
	}

	public ChatColor getTeamColour() {
		return getTeam() == null ? ChatColor.AQUA : getTeam().getColor();
	}

	public String getFullName() {
		return getPrefix() + getTeamColour() + getName();
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int addLife() {
		return addLives(1);
	}

	public int addLives(int lives) {
		this.lives = this.lives + lives;
		return this.lives;
	}

	public int takeLife() {
		return takeLives(1);
	}

	public int takeLives(int lives) {
		this.lives = this.lives - lives;
		return this.lives;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int addScore() {
		return addScore(1);
	}

	public int addScore(int amount) {
		this.score = score + amount;
		return this.score;
	}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String newdm = e.getDeathMessage().replaceAll(getPlayer().getName(), getNickname());
        e.setDeathMessage(newdm);
    }

    @EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(event.getPlayer() != getPlayer())
			return;
		event.setCancelled(true);
		PlayerChatEvent pce = new PlayerChatEvent(this, event.getMessage(), true);
		Spork.callEvent(pce);
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if(event.getPlayer() != this)
			return;
		if(event.getPlayer().getTeam() == null) {
			Log.info(event.getPlayer().getPlayer().getName() + "'s team was null!");
			setTeam(Spork.get().getRotation().getCurrent().getObservers());
		}

		boolean team = event.isTeam() && getTeam() != null;
		String pre = (team ? getTeamColour() + "[Team] " : "");
		String full = pre + getTeamColour() + getPrefix() + getTeamColour() + getNickname() + ChatColor.WHITE + ": " + event.getMessage();
		if(team) {
			for(SporkPlayer player : getTeam().getPlayers()) {
				player.getPlayer().sendMessage(full);
			}
		} else {
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(full);
			}
		}
		Bukkit.getConsoleSender().sendMessage(full);
	}

	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		if(event.getPlayer() != this)
			return;

		Match match = RotationSlot.getRotation().getCurrentMatch();
		if(isObserver() || !match.isRunning())
			event.setCancelled(true);
	}

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String newqm = e.getQuitMessage().replaceAll(getPlayer().getName(), getNickname());
        e.setQuitMessage(newqm);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String newjm = e.getJoinMessage().replaceAll(getPlayer().getName(), getNickname());
        e.setJoinMessage(newjm);
    }

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(event.getPlayer() != getPlayer())
			return;
		SporkSpawn spawn = getTeam().getSporkSpawn();
		event.setRespawnLocation(spawn.getSpawn());
		inventory(spawn);
	}

	public void open(Inventory inventory) {
		Inventory fake = Bukkit.createInventory(getPlayer(), inventory.getSize(), inventory.getTitle());
		fake.setContents(inventory.getContents());
		getPlayer().openInventory(fake);
		inventories.put(inventory, fake);
	}

	public void close(Inventory inventory) {
		inventories.remove(inventory);
	}

	public void update() {
		for(Inventory key : inventories.keySet()) {
			Inventory view = inventories.get(key);
			if(key.getHolder() instanceof Chest) {
				Chest chest = (Chest) key.getHolder();
				if(chest.getLocation().getBlock().getState() instanceof Chest == false) {
					getPlayer().closeInventory();
					continue;
				}
			}

			view.setContents(key.getContents());
		}
	}

	public static void vanish() {
		Match match = RotationSlot.getRotation().getCurrentMatch();
		for(SporkPlayer player : getPlayers()) {
			if(player.getTeam() == null) {
				continue;
			}

			SporkMap map = player.getTeam().getMap();
			List<SporkPlayer> observers = map.getObservers().getPlayers();
			List<SporkPlayer> players = new ArrayList<>();
			for(SporkTeam team : map.getTeams())
				players.addAll(team.getPlayers());

			if(match.isRunning()) {
				for(SporkPlayer observer : observers)
					for(SporkPlayer update : getPlayers())
						try {
							observer.getPlayer().showPlayer(update.getPlayer());
						} catch(IllegalStateException e) {
							e.printStackTrace();
						}
				for(SporkPlayer other : players) {
					for(SporkPlayer update : observers)
						try {
							other.getPlayer().hidePlayer(update.getPlayer());
						} catch(IllegalStateException e) {
							e.printStackTrace();
						}
					for(SporkPlayer update : players)
						try {
							other.getPlayer().showPlayer(update.getPlayer());
						} catch(IllegalStateException e) {
							e.printStackTrace();
						}
				}
			} else {
				for(SporkPlayer observer : getPlayers())
					for(SporkPlayer update : getPlayers())
						try {
							observer.getPlayer().showPlayer(update.getPlayer());
						} catch(IllegalStateException e) {
							e.printStackTrace();
						}
			}
		}
	}

	public String getName() {
		return name;
	}
}
