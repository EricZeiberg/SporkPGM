package io.sporkpgm.module.modules.killreward;

import io.seanbarker.trackerdeaths.event.PlayerKillPlayerEvent;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.player.event.PlayerDealPlayerDamageEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
@ModuleInfo(name = "KillRewardModule", description = "Rewards kills with items", listener = true)
public class KillRewardModule extends Module{

    SporkMap map;
    List<KillRewardItem> materials;

    public KillRewardModule(SporkMap map, List<KillRewardItem> materials) {
        this.map = map;
        this.materials = materials;
    }

    @Override
    public Class<? extends Builder> builder() {
        return KillRewardBuilder.class;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (event.getEntity().getLastDamageCause().getEntity() != null && event.getEntity().getLastDamageCause().getEntity() instanceof Player){
            if (materials.size() == 0){
                Spork.get().getLogger().info("Materials are 0");
            }
            Player p = (Player)((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            for (KillRewardItem i : materials){
                p.getInventory().addItem(new ItemStack(i.material, i.amount));
                Spork.get().getLogger().info("Added a couple of items! " + i.material.name() + " " + i.amount + " to " + p.getName());
            }

        }
        else {
            Spork.get().getLogger().info("Credited is null");
        }
    }
}
