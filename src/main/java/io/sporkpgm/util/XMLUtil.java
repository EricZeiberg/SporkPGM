package io.sporkpgm.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class XMLUtil {

	public static List<Object> elements(Element root, String... names) {
		return elements(root, Lists.newArrayList(names));
	}

	public static List<Object> elements(Element root, List<String> names) {
		List<Object> objects = new ArrayList<>();
		for(Element element : getElements(root)) {
			if(names.contains(element.getName())) {
				objects.add(element);
			}
		}
		return objects;
	}

	public static List<Element> getElements(Element root, String... names) {
		List<Element> elements = new ArrayList<>();

		if(root == null) {
			return elements;
		}

		for(Object object : elements(root, names)) {
			if(object instanceof Element) {
				elements.add((Element) object);
			}
		}

		return elements;
	}

	public static List<Element> getElements(Element root) {
		List<Element> elements = new ArrayList<>();
		if(root == null) {
			return elements;
		}

		for(Object object : root.elements()) {
			if(object instanceof Element) {
				elements.add((Element) object);
			}
		}

		return elements;
	}

	public static boolean parseBoolean(String string, boolean def) {
		if(string == null)
			return def;

		if(string.equalsIgnoreCase("on") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("yes"))
			return true;
		else if(string.equalsIgnoreCase("off") || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("no"))
			return false;

		return def;
	}

	public static int parseInteger(String string) throws NumberFormatException {
		return Integer.parseInt(string);
	}

	public static int parseInteger(String string, int fallback) {
		try {
			return parseInteger(string);
		} catch(NumberFormatException e) {
			return fallback;
		}
	}

	public static double parseDouble(String s) throws NumberFormatException {
		if(s == null)
			throw new NumberFormatException("String provided can't be null");
		if(s.equals("@"))
			return (1.0D / 0.0D);
		if(s.equals("-@"))
			return (-1.0D / 0.0D);
		return Double.parseDouble(s);
	}

	public static List<Double> parseDoubleList(String text) {
		String[] pieces = text.split("[^o0-9\\.-]");
		List<Double> numbers = Lists.newLinkedList();
		for(String piece : pieces) {
			try {
				double num = parseDouble(piece);
				numbers.add(num);
			} catch(NumberFormatException ignored) {
			}
		}
		return numbers;
	}

	public static Vector parseVector(String text) {
		List<Double> numbers = parseDoubleList(text);
		if(numbers.size() >= 3) {
			return new Vector(numbers.get(0), numbers.get(1),
					numbers.get(2));
		}
		return null;
	}

	public static ChatColor parseChatColor(String text) {
		return StringUtil.convertStringToChatColor(text);
	}

	public static ChatColor parseChatColor(String text, ChatColor fallback) {
		try {
			return StringUtil.convertStringToChatColor(text);
		} catch(Exception e) {
			return fallback;
		}
	}

	public static String getElementOrParentValue(Element element, String attribute) {
		return (element.attributeValue(attribute) != null ? element.attributeValue(attribute) : element.getParent().attributeValue(attribute));
	}

	public static String getValue(Element element, String name) {
		return getValue(ElementValueType.BOTH, element, name);
	}

	public static String getValue(ElementValueType type, Element element, String name) {
		if(type != ElementValueType.ATTRIBUTE && element.elementText(name) != null) {
			return element.elementText(name);
		} else if(type != ElementValueType.ELEMENT && element.attributeValue(name) != null) {
			return element.attributeValue(name);
		}

		return null;
	}

	public static boolean hasValue(Element element, String name) {
		return getValue(element, name) != null;
	}

	public static boolean hasValue(ElementValueType type, Element element, String name) {
		return getValue(type, element, name) != null;
	}

	public static ElementValueType getType(Element element, String name) {
		if(element.elementText(name) != null && element.attributeValue(name) != null) {
			return ElementValueType.BOTH;
		}

		if(element.elementText(name) != null) {
			return ElementValueType.ELEMENT;
		} else {
			return ElementValueType.ATTRIBUTE;
		}
	}

	public enum ElementValueType {

		ATTRIBUTE,
		ELEMENT,
		BOTH;

		public static ElementValueType getType(String string) {
			for(ElementValueType type : values())
				if(type.name().equalsIgnoreCase(string))
					return type;

			return null;
		}

	}

}
