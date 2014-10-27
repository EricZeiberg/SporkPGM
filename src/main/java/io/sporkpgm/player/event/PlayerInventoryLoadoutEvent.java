package io.sporkpgm.player.event;

import com.google.common.base.Preconditions;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import io.sporkpgm.team.spawns.SporkSpawn;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInventoryLoadoutEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private SporkPlayer player;
	private SporkSpawn spawn;

	public PlayerInventoryLoadoutEvent(SporkPlayer player, SporkSpawn spawn) {
		this.player = player;
		this.spawn = spawn;
	}

	public PlayerInventoryLoadoutEvent(Player player, SporkTeam team) throws NullArgumentException {
		this.player = Preconditions.checkNotNull(SporkPlayer.getPlayer(player), "Player cannot be null");
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public SporkSpawn getSpawn() {
		return spawn;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
