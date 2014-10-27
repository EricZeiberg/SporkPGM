package io.sporkpgm.module.modules.itemremove;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.extras.TaskedModule;
import io.sporkpgm.util.SchedulerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.List;

@ModuleInfo(name = "ItemRemoveModule", description = "Removes items that shouldn't be picked up")
public class ItemRemoveModule extends Module implements TaskedModule {

	SporkMap map;
	List<Material> materials;
	SchedulerUtil schedule;

	public ItemRemoveModule(SporkMap map, List<Material> materials) {
		this.map = map;
		this.materials = materials;
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		if(materials.contains(event.getItem().getItemStack().getType())) {
			event.setCancelled(true);
			event.getItem().remove();
		}
	}

	public void setTasks(boolean tasks) {
		if(schedule == null) {
			schedule = schedule();
		}

		if(tasks) {
			schedule.startRepeat(0, 1);
		} else {
			schedule.stopRepeat();
		}
	}

	public SchedulerUtil schedule() {
		return new SchedulerUtil(new Runnable() {
			@Override
			public void run() {
				for(Entity entity : map.getWorld().getEntities()) {
					if(entity instanceof Item) {
						Item item = (Item) entity;
						if(materials.contains(item.getItemStack().getType())) {
							item.remove();
						}
					}
				}
			}
		}, false);
	}

	public Class<? extends Builder> builder() {
		return ItemRemoveBuilder.class;
	}

}
