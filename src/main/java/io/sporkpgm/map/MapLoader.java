package io.sporkpgm.map;

import io.sporkpgm.util.Config.Map;
import io.sporkpgm.util.Log;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

	public static List<MapBuilder> build(File folder) {
		List<MapBuilder> builders = new ArrayList<>();

		File[] maps = Map.getDirectory().listFiles();
		Log.info("Found " + maps.length + " possible entries in the Maps repository");

		for(File map : maps) {
			if(!map.isDirectory()) {
				Log.info(map.getName() + " is not a directory");
				continue;
			}

			File xml = new File(map, "map.xml");
			File region = new File(map, "region");
			File level = new File(map, "level.dat");

			boolean loadable = xml.exists() && (region.exists() && region.isDirectory()) && level.exists();
			if(loadable) {
				Log.info(map.getName() + " is loadable!");
				try {
					SAXReader reader = new SAXReader();
					Document document = reader.read(xml);
					MapBuilder builder = new MapBuilder(document, map);
					builders.add(builder);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return builders;
	}

}
