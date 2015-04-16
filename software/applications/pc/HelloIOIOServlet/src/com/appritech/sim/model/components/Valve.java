package com.appritech.sim.model.components;

import java.util.HashMap;

import com.appritech.sim.model.MimicContainer;

public class Valve extends Component {
	
	private double openPercentage = 1.0;
	private double maxFlow = Double.MAX_VALUE;
	
	private Component source;
	private Component sink;
	
	private String sinkName;
	
	public Valve(String name) {
		super(name);
	}
	
	public Valve(String name, String sinkName) {
		super(name);
		this.sinkName = sinkName;
	}
	
	public void setMaximumVolume(double d) {
		setMaxVolume(d);
	}
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		sink = components.get(sinkName);
		sink.setSource(this);
	}

	public double getOpenPercentage() {
		return openPercentage;
	}

	public void setOpenPercentage(double openPercentage) {
		this.openPercentage = openPercentage;
	}

	public double getMaxFlow() {
		return maxFlow;
	}

	public void setMaxFlow(double maxFlow) {
		this.maxFlow = maxFlow;
	}
	
	public void setSink(Component sink) {
		this.sink = sink;
	}
	
	public void setSource(Component source) {
		this.source = source;
	}

	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMin, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double currentMin = openPercentage < oldMin ? Double.valueOf(openPercentage) : Double.valueOf(oldMin);
		if (mc.getOverrideMap().containsKey(this) && thisIsTheRealDeal == false) {
			System.out.println(mc.getOverrideMap().get(this));
			currentMin = currentMin * mc.getOverrideMap().get(this);
		}
		
		double newMin = 0;
		if (hasMaxVolume) {
			newMin = sink.getPossibleFlowDown(originPump, currentMin, volumePerSecond, mc, false);
			
			if (newMin * volumePerSecond > getMaxVolume() && thisIsTheRealDeal) {
				double ratio = getMaxVolume() / (newMin * volumePerSecond);
				newMin = sink.getPossibleFlowDown(originPump, currentMin * ratio, volumePerSecond, mc, thisIsTheRealDeal);
			}
		} else {
			newMin = sink.getPossibleFlowDown(originPump, currentMin, volumePerSecond, mc, thisIsTheRealDeal);
		}
		
		if (thisIsTheRealDeal) {
			addToComplaintLog(originPump, newMin * volumePerSecond, mc);
			setTrueFlowPercent(newMin);
			setTrueFlowVolume(newMin * volumePerSecond);
		}
		System.out.println("Old Flow in: " + oldMin + ", " + "Override: " + mc.getOverrideMap().get(this) + ", new flow: " + newMin + "\r\n");
		
		
		return newMin;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMin, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double currentMin = openPercentage < oldMin ? Double.valueOf(openPercentage) : Double.valueOf(oldMin);
		if (mc.getOverrideMap().containsKey(this) && thisIsTheRealDeal == false) {
			System.out.println(mc.getOverrideMap().get(this));
			currentMin = currentMin * mc.getOverrideMap().get(this);
		}
		
		double newMin = 0;
		if (hasMaxVolume) {
			newMin = source.getPossibleFlowUp(originPump, currentMin, volumePerSecond, mc, false);
			
			if (newMin * volumePerSecond > getMaxVolume() && thisIsTheRealDeal) {
				double ratio = getMaxVolume() / (newMin * volumePerSecond);
				newMin = source.getPossibleFlowUp(originPump, currentMin * ratio, volumePerSecond, mc, thisIsTheRealDeal);
			}
		} else {
			newMin = source.getPossibleFlowUp(originPump, currentMin, volumePerSecond, mc, thisIsTheRealDeal);
		}
		
		if (thisIsTheRealDeal) {
			addToComplaintLog(originPump, newMin * volumePerSecond, mc);
			setTrueFlowPercent(newMin);
			setTrueFlowVolume(newMin * volumePerSecond);
		}
		System.out.println("Old Flow in: " + oldMin + ", " + "Override: " + mc.getOverrideMap().get(this) + ", new flow: " + newMin + "\r\n");
		
		
		return newMin;

	}
	
}
