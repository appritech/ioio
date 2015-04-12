package com.appritech.sim.model.components;

import java.util.HashMap;

public class Valve extends Component {
	
	private double openPercentage = 1.0;
	private double maxFlow = Double.MAX_VALUE;
	private double trueFlow = 0; 
	
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
	
	public void setTrueFlow(double d) {
		this.trueFlow = d;
	}
	
	public double getTrueFlow() {
		return trueFlow;
	}
	
	public void setSink(Component sink) {
		this.sink = sink;
	}
	
	public void setSource(Component source) {
		this.source = source;
	}

	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMin, double volumePerSecond) {
		double currentMin = openPercentage < oldMin ? openPercentage : oldMin;
		double newMin = sink.getPossibleFlowDown(originPump, currentMin, volumePerSecond);
		addToComplaintLog(originPump, newMin);
		return newMin;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMin, double volumePerSecond) {
		double currentMin = openPercentage < oldMin ? openPercentage : oldMin;
		double newMin = source.getPossibleFlowUp(originPump, currentMin, volumePerSecond);
		addToComplaintLog(originPump, newMin);
		return newMin;
	}
	
}
