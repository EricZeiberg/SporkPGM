package io.sporkpgm.module;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleAbout {

	ModuleInfo info;
	List<Class<? extends Module>> requires = new ArrayList<>();

	public ModuleAbout(Class<? extends Module> module) {
		Preconditions.checkArgument(module.isAnnotationPresent(ModuleInfo.class), "Module must have a ModuleInfo annotation");
		this.info = module.getAnnotation(ModuleInfo.class);
		this.requires.addAll(Arrays.asList(info.requires()));
	}

	public ModuleInfo getInfo() {
		return info;
	}

	public String getName() {
		return info.name();
	}

	public String getDescription() {
		return info.description();
	}

	public boolean isListener() {
		return info.listener();
	}

	public List<Class<? extends Module>> getRequires() {
		return requires;
	}

}
