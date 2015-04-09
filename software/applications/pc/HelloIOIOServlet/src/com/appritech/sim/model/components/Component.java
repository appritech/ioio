package com.appritech.sim.model.components;

import java.util.LinkedList;
import java.util.List;

public abstract class Component {
	
	
	private List<Complaint> complaintLog = new LinkedList<Complaint>();
	private double currentAnger = 0;
	private boolean isAngry = false;
	private String name;
	private double trueFlow;
	
	public Component(String name) {
		this.name = name;
	}
	
	public abstract double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond);
	public abstract double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond);
	
	public void addToComplaintLog(Pump originPump, double flow) {
		complaintLog.add(new Complaint(originPump, flow));
		currentAnger += flow;
		
		if (currentAnger > 0) {
			isAngry = true;
		}
	}
	
	public void resetAnger() {
		complaintLog.clear();
		currentAnger = 0;
	}
	
	public boolean isAngry() {
		return isAngry;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTrueFlow() {
		return trueFlow;
	}

	public void setTrueFlow(double trueFlow) {
		this.trueFlow = trueFlow;
	}

	public void setAngry(boolean isAngry) {
		this.isAngry = isAngry;
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
