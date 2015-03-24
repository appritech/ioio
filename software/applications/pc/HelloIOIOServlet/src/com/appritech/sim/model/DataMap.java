package com.appritech.sim.model;

import java.util.HashMap;

public class DataMap {

	private static HashMap<String, Float> floatMap = new HashMap<String, Float>();
	
	public static void setFloatVal(String name, float val) {
		floatMap.put(name, val);
	}
	
	public static float getFloatVal(String name) {
		if(floatMap.containsKey(name))
			return floatMap.get(name);
		return 0.0f;
	}
	
}
