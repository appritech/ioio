package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.appritech.sim.model.DrawingLine;
import com.appritech.sim.model.MimicContainer;
import com.appritech.sim.model.components.helper.SplitValve;

public class Splitter extends Component {
	private Valve input;
	private List<SplitValve> outputs;
	private String[] outputNames;
	private double[] maxWeights;
	private double[] normWeights;
	
	public Splitter(String name, Valve input, List<SplitValve> outputs) {
		super(name);
		setInput(input);
		setOutputs(outputs);
	}
	
	public Splitter(String name, String[] outputNames) {
		super(name);
		this.outputNames = outputNames;
		this.maxWeights = new double[outputNames.length];
		this.normWeights = new double[outputNames.length];
		for(int i = 0; i < outputNames.length; i++) {				//Default has max of 100% and normal weights equally split
			maxWeights[i] = 1.0;
			normWeights[i] = 1.0 / outputNames.length;
		}
	}
	
	public Splitter(String name, String[] outputNames, double[] maxWeights, double[] normWeights) {
		super(name);
		this.outputNames = outputNames;
		this.maxWeights = maxWeights;
		this.normWeights = normWeights;
	}
	
	public Splitter(String name, String[] outputNames, float x, float y) {
		this(name, outputNames);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		this.outputs = new ArrayList<SplitValve>(outputNames.length);
		for(int i = 0; i < outputNames.length; i++) {
			Valve v = (Valve)components.get(outputNames[i]);
			outputs.add(new SplitValve(v, normWeights[i], maxWeights[i]));
			v.setSource(this);
		}
	}

	@Override
	public List<DrawingLine> getConnectionLines() {
		ArrayList<DrawingLine> ret = new ArrayList<DrawingLine>();
		for(SplitValve sv : outputs) {
			Valve v = sv.getValve();
			ret.add(new DrawingLine(x, y, v.x, v.y));
		}
		return ret;
	}

	public Valve getInput() {
		return input;
	}
	@Override
	public void setSource(Component source) {
		if(source instanceof Valve)
			setInput((Valve)source);
		else
			System.out.println("Splitter's input must be a valve");
	}
	public void setInput(Valve input) {
		if(input != null)			//This is for junit tests
			input.setSink(this);
		this.input = input;
	}
	public List<Valve> getOutputs() {
		List<Valve> out = new LinkedList<Valve>();
		for (SplitValve v : outputs) {
			out.add(v.getValve());
		}
		return out;
	}
	public void setOutputs(List<SplitValve> outputs) {
		for (SplitValve v : outputs) {
			v.getValve().setSource(this);
		}
		this.outputs = outputs;
	}
	
	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component input) {
		
		double[] maxFlow = new double[outputs.size()];
		double[] preferredFlow = new double[outputs.size()];
		double currentSum = 0;
		double flowToReturn = 0; 
		ArrayList<Double> trueFlows = new ArrayList<Double>(outputs.size());
		
		for (int i = 0; i < outputs.size(); i++) {
			SplitValve temp = outputs.get(i);
			double maxToSendDown = Math.min(temp.getMaxWeight(), oldMinPercent);
			double childMax = temp.getValve().getPossibleFlowDown(originPump, maxToSendDown, volumePerSecond, mc, false, this);
			double currentMaxFlow = Math.min(temp.getMaxWeight(), childMax);
			maxFlow[i] = currentMaxFlow;
			double prefFlow = Math.min(temp.getNormalWeight(), currentMaxFlow) * oldMinPercent;
			preferredFlow[i] = prefFlow;
			currentSum += prefFlow;
			
			trueFlows.add(prefFlow);
			flowToReturn += prefFlow;
		}
		
		
		if (currentSum < oldMinPercent) { //We need someone to go above their comfort level.
			double sumOfMaxFlow = 0;
			for (double d : maxFlow) {
				sumOfMaxFlow += d;
			}
			
			flowToReturn = 0; 
			
			for (int i = 0; i < outputs.size(); i++) {
				SplitValve v = outputs.get(i);
				double trueFlow = oldMinPercent * (maxFlow[i] / sumOfMaxFlow);
				if(maxFlow[i] == 0.0) {
					trueFlow = 0.0;			//Handle this by itself, since sumOfMaxFlow is also 0, and we don't want to divide by zero
				}
				else if(sumOfMaxFlow == 0.0) {
					throw new RuntimeException("Shouldn't divide by zero");
				}
				else {
					trueFlow = oldMinPercent * (maxFlow[i] / sumOfMaxFlow);
				}
				trueFlow = Math.min(trueFlow, v.getMaxWeight());			//Probably don't need this one, since the one right below is better.
				trueFlow = Math.min(trueFlow, maxFlow[i]);
				trueFlows.set(i, trueFlow);
				flowToReturn += trueFlow;
			}
		}
		
		double theRealFlow = Math.min(flowToReturn, oldMinPercent);
		if (thisIsTheRealDeal) {
			
			
			double sum = 0;
			for (int i = 0; i < outputs.size(); i++) {
				Valve v = outputs.get(i).getValve();
				//Note: We're doign this so we can set these values. 
				double temp = v.getPossibleFlowDown(originPump, trueFlows.get(i), volumePerSecond, mc, true, this);
				sum += temp;
			}
			
			theRealFlow = Math.min(sum, oldMinPercent);
			addToComplaintLog(originPump, theRealFlow * volumePerSecond, mc);
			setTrueFlowPercent(originPump, theRealFlow);
			setTrueFlowVolume(originPump, theRealFlow * volumePerSecond);
		}
		return theRealFlow;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component output) {
		double flowUp = input.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond, mc, thisIsTheRealDeal, this);
		addToComplaintLog(originPump, flowUp * volumePerSecond, mc);
		if (thisIsTheRealDeal) {
			setTrueFlowPercent(originPump, flowUp);
			setTrueFlowVolume(originPump, flowUp * volumePerSecond);
		}
		return flowUp;
	}

}
