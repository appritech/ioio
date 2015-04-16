package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.appritech.sim.model.MimicContainer;

public class Combiner extends Component {
	
	private Collection<Valve> inputs;
	private Component output;
	
	private String outputName;
	
	public Combiner(String name, Collection<Valve> inputs, Component output) {
		super(name);
		this.setInputs(inputs);
		this.setOutput(output);
	}
	
	public Combiner(String name, String outputName) {
		super(name);
		this.outputName = outputName;
		inputs = new ArrayList<Valve>();
	}
	
	@Override
	public void setSource(Component source) {
		//Don't do anything. Combiners must set their sources manually in 'connectSelf', and they must be defined in their constructor
		inputs.add((Valve)source);
	};
	
	@Override
	public void connectSelf(HashMap<String, Component> components) {
		output = components.get(outputName);
		
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
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double pushThrough = output.getPossibleFlowDown(originPump, oldMinPercent, volumePerSecond, mc, thisIsTheRealDeal);
		if (thisIsTheRealDeal) {
			addToComplaintLog(originPump, pushThrough * volumePerSecond, mc);
			setTrueFlowPercent(pushThrough);
			setTrueFlowVolume(pushThrough * volumePerSecond);
		}
		return pushThrough;
	}

	//Note: This is not quite the inverse of splitter.getPossibleFlowDown().
	// But we could make it, if we wanted it to be.
	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal) {
		double[] flowArray = new double[inputs.size()];
		
		double totalPossibleFlow = 0;
		Iterator<Valve> iter = inputs.iterator();
		for (int i = 0; i < inputs.size(); i++) {
			Valve v = iter.next();
			double flow = v.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond, mc, false);
			flowArray[i] = flow;
			totalPossibleFlow += flow;
			
		}
		double ratio = 1;
		if (totalPossibleFlow > 1) {
			ratio = 1 / totalPossibleFlow;
		}
		
		double trueFlow = Math.min(1, totalPossibleFlow);
		if (thisIsTheRealDeal) {
			Iterator<Valve> iterator = inputs.iterator();
			for (int i = 0; i < inputs.size(); i++) {
				Valve v = iterator.next();
				//Note: We're sending the TRUE flow of this valve down the line, just so we will populate 
				// The true flow and true pressure appropriately. 
				v.getPossibleFlowUp(originPump, flowArray[i] * ratio, volumePerSecond, mc, true);
			}
			trueFlow = trueFlow * ratio;
			setTrueFlowPercent(trueFlow);
			setTrueFlowVolume(trueFlow * volumePerSecond);
			addToComplaintLog(originPump, trueFlow * volumePerSecond, mc);
		}
		return Math.min(oldMinPercent, Math.min(trueFlow, totalPossibleFlow));
	}
}
