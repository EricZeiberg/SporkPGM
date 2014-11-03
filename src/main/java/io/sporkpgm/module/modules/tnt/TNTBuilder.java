package io.sporkpgm.module.modules.tnt;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.region.exception.InvalidRegionException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
@BuilderInfo(documentable = true)
public class TNTBuilder extends Builder{

    public TNTBuilder(Document document) {
        super(document);
    }

    public TNTBuilder(SporkMap map) {
        super(map);
    }

    @Override
    public List<Module> build() throws ModuleLoadException, InvalidRegionException {
        List<Module> modules = new ArrayList<>();
        boolean instantIgnite = false;
        boolean blockDamage = true;
        double yield = 1.0;
        double power = 20.0;
        String fuse = null;
        if (getRoot().element("tnt") != null){
            //Spork.get().getLogger().info("Detected TNTModule");
            Element element = getRoot().element("tnt");
            if (element.element("instantignite") != null){
                instantIgnite = parseBoolean(element.element("instantignite").getText());
                //Spork.get().getLogger().info("II");
            }
            if (element.element("blockdamage") != null){
                blockDamage = parseBoolean(element.element("blockdamage").getText());
                //Spork.get().getLogger().info("BD");
            }
            if (element.element("yield") != null && blockDamage){
                yield = Double.parseDouble(element.element("yield").getText());
                //Spork.get().getLogger().info("Yield " + yield);
            }
            if (element.element("power") != null){
                power = Double.parseDouble(element.element("power").getText());
                //Spork.get().getLogger().info("Power");
            }
            if (element.element("fuse") != null){
                fuse = element.element("fuse").getText().replace("s", "");
                //Spork.get().getLogger().info("Fuse");
            }
            TNTSettings settings = new TNTSettings(instantIgnite, blockDamage, yield, power, fuse);
            TNTModule module = new TNTModule(map, settings);
            modules.add(module);
        }
        return modules;
    }


    private boolean parseBoolean(String text){
        if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("on")){
            return true;
        }
        else if (text.equalsIgnoreCase("false") || text.equalsIgnoreCase("no") || text.equalsIgnoreCase("off")){
            return false;
        }
        else {
            return false;
        }
    }
}
