package io.sporkpgm.module.modules.friendlyfire;

import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.team.SporkTeam;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@ModuleInfo(name = "FriendlyFireModule", description = "The module that toggles friendly fire", listener = true)
public class FriendlyFireModule extends Module {

	boolean enabled;

	public FriendlyFireModule(boolean enabled) {
		this.enabled = enabled;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDamageEvent(EntityDamageByEntityEvent event) {
		if(enabled) {
			return;
		}

		Player entity = null;
		Player damager = null;

		if(event.getEntity() instanceof Player) {
			entity = (Player) event.getEntity();
		}

		if(event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if(event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if(projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}

		if(damager == null || entity == null) {
			return;
		}

		SporkTeam victimTeam = SporkPlayer.getPlayer(entity).getTeam();
		SporkTeam attackerTeam = SporkPlayer.getPlayer(damager).getTeam();
		if(victimTeam == attackerTeam) {
			event.setCancelled(true);
		}
	}

	@Override
	public Class<? extends Builder> builder() {
		return FriendlyFireBuilder.class;
	}

}
