package io.sporkpgm.module.modules.maxheight;

import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.util.Log;
import org.bukkit.event.EventHandler;
import static org.bukkit.ChatColor.*;

@ModuleInfo(name = "MaxHeightModule", description = "Prevents players from building above a defined height", multiple = false)
public class MaxHeightModule extends Module {

	int height;

	public MaxHeightModule(int height) {
		this.height = height;
	}

	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		if(!event.hasPlayer()) {
			return;
		}

		// Log.info("Checking Y > Max Y (" + event.getLocation().getBlockY() + " > " + height + ") = " + (event.getLocation().getBlockY() > height));
		if(event.getLocation().getBlockY() > height) {
			event.setCancelled(true);
			event.getPlayer().getPlayer().sendMessage(YELLOW + " [!]" + GRAY + " You may not edit above the height limit (" + RED + height + " Blocks" + GRAY + ")");
		}
	}

	public Class<? extends Builder> builder() {
		return MaxHeightBuilder.class;
	}

}
