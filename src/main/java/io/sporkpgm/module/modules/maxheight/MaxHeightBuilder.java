package io.sporkpgm.module.modules.maxheight;

import com.google.common.collect.Lists;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;

@BuilderInfo(documentable = true)
public class MaxHeightBuilder extends Builder {

	public MaxHeightBuilder(Document document) {
		super(document);
	}

	public List<Module> build() throws ModuleLoadException {
		Element max = getRoot().element("maxbuildheight");

		if(max != null) {
			int height;

			try {
				height = StringUtil.convertStringToInteger(max.getText());
			} catch(NumberFormatException e) {
				throw new ModuleLoadException(max, "The height supplied was not valid");
			}

			List<Module> modules = Lists.newArrayList();
			modules.add(new MaxHeightModule(height));
			return modules;
		}

		return Lists.newArrayList();
	}

}
