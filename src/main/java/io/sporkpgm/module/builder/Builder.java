package io.sporkpgm.module.builder;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.exception.InvalidRegionException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;

public abstract class Builder {

	protected SporkMap map;
	protected Document document;

	public Builder(Document document) {
		this.document = document;
	}

	public Builder(SporkMap map) {
		this(map.getDocument());
		this.map = map;
	}

	public Element getRoot() {
		return document.getRootElement();
	}

	public abstract List<Module> build() throws ModuleLoadException, InvalidRegionException;

	public BuilderAbout getInfo() {
		return new BuilderAbout(getClass());
	}

	public static boolean isDocumentable(Class<? extends Builder> clazz) {
		return new BuilderAbout(clazz).isDocumentable();
	}

}