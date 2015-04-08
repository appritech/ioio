package com.appritech.sim.model.components;

public class Tank extends Component {
	private double capacity;
	private double currentVolume;
	
	public Tank(double capacity, double currentVolume) {
		this.capacity = capacity;
		this.currentVolume = currentVolume;
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

	@Override
	public double getPossibleFlow(Pump originPump, double oldMin, double volumePerSecond) {
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
	
}
