package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.appritech.sim.model.components.helper.SplitValve;

public class Splitter extends Component {
	private Valve input;
	private List<SplitValve> outputs;
	
	public Splitter(Valve input, List<SplitValve> outputs) {
		super();
		this.input = input;
		this.outputs = outputs;
	}	
	
	public Valve getInput() {
		return input;
	}
	public void setInput(Valve input) {
		this.input = input;
	}
	public List<Valve> getOutputs() {
		return outputs.stream().map(v -> v.getValve()).collect(Collectors.toList());
	}
	public void setOutputs(List<SplitValve> outputs) {
		this.outputs = outputs;
	}
	
	//Yes, this one will need some testing.
	@Override
	public double getPossibleFlow(Pump originPump, double oldMinPercent, double volumePerSecond) {
		
		double[] maxFlow = new double[outputs.size()];
		double[] preferredFlow = new double[outputs.size()];
		double currentSum = 0;
		double flowToReturn = 0; 
		
		for (int i = 0; i < outputs.size(); i++) {
			SplitValve temp = outputs.get(i);
			double maxToSendDown = Math.min(temp.getMaxWeight(), oldMinPercent);
			double childMax = temp.getValve().getPossibleFlow(originPump, maxToSendDown, volumePerSecond);
			double currentMaxFlow = Math.min(temp.getMaxWeight(), childMax);
			maxFlow[i] = currentMaxFlow;
			double prefFlow = Math.min(temp.getNormalWeight(), currentMaxFlow);
			preferredFlow[i] = prefFlow;
			currentSum += prefFlow;
			
			temp.getValve().setTrueFlow(prefFlow);
			flowToReturn += prefFlow;
		}
		
		
		if (Math.abs(currentSum - 1) > 0.0005) { //We need someone to go above their comfort level.
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

}
