package com.appritech.sim.model.components;

import java.util.HashMap;

public class Tank extends Component {
	private double capacity;
	private double currentVolume;
	private Component source;
	private Component sink;
	
	private String sinkName;
	
	public Tank(String name, double capacity, double currentVolume) {
		super(name);
		this.capacity = capacity;
		this.currentVolume = currentVolume;
	}
	
	public Tank(String name, double capacity, double currentVolume, String sinkName) {
		this(name, capacity, currentVolume);
		this.sinkName = sinkName;
	}
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		sink = components.get(sinkName);
		sink.setSource(this);
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getCurrentVolume() {
		return currentVolume;
	}

	public void setCurrentVolume(double currentVolume) {
		this.currentVolume = currentVolume;
	}

	public Component getSource() {
		return source;
	}

	public void setSource(Component source) {
		this.source = source;
	}

	public Component getSink() {
		return sink;
	}

	public void setSink(Component sink) {
		this.sink = sink;
	}

	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMin, double volumePerSecond) {
		if (capacity <= currentVolume) {
			return 0;
		}
		
		if (capacity - currentVolume < oldMin * volumePerSecond) {
			double remainingSpace = capacity - currentVolume;
			double percentAvailable = remainingSpace / volumePerSecond;
			return percentAvailable;
		}
		
		return oldMin;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMin, double volumePerSecond) {
		if (currentVolume <= 0) {
			return 0;
		}
		
		//If empty, return nothing
		//If currentVolume < oldMin * volumePerSecond
		if (currentVolume < oldMin * volumePerSecond) {
			double remainingVolume = currentVolume;
			double percentAvailable = remainingVolume / volumePerSecond;
			return percentAvailable;
		}
		
		return oldMin;
	}
	
}
