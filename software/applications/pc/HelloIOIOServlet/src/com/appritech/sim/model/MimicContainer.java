package com.appritech.sim.model;

import java.util.Collection;
import java.util.HashMap;

import com.appritech.sim.model.components.Component;
import com.appritech.sim.model.components.Pump;

public class MimicContainer {

	private HashMap<String, Component> components = new HashMap<String, Component>();
	private HashMap<String, Pump> pumps = new HashMap<String, Pump>();					//This could probably be a set, but we might want to find them...
	
	public void addComponent(Component c) {
		if(c instanceof Pump) {
			pumps.put(c.getName(), (Pump)c);
		}
		components.put(c.getName(), c);
	}
	
	public void connectComponents() {
		for(Component c : components.values()) {
			c.connectSelf(components);
		}
	}

	public Collection<Component> getComponents() {
		return components.values();
	}

	public Component getComponent(String name) {
		return components.get(name);
	}
	
	public void solveMimic() {
		Pump p1 = pumps.get("p1");
		Pump p2 = pumps.get("p2");
		
		double p1Down = p1.getPossibleFlowDown(p1, 1.0, p1.getMcrRating());
		double p1Up = p1.getPossibleFlowUp(p1, 1.0, p1.getMcrRating());
		
		double p2Down = p2.getPossibleFlowDown(p2, 1.0, p2.getMcrRating());
		double p2Up = p2.getPossibleFlowUp(p2, 1.0, p2.getMcrRating());
		
		System.out.println("p1 down = " + p1Down);
		System.out.println("p1 up = " + p1Up);
		System.out.println("p2 down = " + p2Down);
		System.out.println("p2 up = " + p2Up);
	
		System.out.println("Holy moly, we made it through");
	}
	
}
