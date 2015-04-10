package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.appritech.sim.model.components.helper.SplitValve;

public class Splitter extends Component {
	private Valve input;
	private List<SplitValve> outputs;
	private String inputName;
	private String[] outputNames;
	private double[] maxWeights;
	private double[] normWeights;
	
	public Splitter(String name, Valve input, List<SplitValve> outputs) {
		super(name);
		setInput(input);
		setOutputs(outputs);
	}
	
	public Splitter(String name, String inputName, String[] outputNames) {
		super(name);
		this.inputName = inputName;
		this.outputNames = outputNames;
		this.maxWeights = new double[outputNames.length];
		this.normWeights = new double[outputNames.length];
		for(int i = 0; i < outputNames.length; i++) {				//Default has max of 100% and normal weights equally split
			maxWeights[i] = 1.0;
			normWeights[i] = 1.0 / outputNames.length;
		}
	}
	
	public Splitter(String name, String inputName, String[] outputNames, double[] maxWeights, double[] normWeights) {
		super(name);
		this.inputName = inputName;
		this.outputNames = outputNames;
		this.maxWeights = maxWeights;
		this.normWeights = normWeights;
	}
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		this.input = (Valve)components.get(inputName);
		this.outputs = new ArrayList<SplitValve>(outputNames.length);
		for(int i = 0; i < outputNames.length; i++) {
			outputs.add(new SplitValve((Valve)components.get(outputNames[i]), normWeights[i], maxWeights[i]));
		}
	}

	public Valve getInput() {
		return input;
	}
	public void setInput(Valve input) {
		input.setSink(this);
		this.input = input;
	}
	public List<Valve> getOutputs() {
		return outputs.stream().map(v -> v.getValve()).collect(Collectors.toList());
	}
	public void setOutputs(List<SplitValve> outputs) {
		for (SplitValve v : outputs) {
			v.getValve().setSource(this);
		}
		this.outputs = outputs;
	}
	
	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond) {
		
		double[] maxFlow = new double[outputs.size()];
		double[] preferredFlow = new double[outputs.size()];
		double currentSum = 0;
		double flowToReturn = 0; 
		
		for (int i = 0; i < outputs.size(); i++) {
			SplitValve temp = outputs.get(i);
			double maxToSendDown = Math.min(temp.getMaxWeight(), oldMinPercent);
			double childMax = temp.getValve().getPossibleFlowDown(originPump, maxToSendDown, volumePerSecond);
			double currentMaxFlow = Math.min(temp.getMaxWeight(), childMax);
			maxFlow[i] = currentMaxFlow;
			double prefFlow = Math.min(temp.getNormalWeight(), currentMaxFlow);
			preferredFlow[i] = prefFlow;
			currentSum += prefFlow;
			
			temp.getValve().setTrueFlow(prefFlow);
			flowToReturn += prefFlow;
		}
		
		
		if (currentSum > 1) { //We need someone to go above their comfort level.
			double sumOfMaxFlow = 0;
			for (double d : maxFlow) {
				sumOfMaxFlow += d;
			}
			
			flowToReturn = 0; 
			
			for (int i = 0; i < outputs.size(); i++) {
				SplitValve v = outputs.get(i);
				double trueFlow = oldMinPercent * (maxFlow[i] / sumOfMaxFlow);
				trueFlow = Math.min(trueFlow, v.getMaxWeight());
				v.getValve().setTrueFlow(trueFlow);
				flowToReturn += trueFlow;
			}
		}
		
		double theRealFlow = Math.min(flowToReturn, oldMinPercent);
		
		addToComplaintLog(originPump, theRealFlow);
		return theRealFlow;
	}

	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond) {
		double flowUp = input.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond);
		addToComplaintLog(originPump, flowUp);
		return flowUp;
	}

}
