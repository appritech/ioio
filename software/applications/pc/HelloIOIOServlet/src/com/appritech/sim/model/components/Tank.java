package com.appritech.sim.model.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.appritech.sim.model.DrawingLine;

import com.appritech.sim.model.MimicContainer;

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
	
	public Tank(String name, double capacity, double currentVolume, String sinkName, float x, float y) {
		this(name, capacity, currentVolume, sinkName);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		sink = components.get(sinkName);
		sink.setSource(this);
	}

	@Override
	public List<DrawingLine> getConnectionLines() {
		if(sink != null) {
			return Collections.singletonList(new DrawingLine(x, y, sink.x, sink.y));
		}
		return null;
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
	public double getPossibleFlowDown(Pump originPump, double oldMin, double volumePerSecond, MimicContainer mc, boolean isTheRealDeal, Component input) {
		if (capacity <= currentVolume) {
			if (isTheRealDeal) {
				setTrueFlowPercent(originPump, 0);
				setTrueFlowVolume(originPump, 0);
			}
			return 0;
		}
		
		if (capacity - currentVolume < oldMin * volumePerSecond) {
			double remainingSpace = capacity - currentVolume;
			double percentAvailable = remainingSpace / volumePerSecond;
			if (isTheRealDeal) {
				setTrueFlowPercent(originPump, percentAvailable);
				setTrueFlowVolume(originPump, percentAvailable * volumePerSecond);	
			}
			return percentAvailable;
		}
		
		if (isTheRealDeal) {
			setTrueFlowPercent(originPump, oldMin);
			setTrueFlowVolume(originPump, oldMin * volumePerSecond);
		}
		return oldMin;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMin, double volumePerSecond, MimicContainer mc, boolean isTheRealDeal, Component output) {
		if (currentVolume <= 0) {
			if (isTheRealDeal) {
				setTrueFlowPercent(originPump, 0);
				setTrueFlowVolume(originPump, 0);
			}
			return 0;
		}
		
		//If empty, return nothing
		//If currentVolume < oldMin * volumePerSecond
		if (currentVolume < oldMin * volumePerSecond) {
			double remainingVolume = currentVolume;
			double percentAvailable = remainingVolume / volumePerSecond;
			if (isTheRealDeal) {
				setTrueFlowPercent(originPump, percentAvailable * -1);
				setTrueFlowVolume(originPump, percentAvailable * volumePerSecond * -1);
			}
			return percentAvailable;
		}
		
		if (isTheRealDeal) {
			setTrueFlowPercent(originPump, oldMin * -1);
			setTrueFlowVolume(originPump, oldMin * volumePerSecond * -1);
		}
		return oldMin;
	}
	
}
