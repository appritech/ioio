package com.appritech.sim.model.components;

import java.util.Collection;

public class Combiner extends Component {
	
	private Collection<Valve> inputs;
	private Valve output;
	
	public Combiner(Collection<Valve> inputs, Valve output) {
		this.setInputs(inputs);
		this.setOutput(output);
	}
	
	public Valve getOutput() {
		return output;
	}
	
	public void setOutput(Valve output) {
		this.output = output;
	}
	
	public Collection<Valve> getInputs() {
		return inputs;
	}
	
	public void setInputs(Collection<Valve> inputs) {
		this.inputs = inputs;
	}
	
	@Override
	public double getPossibleFlow(Pump originPump, double oldMinPercent, double volumePerSecond) {
		double pushThrough = output.getPossibleFlow(originPump, oldMinPercent, volumePerSecond);
		addToComplaintLog(originPump, pushThrough);
		return pushThrough;
	}
}
