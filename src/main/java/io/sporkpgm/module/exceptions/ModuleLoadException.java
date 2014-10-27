package io.sporkpgm.module.exceptions;

import org.dom4j.Element;

@SuppressWarnings("ALL")
public class ModuleLoadException extends Exception {

	private static final long serialVersionUID = 4743495996323223505L;

	Element element;
	String message;

	public ModuleLoadException(String message) {
		this.element = element;
		this.message = message;
	}

	public ModuleLoadException(Element element, String message) {
		this.element = element;
		this.message = message;
	}

	@Override
	public String getMessage() {
		String sep = System.getProperty("line.separator");

		String message = this.message;
		if(element != null) {
			message = message + ": " + sep + element.asXML();
		}

		return message;
	}

}
