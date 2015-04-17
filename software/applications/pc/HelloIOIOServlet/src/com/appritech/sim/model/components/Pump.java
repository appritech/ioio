package com.appritech.sim.model.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.appritech.sim.model.DrawingLine;

import com.appritech.sim.model.MimicContainer;

public class Pump extends Component {
	
	private double mcrRating;
	private double mcrPressure;
	
	private Component sink;
	private Component source;
	
	private String sinkName;
	
	public Pump(String name, double mcrRating, double mcrPressure) {
		super(name);
		this.mcrRating = mcrRating;
		this.mcrPressure = mcrPressure;
	}
	
	public Pump(String name, double mcrRating, double mcrPressure, String sinkName) {
		super(name);
		this.mcrRating = mcrRating;
		this.mcrPressure = mcrPressure;
		this.sinkName = sinkName;
	}
	
	public Pump(String name, double mcrRating, double mcrPressure, String sinkName, float x, float y) {
		this(name, mcrRating, mcrPressure, sinkName);
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
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double flowDown = sink.getPossibleFlowDown(originPump, oldMinPercent, volumePerSecond, mc, thisIsTheRealDeal);
		if (thisIsTheRealDeal) {
			setTrueFlowPercent(flowDown);
			setTrueFlowVolume(flowDown * volumePerSecond);
		}
		return flowDown;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double flowUp = source.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond, mc, thisIsTheRealDeal);
		if (thisIsTheRealDeal) {
			setTrueFlowPercent(flowUp);
			setTrueFlowVolume(flowUp * volumePerSecond);
		}
		return flowUp;
	}
	
}
