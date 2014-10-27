package io.sporkpgm.module.modules.tdm;

import io.seanbarker.trackerdeaths.event.DeathMessageEvent;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.objective.scored.ScoredBuilder;
import io.sporkpgm.objective.scored.ScoredObjective;
import io.sporkpgm.player.SporkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@ModuleInfo(name = "TeamDeathmatchModule", description = "Handles ScoredObjective Kill/Death scoring", requires = ScoredObjective.class, listener = true)
public class TeamDeathmatchModule extends Module {

	private ScoredObjective objective;
	private int value;
	private int natural;

	public TeamDeathmatchModule(ScoredObjective objective, int value, int natural) {
		this.objective = objective;
		this.value = value;
		this.natural = natural;
	}

	@EventHandler
	public void onDeath(DeathMessageEvent event) {
		SporkPlayer victim = SporkPlayer.getPlayer(event.getDeath().getVictim());
		SporkPlayer killer = (event.getDeath().getKiller() instanceof Player ? SporkPlayer.getPlayer((Player) event.getDeath().getKiller()) : null);
		if(killer != null) {
			if(killer.getTeam().getScored().equals(objective)) {
				objective.addScore(value);
			}
		} else {
			if(victim.getTeam().getScored().equals(objective)) {
				objective.takeScore(natural);
			}
		}
	}

	public Class<? extends Builder> builder() {
		return ScoredBuilder.class;
	}

}
