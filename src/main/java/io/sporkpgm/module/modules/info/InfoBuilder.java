package io.sporkpgm.module.modules.info;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class InfoBuilder extends Builder {

	public InfoBuilder(Document document) {
		super(document);
	}

	public InfoBuilder(SporkMap map) {
		super(map);
	}

	public List<Module> build() throws ModuleLoadException {
		List<Module> modules = new ArrayList<>();

		Element root = document.getRootElement();
		String name;
		try {
			name = root.element("name").getText();
		} catch(NullPointerException ex) {
			throw new ModuleLoadException("Map names can't be null");
		}

		String version;
		try {
			version = root.element("version").getText();
		} catch(NullPointerException ex) {
			throw new ModuleLoadException("Map versions can't be null");
		}

		String objective;
		try {
			objective = root.element("objective").getText();
		} catch(NullPointerException ex) {
			throw new ModuleLoadException("Map objectives can't be null");
		}

		List<String> rules = new ArrayList<>();
		Element rulesElement = root.element("rules");
		for(Element rule : XMLUtil.getElements(rulesElement, "rule")) {
			rules.add(rule.getText());
		}

		List<Contributor> authors = new ArrayList<>();
		Element authorsElement = root.element("authors");
		for(Element author : XMLUtil.getElements(authorsElement, "author")) {
			if(author.getText() == null)
				continue;
			authors.add(new Contributor(author.getText(), author.attributeValue("contribution")));
		}

		if(authors.size() == 0) {
			throw new ModuleLoadException("Maps must have at least 1 author");
		}

		List<Contributor> contributors = new ArrayList<>();
		Element contributorsElement = root.element("contributors");
		if(contributorsElement != null) {
			for(Element contributor : XMLUtil.getElements(contributorsElement, "contributor")) {
				if(contributor.getText() == null)
					continue;
				contributors.add(new Contributor(contributor.getText(), contributor.attributeValue("contribution")));
			}
		}

		int maxPlayers = 0;
		Element teamsElement = document.getRootElement().element("teams");
		for(Element team : XMLUtil.getElements(teamsElement, "team")) {
			try {
				maxPlayers += StringUtil.convertStringToInteger(team.attributeValue("max"));
			} catch(Exception e) { /* nothing */ }
		}

		modules.add(new InfoModule(name, version, objective, rules, maxPlayers, authors, contributors));
		return modules;
	}

}
