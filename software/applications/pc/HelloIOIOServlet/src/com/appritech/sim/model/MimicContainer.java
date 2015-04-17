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
	List<Component> angryComponents = new LinkedList<Component>();
	
	
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
		List<Complaint> log = c.getComplaintLog();
		double sum = 0;
		for (Complaint complaint : log) {
			sum += complaint.getFlow();
		}
		
		if (sum > c.getMaxVolume()) {
			double ratio = c.getMaxVolume() / sum;
			overrideMap.put(c, ratio);
		}
	}
	
	public Map<Component, Double> getOverrideMap() {
		return overrideMap;
	}
	
	private void prepForTryAgain() {
		angryComponents.clear();
		for (Component c : components.values()) {
			c.reset();
		}
	}
	
	private double[] computeDown(List<Pump> pumps, List<Double> flows) {
		double[] resultList = new double[pumps.size()];
		
		boolean isAngry = true;
		while (isAngry) {
			isAngry  = false;
			
			for (int i = 0; i < pumps.size(); i++) {
				resultList[i] = pumps.get(i).getPossibleFlowDown(pumps.get(i), flows.get(i), pumps.get(i).getMcrRating(), this, true);				
			}
			
			if (angryComponents.size() > 0) {
				isAngry = true;
				resolveAnger(angryComponents.get(angryComponents.size() -1));
				prepForTryAgain();
			}
		}
		
		return resultList;
	}
	
	private double[] computeUp(List<Pump> pumps, List<Double> flows) {
		double[] resultList = new double[pumps.size()];
		
		boolean isAngry = true;
		while (isAngry) {
			isAngry  = false;
			
			for (int i = 0; i < pumps.size(); i++) {
				resultList[i] = pumps.get(i).getPossibleFlowUp(pumps.get(i), flows.get(i), pumps.get(i).getMcrRating(), this, true);				
			}
			if (angryComponents.size() > 0) {
				isAngry = true;
				resolveAnger(angryComponents.get(angryComponents.size() -1));
				prepForTryAgain();
			}
		}
		
		return resultList;
	}
	
	private void resetOverrideMap() {
		overrideMap = new HashMap<Component, Double>();
	}
	
	public void solveMimic() {
		Pump p1 = pumps.get("p1");
		Pump p2 = pumps.get("p2");
		
		double[] downResult = computeDown(Arrays.asList(p1, p2), Arrays.asList(1.0, 1.0));
		double[] upResult = computeUp(Arrays.asList(p1, p2), Arrays.asList(1.0, 1.0));
		
		System.out.println("Round one: " + this);
		
		List<Double> minimumFlows = new ArrayList<Double>(downResult.length);
		for (int i = 0; i < downResult.length; i++) {
			minimumFlows.add(Math.min(downResult[i], upResult[i]));
		}

		prepForTryAgain();
		resetOverrideMap();
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
		for (Component c : angryComponents) {
			sb.append(c.getName() + " is angry! (" + c.getTrueFlowPercent() + ")\r\n");
			for (Complaint com : c.getComplaintLog()) {
				sb.append(com + "\r\n");
			}
		}
		return sb.toString();
	}
	
	public void addAngryComponent(Component c) {
		angryComponents.add(c);
	}
	
}