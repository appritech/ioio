package com.appritech.sim.model.components.helper;

import com.appritech.sim.model.components.Valve;

public class SplitValve {
	private double normalWeight;
	private double maxWeight;
	private Valve valve;
	
	public SplitValve(Valve valve, double normalWeight, double maxWeight) {
		this.valve = valve;
		this.normalWeight = normalWeight;
		this.maxWeight = maxWeight;
	}

	public double getNormalWeight() {
		return normalWeight;
	}

	public void setNormalWeight(double normalWeight) {
		this.normalWeight = normalWeight;
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public Valve getValve() {
		return valve;
	}

	public void setValve(Valve valve) {
		this.valve = valve;
	}	
}
