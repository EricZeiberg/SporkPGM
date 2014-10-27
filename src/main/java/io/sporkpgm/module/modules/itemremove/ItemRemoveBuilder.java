package io.sporkpgm.module.modules.itemremove;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Material;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(documentable = false)
public class ItemRemoveBuilder extends Builder {

	public ItemRemoveBuilder(SporkMap map) {
		super(map);
	}

	public List<Module> build() throws ModuleLoadException {
		List<Module> modules = new ArrayList<>();
		if(getRoot().element("itemremove") != null) {
			Element element = getRoot().element("itemremove");
			List<Material> materials = new ArrayList<>();

			for(Element item : XMLUtil.getElements(element, "item")) {
				Material material = StringUtil.convertStringToMaterial(item.getText());
				if(material == null) {
					throw new ModuleLoadException("Unsupported Material: " + item.getText());
				}

				materials.add(material);
			}

			ItemRemoveModule module = new ItemRemoveModule(map, materials);
			modules.add(module);
		}
		return modules;
	}

}
