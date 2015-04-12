package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Combiner extends Component {
	
	private Collection<Valve> inputs;
	private Component output;
	
	private String[] inputNames;
	private String outputName;
	
	public Combiner(String name, Collection<Valve> inputs, Component output) {
		super(name);
		this.setInputs(inputs);
		this.setOutput(output);
	}
	
	public Combiner(String name, String[] inputNames, String outputName) {
		super(name);
		this.inputNames = inputNames;
		this.outputName = outputName;
	}
	
	@Override
	public void setSource(Component source) {
		//Don't do anything. Combiners must set their sources manually in 'connectSelf', and they must be defined in their constructor
	};
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		output = components.get(outputName);
		inputs = new ArrayList<Valve>(inputNames.length);
		for(int i = 0; i < inputNames.length; i++) {
			inputs.add((Valve)components.get(inputNames[i]));
		}
		output.setSource(this);
	}
	
	public Component getOutput() {
		return output;
	}
	
	public void setOutput(Component output) {
		if (output instanceof Valve) {
			Valve o = (Valve) output;
			o.setSource(this);
		}
		this.output = output;
	}
	
	public Collection<Valve> getInputs() {
		return inputs;
	}
	
	public void setInputs(Collection<Valve> inputs) {
		for (Valve v : inputs) {
			v.setSink(this);
		}
		this.inputs = inputs;
	}
	
	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond) {
		double pushThrough = output.getPossibleFlowDown(originPump, oldMinPercent, volumePerSecond);
		addToComplaintLog(originPump, pushThrough);
		return pushThrough;
	}

	//Note: This is not quite the inverse of splitter.getPossibleFlowDown().
	// But we could make it, if we wanted it to be.
	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond) {
		//Get the sum of each input valve.
		//return the min of them all.
		
		double[] flowArray = new double[inputs.size()];
		
		double totalPossibleFlow = 0;
		Iterator<Valve> iter = inputs.iterator();
		for (int i = 0; i < inputs.size(); i++) {
			Valve v = iter.next();
			double flow = v.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond);
			flowArray[i] = flow;
			totalPossibleFlow += flow;
			
		}
		
		if (totalPossibleFlow > 1) {
			double ratio = 1 / totalPossibleFlow;
			Iterator<Valve> iter2 = inputs.iterator();
			for (int i = 0; i < inputs.size(); i++) {
				Valve v = iter2.next();
				v.setTrueFlow(flowArray[i] * ratio);
			}
		}
		
		double trueFlow = Math.min(1, totalPossibleFlow);
		addToComplaintLog(originPump, trueFlow);
		return Math.min(1, totalPossibleFlow);
	}
}
