package com.appritech.sim.model.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.appritech.sim.model.MimicContainer;
import com.appritech.sim.model.components.helper.Complaint;
import com.appritech.sim.model.DrawingLine;

public abstract class Component {
	
	
	private HashMap<Pump, Double> complaintLog = new HashMap<Pump, Double>();
	private double currentAnger = 0;
	private boolean isAngry = false;
	private int numTimesAngerResolved = 0;
	private String name;
	private HashMap<Pump, Double> trueFlowPercent = new HashMap<Pump, Double>();
	private HashMap<Pump, Double> trueFlowVolume = new HashMap<Pump, Double>();
	protected double maxVolume = Double.MAX_VALUE;
	
	protected float x;
	protected float y;
	
	protected boolean hasMaxVolume = false;
	public void setMaxVolume(double d) {
		this.maxVolume = d;
		hasMaxVolume = true;
	}
	
	public double getMaxVolume() {
		return maxVolume;
	}
	
	public Component(String name) {
		this.name = name;
	}
	
	public abstract double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component input);
	public abstract double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component output);
	public abstract void setSource(Component source);
	public abstract List<DrawingLine> getConnectionLines();
	
	public void addToComplaintLog(Pump originPump, double volume, MimicContainer mc) {
		if (hasMaxVolume) {
			complaintLog.put(originPump, volume);
			currentAnger += volume;
			
			if (currentAnger > maxVolume) {
				isAngry = true;
			}
			else {
				isAngry = false;
			}
		}
	}
	
	public void reset() {
		complaintLog.clear();
		currentAnger = 0;
		trueFlowPercent.clear();
		trueFlowVolume.clear();
		isAngry = false;
	}
	
	//WARNING - This class overridden in Valve.java to include percent open
	public boolean isAngry() {
		//Calculating anger as we go is a pain, and I can't get it to work... so let's just calculate it after trueFlowVolume is set.
		if(maxVolume <= 0)
			return false;			//If we don't have max set, then we can't be angry...
		return getTrueFlowVolume() > getMaxVolume();
//		return isAngry;
	}
	
	public HashMap<Pump, Double> getComplaintLog() {
		return complaintLog;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTrueFlowPercent() {
		return trueFlowPercent.values().stream().reduce(0.0, Double::sum);
	}

	public void setTrueFlowPercent(Pump p, double trueFlow) {
		trueFlowPercent.put(p, trueFlow);
	}

	public double getTrueFlowVolume() {
		return trueFlowVolume.values().stream().reduce(0.0, Double::sum);
	}

	public void setTrueFlowVolume(Pump p, double volume) {
		trueFlowVolume.put(p, volume);
	}

	public void setAngry(boolean isAngry) {
		this.isAngry = isAngry;
	}

	public int getNumTimesAngerResolved() {
		return numTimesAngerResolved;
	}

	public void setNumTimesAngerResolved(int numTimesAngerResolved) {
		this.numTimesAngerResolved = numTimesAngerResolved;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void connectSelf(HashMap<String, Component> components) {
		//This should probably be abstract... but default can do nothing...
	}
	
	public void setValueFromUser(float value) {
		//This can be overridden. It is a cheater method.
	}
	
	
	@Override
	public String toString() {
		return name + ": " + getTrueFlowPercent() + ", " + trueFlowPercent.values() + ", " + getTrueFlowVolume() + ", " + trueFlowVolume.values();
	}

}
