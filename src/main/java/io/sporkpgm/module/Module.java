package io.sporkpgm.module;

import io.sporkpgm.module.builder.Builder;
import org.bukkit.event.Listener;

public abstract class Module implements Listener {

	public ModuleAbout getInfo() {
		return new ModuleAbout(this.getClass());
	}

	public abstract Class<? extends Builder> builder();

	public static ModuleAbout getInfo(Class<? extends Module> clazz) {
		return new ModuleAbout(clazz);
	}

}
