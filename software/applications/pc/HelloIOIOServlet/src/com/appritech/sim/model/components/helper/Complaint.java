package com.appritech.sim.model.components.helper;

import com.appritech.sim.model.components.Pump;

public class Complaint {
	private Pump originPump;
	private double flow;
	
	public Complaint(Pump originPump, double flow) {
		this.originPump = originPump;
		this.flow = flow;
	}

	public Pump getOriginPump() {
		return originPump;
	}

	public void setOriginPump(Pump originPump) {
		this.originPump = originPump;
	}

	public double getFlow() {
		return flow;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}
	
	@Override
	public String toString() {
		return "originPump: " + originPump.getName() + ", " + flow;
	}
	
}