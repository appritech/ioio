package com.appritech.sim.model.components;

import java.util.LinkedList;
import java.util.List;

public abstract class Component {
	
	
	private List<Complaint> complaintLog = new LinkedList<Complaint>();
	private float x;
	private float y;
	private double currentAnger = 0;
	private boolean isAngry = false;
	
	public abstract double getPossibleFlow(Pump originPump, double oldMinPercent, double volumePerSecond);
	
	public void addToComplaintLog(Pump originPump, double flow) {
		complaintLog.add(new Complaint(originPump, flow));
		currentAnger += flow;
		
		if (currentAnger > 0) {
			isAngry = true;
		}
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
	
	public void setValueFromUser(float value) {
		//This can be overridden. It is a cheater method.
	}
	
	public void resetAnger() {
		complaintLog.clear();
		currentAnger = 0;
	}
	
	public boolean isAngry() {
		return isAngry;
	}

	class Complaint {
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
		
	}

}
