package io.sporkpgm.module.modules.damage;


import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.event.entity.EntityDamageEvent;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = true)
public class DisableDamageBuilder extends Builder {

	public DisableDamageBuilder(Document document) {
		super(document);
	}

	public DisableDamageBuilder(SporkMap map) {
		super(map);
	}

	@Override
	public List<Module> build() throws ModuleLoadException, InvalidRegionException {
		List<Module> modules = new ArrayList<>();
		List<EntityDamageEvent.DamageCause> causes = new ArrayList<>();
		Element root = getRoot();
		for(Element e : XMLUtil.getElements(root, "disabledamage")) {
			for(Element damage : XMLUtil.getElements(e, "damage")) {
				String damageS = damage.getText().toUpperCase();
				causes.add(EntityDamageEvent.DamageCause.valueOf(damageS));
			}
		}
		modules.add(new DisableDamageModule(causes));

		return modules;
	}
}
