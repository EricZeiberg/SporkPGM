package io.sporkpgm.module.modules.tnt;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.modules.tnt.TNTBuilder;
import io.sporkpgm.module.modules.tnt.TNTSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
@ModuleInfo(name = "TNTModule", description = "Addes some settings to TNT")
public class TNTModule extends Module{

    SporkMap map;
    TNTSettings settings;

    public TNTModule(SporkMap map, TNTSettings settings) {
        this.map = map;
        this.settings = settings;
    }

    @Override
    public Class<? extends Builder> builder() {
        return TNTBuilder.class;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTNTPlace(BlockPlaceEvent event){
        if (event.getBlock().getType() == Material.TNT){
            if (settings.isInstantIgnite()){
                event.getBlock().setType(Material.AIR);
                TNTPrimed tnt = event.getBlock().getWorld().spawn(event.getBlock().getLocation().clone().add(new Location(event.getBlock().getWorld(), 0.5, 0.5, 0.5)), TNTPrimed.class);
                if(settings.getPower() >= 0) {
                    tnt.setYield((float) settings.getPower());
                }
                if(settings.getFuse() != null) {
                    tnt.setFuseTicks(Integer.parseInt(settings.getFuse()) * 20);
                }

                ItemStack inHand = event.getPlayer().getItemInHand();
                if(inHand.getAmount() == 1) {
                    event.getPlayer().setItemInHand(null);
                } else {
                    inHand.setAmount(inHand.getAmount() - 1);
                }
                event.getBlock().getWorld().playSound(tnt.getLocation(), Sound.FUSE, 1, 1);
            }

        }
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void fuseAndPower(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            if (settings.getPower() >= 0) {
                tnt.setYield((float) settings.getPower());
             }
            if (settings.getFuse() != null) {
                tnt.setFuseTicks(Integer.parseInt(settings.getFuse()) * 20);
            }


        }
      }

    @EventHandler(ignoreCancelled = true)
    public void yield(EntityExplodeEvent event) {
        if(settings.getYield() >= 0 && event.getEntity() instanceof TNTPrimed) {
            event.setYield((float) settings.getYield());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockDamage(EntityExplodeEvent event) {
        if(!settings.isBlockDamage() && event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear();
        }
     }
    }
