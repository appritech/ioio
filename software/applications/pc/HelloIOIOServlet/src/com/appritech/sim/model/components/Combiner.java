package com.appritech.sim.model.components;

import java.util.Collection;

public class Combiner extends Component {
	private Collection<Line> inputs;
	private Line output;
	public Combiner(Collection<Line> inputs, Line output) {
		this.setInputs(inputs);
		this.setOutput(output);
	}
	public Line getOutput() {
		return output;
	}
	public void setOutput(Line output) {
		this.output = output;
	}
	public Collection<Line> getInputs() {
		return inputs;
	}
	public void setInputs(Collection<Line> inputs) {
		this.inputs = inputs;
	}
}
