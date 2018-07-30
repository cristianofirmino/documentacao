package com.windchillWS.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.windchillWS.enums.AttributesFixed;
import com.windchillWS.enums.AttributesMultivalued;
import com.windchillWS.enums.AttributesOthers;

@Component
public class AtributtesAndValuesUtil {

	public static Map<String, String> getAttributesAndValuesFixed(JSONObject doc) {

		Map<String, String> attributesAndValuesFixed = new HashMap<String, String>();
		Iterator<String> attributes = doc.keys();

		while (attributes.hasNext()) {

			String attribute = (String) attributes.next();

			if (AttributesFixed.attributes().contains(attribute)) {
				attributesAndValuesFixed.put(attribute, doc.getString(attribute));
			}

		}

		return attributesAndValuesFixed;
	}

	public static Map<String, String> getAttributesAndValuesOthers(JSONObject doc) {

		Map<String, String> attributesAndValuesOthers = new HashMap<>();
		Iterator<String> attributes = doc.keys();
		StringBuilder attributesNameB = new StringBuilder();
		StringBuilder paramsNameB = new StringBuilder();
		

		while (attributes.hasNext()) {

			String attribute = (String) attributes.next();

			if (!AttributesFixed.attributes().contains(attribute) && !AttributesMultivalued.attributes().contains(attribute)) {
				attributesAndValuesOthers.put(attribute, doc.getString(attribute));

				for (int i = 0; i < AttributesOthers.values().length; i++) {

					if (attribute.equals(AttributesOthers.values()[i].toString())) {
						attributesNameB.append(AttributesOthers.values()[i].getName() + ",");
						paramsNameB.append(AttributesOthers.values()[i].toString() + ",");
					}
				}
			}
		}

		String attributesName = attributesNameB.substring(0, attributesNameB.length() - 1);
		String paramsName = paramsNameB.substring(0, paramsNameB.length() - 1);
		attributesAndValuesOthers.put("attributesNameOthers", attributesName);
		attributesAndValuesOthers.put("paramsNameOthers", paramsName);

		return attributesAndValuesOthers;
	}
	
	public static Map<String, String> getAttributesAndValuesMultivalued(JSONObject doc) {

		Map<String, String> attributesAndValuesMultivalued = new HashMap<>();
		Iterator<String> attributes = doc.keys();
		StringBuilder attributesNameB = new StringBuilder();
		StringBuilder paramsNameB = new StringBuilder();
		

		while (attributes.hasNext()) {

			String attribute = (String) attributes.next();

			if (!AttributesFixed.attributes().contains(attribute) && !AttributesOthers.attributes().contains(attribute)) {
				attributesAndValuesMultivalued.put(attribute, doc.getString(attribute));

				for (int i = 0; i < AttributesMultivalued.values().length; i++) {

					if (attribute.equals(AttributesMultivalued.values()[i].toString())) {
						attributesNameB.append(AttributesMultivalued.values()[i].getName() + ",");
						paramsNameB.append(AttributesMultivalued.values()[i].toString() + ",");
					}
				}
			}
		}

		String attributesName = attributesNameB.substring(0, attributesNameB.length() - 1);
		String paramsName = paramsNameB.substring(0, paramsNameB.length() - 1);
		attributesAndValuesMultivalued.put("attributesNameMultivalued", attributesName);
		attributesAndValuesMultivalued.put("paramsNameMultivalued", paramsName);

		return attributesAndValuesMultivalued;
	}

	public static Map<String, String> getAttributesAndValues(JSONObject doc) {

		Map<String, String> mapAttributesAndMethods = new HashMap<>();
		Iterator<String> attributes = doc.keys();

		while (attributes.hasNext()) {
			String attribute = (String) attributes.next();
			mapAttributesAndMethods.put(attribute, doc.getString(attribute));
		}

		return mapAttributesAndMethods;
	}

	@Deprecated
	public static List<String> getAtributos(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			getAtributos(fields, type.getSuperclass());
		}

		List<String> atributos = new ArrayList<>();

		for (Field field : fields) {
			atributos.add(field.getName());
		}

		Collections.sort(atributos);

		return atributos;
	}

	@Deprecated
	public static List<String> getMethods(List<Method> methods, Class<?> type) {
		methods.addAll(Arrays.asList(type.getDeclaredMethods()));

		if (type.getSuperclass() != null) {
			getMethods(methods, type.getSuperclass());
		}

		List<String> metodos = new ArrayList<>();

		for (Method metodo : methods) {

			if (metodo.getName().contains("get") && !metodo.getName().contains("getClass")) {

				metodos.add(metodo.getName());
			}

		}

		Collections.sort(metodos);

		return metodos;
	}

}
