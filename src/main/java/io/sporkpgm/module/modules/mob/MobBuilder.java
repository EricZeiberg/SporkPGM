package io.sporkpgm.module.modules.mob;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleStage;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = false, stage = ModuleStage.LOAD)
public class MobBuilder extends Builder {

	public MobBuilder(Document document) {
		super(document);
	}

	public MobBuilder(SporkMap map) {
		super(map);
	}

	@Override
	public List<Module> build() throws ModuleLoadException, InvalidRegionException {
		List<Module> modules = new ArrayList<>();
		Element root = getRoot();
		List<CreatureSpawnEvent.SpawnReason> reasons = new ArrayList<>();
		List<EntityType> mobs = new ArrayList<>();
		for(Element e : XMLUtil.getElements(root, "mobs")) {
			for(Element filter : XMLUtil.getElements(e, "filter")) {
				for(Element reason : XMLUtil.getElements(filter, "spawn")) {
					String reasonS = reason.getText().toUpperCase();
					reasons.add(CreatureSpawnEvent.SpawnReason.valueOf(reasonS));
				}
				for(Element mob : XMLUtil.getElements(filter, "mob")) {
					String mobS = mob.getText().toUpperCase();
					mobs.add(EntityType.fromName(mobS));
					Log.info(EntityType.fromName(mobS).name() + " is the mob!");
				}
			}
			modules.add(new MobModule(mobs, reasons));
		}

		if(modules.size() == 0) {
			modules.add(new MobModule(null, null));
		}

		return modules;
	}

}
