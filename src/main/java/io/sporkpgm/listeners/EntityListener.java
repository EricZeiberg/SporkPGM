package io.sporkpgm.listeners;

import io.sporkpgm.Spork;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.player.event.PlayerDealPlayerDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityListener implements Listener {

	@EventHandler
	public void entitySpawn(CreatureSpawnEvent event) {
		/*
		 * if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
		 * { event.setCancelled(true); }
		 */
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		SporkPlayer victim = SporkPlayer.getPlayer((Player) event.getEntity());
		if(!(event.getDamager() instanceof Player))
			return;
		SporkPlayer damager = SporkPlayer.getPlayer((Player) event.getDamager());

		PlayerDealPlayerDamageEvent pdpde = new PlayerDealPlayerDamageEvent(event, damager, victim);
		Spork.callEvent(pdpde);

		if(pdpde.isCancelled()) {
			event.setDamage(0);
			event.setCancelled(true);
		}
	}

}
