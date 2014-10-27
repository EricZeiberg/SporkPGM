package io.sporkpgm.module.modules.tdm;

import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.objective.scored.ScoredObjective;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.team.SporkTeam;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = true)
public class TeamDeathmatchBuilder extends Builder {

	public TeamDeathmatchBuilder(Document document) {
		super(document);
	}

	@Override
	public List<Module> build() throws ModuleLoadException, InvalidRegionException {
		List<Module> modules = new ArrayList<>();
		Element root = getRoot();
		Element scoreElement = root.element("score");
		if(scoreElement != null) {
			for(SporkTeam team : map.getTeams()) {
				ScoredObjective scored = team.getScored();
				TeamDeathmatchModule tdm = new TeamDeathmatchModule(scored, 1, 1);
				modules.add(tdm);
			}
		}
		return modules;
	}

}
