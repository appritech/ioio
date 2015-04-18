package com.appritech.sim.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appritech.sim.model.components.Combiner;
import com.appritech.sim.model.components.Component;
import com.appritech.sim.model.components.Pump;
import com.appritech.sim.model.components.helper.AngerStory;

public class MimicContainer {

	private HashMap<String, Component> components = new HashMap<String, Component>();
	private HashMap<String, Pump> pumps = new HashMap<String, Pump>();					//This could probably be a set, but we might want to find them...
	private Map<Component, AngerStory> overrideMap = new HashMap<Component, AngerStory>();
	
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
	
	private void resolveAnger(Component c, List<Pump> pumpList, List<Double> minimumFlow) {
		
		if(c instanceof Combiner) {
			resolveCombinerAnger((Combiner)c, pumpList, minimumFlow);
			return;
		}
		
		HashMap<Pump, Double> log = c.getComplaintLog();
		double sum = 0;
		for (Double d : log.values()) {
			sum += d;
		}
//		double sum = log.values().stream().reduce(0.0, Double::sum);
		
		if (sum > c.getMaxVolume()) {
			double ratio = c.getMaxVolume() / sum;
			//We need to create a map of pumps.
			
			for (int i = 0; i < pumpList.size(); i++) {
				Pump p = pumpList.get(i);
				double minFlow = minimumFlow.get(i);
				
				if (!overrideMap.containsKey(c)) {
					overrideMap.put(c, new AngerStory());
				}
				overrideMap.get(c).addEntry(p, p.getMcrRating() * minFlow, ratio);				
			}
		}
		c.setNumTimesAngerResolved(c.getNumTimesAngerResolved() + 1);
	}
	
	private void resolveCombinerAnger(Combiner c, List<Pump> pumpList, List<Double> minimumFlow) {
//		double sum = c.getInputFlowVolumeSum();
//		if(sum > c.getTrueFlowVolume()) {
//			double ratio = c.getTrueFlowVolume() / sum;
//			if(overrideMap.containsKey(c))
//				ratio *= overrideMap.get(c);
//			overrideMap.put(c, ratio);
//		}
//		c.setNumTimesAngerResolved(c.getNumTimesAngerResolved() + 1);
		
		HashMap<Pump, Double> log = c.getComplaintLog();
		double sum = c.getInputFlowVolumeSum();
//		double sum = log.values().stream().reduce(0.0, Double::sum);
		
		if (sum > c.getTrueFlowVolume()) {
			double ratio = c.getTrueFlowVolume() / sum;
			//We need to create a map of pumps.
			
			for (int i = 0; i < pumpList.size(); i++) {
				Pump p = pumpList.get(i);
				double minFlow = minimumFlow.get(i);
				
				if (!overrideMap.containsKey(c)) {
					overrideMap.put(c, new AngerStory());
				}
				overrideMap.get(c).addEntry(p, p.getMcrRating() * minFlow, ratio);				
			}
		}
		c.setNumTimesAngerResolved(c.getNumTimesAngerResolved() + 1);
	}

	//Override map : Map<Component, 
	public Map<Component, AngerStory> getOverrideMap() {
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
			double ratio = flows.get(i);
			double flowRate = pumps.get(i).getMcrRating() * ratio;
			resultList[i] = pumps.get(i).getPossibleFlowDown(pumps.get(i), 1.0, flowRate, this, true, null) * ratio;				
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
				else if(c.getNumTimesAngerResolved() == nextAngryComponent.getNumTimesAngerResolved()) {
					if(nextAngryComponent instanceof Combiner) {
						//Lets do combiners closer to the end...
						nextAngryComponent = c;
					}
				}
			}
		}
		return nextAngryComponent;
	}
	
	private double[] computeUp(List<Pump> pumps, List<Double> flows) {
		double[] resultList = new double[pumps.size()];
		
		for (int i = 0; i < pumps.size(); i++) {
			double ratio = flows.get(i);
			double flowRate = pumps.get(i).getMcrRating() * ratio;
			resultList[i] = pumps.get(i).getPossibleFlowUp(pumps.get(i), 1.0, flowRate, this, true, null) * ratio;				
		}
		
		return resultList;
	}
	
	private void resetOverrideMapAndOtherStuff() {
		overrideMap = new HashMap<Component, AngerStory>();
		for(Component c : components.values()) {
			c.setNumTimesAngerResolved(0);
		}
	}
	
	public void solveMimic() {

		resetOverrideMapAndOtherStuff();				//Reset anything since last iteration
		
		boolean isAngry = true;			//Assume the worst of the world (i.e. make sure we get into the while loop at least once)
		List<Double> minimumFlows = new ArrayList<Double>(pumps.values().size());
		for (int i = 0; i < pumps.values().size(); i++) {
			minimumFlows.add(1.0);
		}
		
		List<Pump> pumpList = new ArrayList<Pump>(pumps.values());
		while(isAngry) {
			double[] downResult = computeDown(pumpList, minimumFlows);
			double[] upResult = computeUp(pumpList, minimumFlows);
			
			Component grumpyHead = getNextAngryComponent();
			if (grumpyHead != null) {
				isAngry = true;
				resolveAnger(grumpyHead, pumpList, minimumFlows);
				prepForTryAgain();
//				resetOverrideMapAndOtherStuff();				//Reset anything since last iteration
			}
			else {
				isAngry = false;
			}
			
			
			for (int i = 0; i < downResult.length; i++) {
				minimumFlows.set(i, Math.min(downResult[i], upResult[i]));
			}
		}
		
		System.out.println("Round one: " + this);
		
//		resetOverrideMapAndOtherStuff();				//Reset anything since last iteration
		prepForTryAgain();
		isAngry = true;
		//while(isAngry) {
			double[] downResult = computeDown(new ArrayList<Pump>(pumps.values()), minimumFlows);
			double[] upResult = computeUp(new ArrayList<Pump>(pumps.values()), minimumFlows);
			
//			Component grumpyHead = getNextAngryComponent();
//			if (grumpyHead != null) {
//				isAngry = true;
//				resolveAnger(grumpyHead, pumpList, minimumFlows);
//				prepForTryAgain();
//			}
//			else {
//				isAngry = false;
//			}
//			
//			
//			for (int i = 0; i < downResult.length; i++) {
//				minimumFlows.set(i, Math.min(downResult[i], upResult[i]));
//			}
		//}
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