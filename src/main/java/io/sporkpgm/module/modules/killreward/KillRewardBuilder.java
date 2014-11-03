package io.sporkpgm.module.modules.killreward;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.module.modules.itemremove.ItemRemoveModule;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Material;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
@BuilderInfo(documentable = true)

public class KillRewardBuilder extends Builder{

    public KillRewardBuilder(Document document) {
        super(document);
    }

    public KillRewardBuilder(SporkMap map) {
        super(map);
    }

    @Override
    public List<Module> build() throws ModuleLoadException, InvalidRegionException {
        List<Module> modules = new ArrayList<>();
        if (getRoot().element("killreward") != null) {
            Element element = getRoot().element("killreward");
            List<KillRewardItem> materials = new ArrayList<>();

            for (Element item : XMLUtil.getElements(element, "item")) {
                Material material = StringUtil.convertStringToMaterial(item.getText());
                int amount = 1;
                if (item.attribute("amount") != null){
                   amount = Integer.parseInt(item.attribute("amount").getText());
                }

                if (material == null) {
                    throw new ModuleLoadException("Unsupported Material: " + item.getText());
                }

                materials.add(new KillRewardItem(material, amount));
                Spork.get().getLogger().info("Found Kill Reward - " + material.name() + " " + "in amount: " + amount);
            }

            KillRewardModule module = new KillRewardModule(map, materials);
            modules.add(module);
        }
        return modules;
    }
}
