package io.sporkpgm.region.exception;

import org.dom4j.Element;

public class InvalidRegionException extends Exception {

	private static final long serialVersionUID = -8988962714866893205L;

	Element element;
	String message;

	public InvalidRegionException(Element element, String message) {
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
