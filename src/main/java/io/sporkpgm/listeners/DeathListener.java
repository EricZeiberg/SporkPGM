package io.sporkpgm.listeners;

import io.seanbarker.trackerdeaths.event.DeathMessageEvent;
import io.seanbarker.trackerdeaths.messages.SimpleDeathMessageBuilder;
import io.sporkpgm.player.SporkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DeathListener implements Listener {

	@EventHandler
	public void onDeath(DeathMessageEvent event) {
		event.setBuilder(new SporkDeathMessageBuilder());
	}

	public class SporkDeathMessageBuilder extends SimpleDeathMessageBuilder {

		@Override
		public String getName(Player player) {
			SporkPlayer spork = SporkPlayer.getPlayer(player);
			return spork.getTeamColour() + player.getName() + ChatColor.GRAY;
		}

	}

}
