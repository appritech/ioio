package com.appritech.sim.model.components.helper;

import java.util.HashMap;
import java.util.Map;

import com.appritech.sim.model.components.Pump;

public class AngerStory {

	Map<Pump, Map<Double, Double>> map = new HashMap<Pump, Map<Double, Double>>();
	
	public Map<Pump, Map<Double, Double>> getMap() {
		return map;
	}
	
	public void addEntry(Pump pump, Double oldVolume, Double ratio) {
		if (map.containsKey(pump) == false) {
			map.put(pump, new HashMap<Double, Double>());
		}
		
		map.get(pump).put(oldVolume, ratio);
	}
	
	public Double getRatio(Pump originPump, double volume) {
		if (map.get(originPump) != null) {
			return map.get(originPump).get(volume);
		}
		return null;
	}
	
	
}
