/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PropertiesUtil {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map convertMapToSerializableMap(Map map) {
		Map result = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
		for(Iterator i = map.keySet().iterator(); i.hasNext(); ) {
			Object key = i.next();
			Object value = map.get(key);
			if (SerializationUtil.isSerializable(value))
				result.put(key, value);
			else
				result.put(key, String.valueOf(value));
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertDictionaryToMap(Dictionary dict) {
		Map result = new HashMap();
		for (Enumeration e = dict.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Object value = dict.get(key);
			if (SerializationUtil.isSerializable(value))
				result.put(key, value);
			else
				result.put(key, String.valueOf(value));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> convertHeadersToMap(Dictionary<String,String> headers) {
		return (Map<String, String>) convertDictionaryToMap(headers);
	}

}
