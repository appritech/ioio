package com.appritech.sim.model.components;

public class Pump extends Component {
	
	private double mcrRating;
	private double mcrPressure;
	
	private Component sink;
	private Component source;
	
	public Pump(double mcrRating, double mcrPressure) {
		this.mcrRating = mcrRating;
		this.mcrPressure = mcrPressure;
	}

	public double getMcrRating() {
		return mcrRating;
	}

	public void setMcrRating(double mcrRating) {
		this.mcrRating = mcrRating;
	}

	public double getMcrPressure() {
		return mcrPressure;
	}

	public void setMcrPressure(double mcrPressure) {
		this.mcrPressure = mcrPressure;
	}
	
	public void setSink(Component sink) {
		this.sink = sink;
	}
	
	public void setSource(Component source) {
		this.source = source;
	}

	@Override
	public double getPossibleFlow(Pump originPump, double oldMinPercent, double volumePerSecond) {
		return sink.getPossibleFlow(this, 1, mcrRating);
	}
	
}
