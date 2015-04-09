package com.appritech.sim.model.components;

public class Pump extends Component {
	
	private double mcrRating;
	private double mcrPressure;
	
	private Component sink;
	private Component source;
	
	public Pump(String name, double mcrRating, double mcrPressure) {
		super(name);
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
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond) {
		return sink.getPossibleFlowDown(originPump, 1, volumePerSecond);
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond) {
		return source.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond);
	}
	
}
