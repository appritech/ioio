package com.appritech.sim.model.components;

import java.util.Collection;

import com.appritech.sim.model.components.Valve;

public class Line extends Component {
	private Collection<Valve> valves;
	public Line(Collection<Valve> valves) {
		super();
		this.valves = valves;
	}
	public Collection<Valve> getValves() {
		return valves;
	}
	public void setValves(Collection<Valve> valves) {
		this.valves = valves;
	}
	
}
