package com.appritech.sim.model.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.appritech.sim.model.MimicContainer;
import com.appritech.sim.model.components.helper.Complaint;
import com.appritech.sim.model.DrawingLine;

public abstract class Component {
	
	
	private List<Complaint> complaintLog = new LinkedList<Complaint>();
	private double currentAnger = 0;
	private boolean isAngry = false;
	private String name;
	private List<Double> trueFlowPercent = new LinkedList<Double>();
	private List<Double> trueFlowVolume = new LinkedList<Double>();
	private double maxVolume = Double.MAX_VALUE;
	
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
	
	public abstract double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal);
	public abstract double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal);
	public abstract void setSource(Component source);
	public abstract List<DrawingLine> getConnectionLines();
	
	public void addToComplaintLog(Pump originPump, double volume, MimicContainer mc) {
		if (hasMaxVolume) {
			complaintLog.add(new Complaint(originPump, volume));
			double oldAnger = currentAnger;
			currentAnger += volume;
			
			if (oldAnger <= maxVolume && currentAnger > maxVolume) {
				isAngry = true;
				mc.addAngryComponent(this);
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
	
	public boolean isAngry() {
		return isAngry;
	}
	
	public List<Complaint> getComplaintLog() {
		return complaintLog;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTrueFlowPercent() {
		double flowSum = 0;
		for (Double d : trueFlowPercent) {
			flowSum += d;
		}
		return flowSum;
	}

	public void setTrueFlowPercent(double trueFlow) {
		trueFlowPercent.add(trueFlow);
	}

	public double getTrueFlowVolume() {
		double volumeSum = 0;
		for (Double d : trueFlowVolume) {
			volumeSum += d;
		}
		return volumeSum;
	}

	public void setTrueFlowVolume(double volume) {
		trueFlowVolume.add(volume);
	}

	public void setAngry(boolean isAngry) {
		this.isAngry = isAngry;
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
		return name + ": " + getTrueFlowPercent() + ", " + trueFlowPercent + ", " + getTrueFlowVolume() + ", " + trueFlowVolume;
	}

}
