package com.appritech.sim.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.appritech.sim.model.components.Component;
import com.appritech.sim.model.components.Pump;
import com.appritech.sim.model.components.helper.Complaint;

public class MimicContainer {

	private HashMap<String, Component> components = new HashMap<String, Component>();
	private HashMap<String, Pump> pumps = new HashMap<String, Pump>();					//This could probably be a set, but we might want to find them...
	private Map<Component, Double> overrideMap = new HashMap<Component, Double>();
	
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
	
	private void resolveAnger(Component c) {
		HashMap<Pump, Double> log = c.getComplaintLog();
		double sum = 0;
		for (Double d : log.values()) {
			sum += d;
		}
//		double sum = log.values().stream().reduce(0.0, Double::sum);
		
		if (sum > c.getMaxVolume()) {
			double ratio = c.getMaxVolume() / sum;
			if(overrideMap.containsKey(c))
				ratio *= overrideMap.get(c);
			overrideMap.put(c, ratio);
		}
		c.setNumTimesAngerResolved(c.getNumTimesAngerResolved() + 1);
	}
	
	public Map<Component, Double> getOverrideMap() {
		return overrideMap;
	}
	
	private void prepForTryAgain() {
		for (Component c : components.values()) {
			c.reset();
		}
	}
	
	private double[] computeDown(List<Pump> pumps, List<Double> flows) {
		double[] resultList = new double[pumps.size()];
		
		for (int i = 0; i < pumps.size(); i++) {
			resultList[i] = pumps.get(i).getPossibleFlowDown(pumps.get(i), flows.get(i), pumps.get(i).getMcrRating(), this, true, null);				
		}
		
		return resultList;
	}

	/** Find the component with the lowest number of times of being resolved */
	private Component getNextAngryComponent() {
		Component nextAngryComponent = null;
		for(Component c : components.values()) {
			if(c.isAngry()) {
				if(nextAngryComponent == null) {
					//This is the first one, so it is the lowest number
					nextAngryComponent = c;
				}
				else if(c.getNumTimesAngerResolved() < nextAngryComponent.getNumTimesAngerResolved()) {
					//If this has a lower number, then use it instead.
					nextAngryComponent = c;
				}
			}
		}
		return nextAngryComponent;
	}
	
	private double[] computeUp(List<Pump> pumps, List<Double> flows) {
		double[] resultList = new double[pumps.size()];
		
		for (int i = 0; i < pumps.size(); i++) {
			resultList[i] = pumps.get(i).getPossibleFlowUp(pumps.get(i), flows.get(i), pumps.get(i).getMcrRating(), this, true, null);				
		}
		
		return resultList;
	}
	
	private void resetOverrideMapAndOtherStuff() {
		overrideMap = new HashMap<Component, Double>();
		for(Component c : components.values()) {
			c.setNumTimesAngerResolved(0);
		}
	}
	
	public void solveMimic() {
		Pump p1 = pumps.get("p1");
		Pump p2 = pumps.get("p2");
		
		resetOverrideMapAndOtherStuff();				//Reset anything since last iteration
		
		boolean isAngry = true;			//Assume the worst of the world (i.e. make sure we get into the while loop at least once)
		List<Double> minimumFlows = Arrays.asList(1.0, 1.0);
		while(isAngry) {
			double[] downResult = computeDown(Arrays.asList(p1, p2), minimumFlows);
			double[] upResult = computeUp(Arrays.asList(p1, p2), minimumFlows);
			
			Component grumpyHead = getNextAngryComponent();
			if (grumpyHead != null) {
				isAngry = true;
				resolveAnger(grumpyHead);
				prepForTryAgain();
			}
			else {
				isAngry = false;
			}
			
			for (int i = 0; i < downResult.length; i++) {
				minimumFlows.set(i, Math.min(downResult[i], upResult[i]));
			}
		}
		
		System.out.println("Round one: " + this);
		
		
		prepForTryAgain();
		computeDown(Arrays.asList(p1, p2), minimumFlows);
		computeUp(Arrays.asList(p1, p2), minimumFlows);
		
		System.out.println("Round two: " + this);
		
//		double p1Down = computeDown(p1, 1.0);
//		double p1Up = computeUp(p1, 1.0);
//		double minP1 = Math.min(p1Down, p1Up);
//		
//		computeDown(p1, minP1);
//		computeUp(p1, minP1);
//		
//		p1.getPossibleFlowDown(p1, minP1, p1.getMcrRating(), this, true);
//		p1.getPossibleFlowUp(p1, minP1, p1.getMcrRating(), this, true);
//		
//		double p2Down = p2.getPossibleFlowDown(p2, 1.0, p2.getMcrRating(), this, false);
//		double p2Up = p2.getPossibleFlowUp(p2, 1.0, p2.getMcrRating(), this, false);
//		double minP2 = Math.min(p2Down, p2Up);
//		
//		p2.getPossibleFlowDown(p2, minP2, p2.getMcrRating(), this, true);
//		p2.getPossibleFlowUp(p2, minP2, p2.getMcrRating(), this, true);
//		
//		System.out.println("p1 down = " + p1Down);
//		System.out.println("p1 up = " + p1Up);
//		System.out.println("p2 down = " + p2Down);
//		System.out.println("p2 up = " + p2Up);
//	
//		System.out.println("Holy moly, we made it through");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Component c : components.values()) {
			sb.append(c.toString() + "\r\n");
		}
		
		sb.append("Angryzorz!\r\n");
		for (Component c : components.values()) {
			if(c.isAngry()) {
				sb.append(c.getName() + " is angry! (" + c.getTrueFlowPercent() + ")\r\n");
//				for (Complaint com : c.getComplaintLog()) {
//					sb.append(com + "\r\n");
//				}
			}
		}
		return sb.toString();
	}
	
}