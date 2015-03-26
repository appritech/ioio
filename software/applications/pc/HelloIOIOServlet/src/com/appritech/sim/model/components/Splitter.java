package com.appritech.sim.model.components;

import java.util.Collection;

public class Splitter extends Component {
	private Line input;
	private Collection<Line> outputs;
	public Splitter(Line input, Collection<Line> outputs) {
		super();
		this.input = input;
		this.outputs = outputs;
	}
	public Line getInput() {
		return input;
	}
	public void setInput(Line input) {
		this.input = input;
	}
	public Collection<Line> getOutputs() {
		return outputs;
	}
	public void setOutputs(Collection<Line> outputs) {
		this.outputs = outputs;
	}
	
}
