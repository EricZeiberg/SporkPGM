package io.sporkpgm.module.builder;

import com.google.common.base.Preconditions;
import io.sporkpgm.module.ModuleStage;

public class BuilderAbout {

	private BuilderInfo info;

	public BuilderAbout(Class<? extends Builder> module) {
		Preconditions.checkArgument(module.isAnnotationPresent(BuilderInfo.class), "Module must have a BuilderInfo annotation");
		this.info = module.getAnnotation(BuilderInfo.class);
	}

	public BuilderInfo getInfo() {
		return info;
	}

	public boolean isDocumentable() {
		return info.documentable();
	}

	public ModuleStage getStage() {
		return info.stage();
	}

}