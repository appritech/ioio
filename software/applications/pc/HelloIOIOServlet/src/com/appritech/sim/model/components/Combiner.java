package com.appritech.sim.model.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.appritech.sim.model.DrawingLine;

import com.appritech.sim.model.MimicContainer;

public class Combiner extends Component {
	
	private Collection<Valve> inputs;
	private Component output;
	private HashMap<Pump, HashMap<Component, Double>> trueFlowPercentagesByPumpAndInput = new HashMap<Pump, HashMap<Component, Double>>();
	
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
	
	public Combiner(String name, String outputName, float x, float y) {
		this(name, outputName);
		this.x = x;
		this.y = y;
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

	@Override
	public List<DrawingLine> getConnectionLines() {
		if(output != null) {
			return Collections.singletonList(new DrawingLine(x, y, output.x, output.y));
		}
		return null;
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
	public void reset() {
		super.reset();
		trueFlowPercentagesByPumpAndInput.clear();	//Not sure if we even need to do this, since it is a map, and the values will be overwritten every time...
	}
	
	@Override
	public double getPossibleFlowDown(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component input) {
		double pushThrough = output.getPossibleFlowDown(originPump, oldMinPercent, volumePerSecond, mc, false, this);
		addToComplaintLog(originPump, pushThrough * volumePerSecond, mc);
		if (thisIsTheRealDeal) {
			if(!trueFlowPercentagesByPumpAndInput.containsKey(originPump))
				trueFlowPercentagesByPumpAndInput.put(originPump, new HashMap<Component, Double>());
			HashMap<Component, Double> trueFlowPercentagesForOriginPump = trueFlowPercentagesByPumpAndInput.get(originPump);
			trueFlowPercentagesForOriginPump.put(input, pushThrough);
			double cumulativePercent = trueFlowPercentagesForOriginPump.values().stream().reduce(0.0, Double::sum);			//Current sum
			setTrueFlowPercent(originPump, cumulativePercent);
			setTrueFlowVolume(originPump, cumulativePercent * volumePerSecond);
			
			//This is the dumbest, most lame thing (since we called this above), but we need to push this cumulative number down the tree again so that downstream can set their 'real deal'
			output.getPossibleFlowDown(originPump, cumulativePercent, volumePerSecond, mc, thisIsTheRealDeal, this);
		}
		return pushThrough;
	}

	//Note: This is not quite the inverse of splitter.getPossibleFlowDown().
	// But we could make it, if we wanted it to be.
	@Override
	public double getPossibleFlowUp(Pump originPump, double oldMinPercent, double volumePerSecond, MimicContainer mc, boolean thisIsTheRealDeal, Component output) {
		double[] flowArray = new double[inputs.size()];
		
		double totalPossibleFlow = 0;
		Iterator<Valve> iter = inputs.iterator();
		for (int i = 0; i < inputs.size(); i++) {
			Valve v = iter.next();
			double flow = v.getPossibleFlowUp(originPump, oldMinPercent, volumePerSecond, mc, false, this);
			flowArray[i] = flow;
			totalPossibleFlow += flow;
			
		}
		double ratio = 1;
		//17-Apr-2015 - RW - I don't see any reason to have this ratio. If the sum of our sources can support more than 1, 
		//                   then we should be happy about that, and not limit anything...
//		if (totalPossibleFlow > 1) {
//			ratio = 1 / totalPossibleFlow;
//		}
		
		double trueFlow = Math.min(oldMinPercent, totalPossibleFlow);
		addToComplaintLog(originPump, trueFlow * volumePerSecond, mc);
		if (thisIsTheRealDeal) {
			Iterator<Valve> iterator = inputs.iterator();
			for (int i = 0; i < inputs.size(); i++) {
				Valve v = iterator.next();
				//Note: We're sending the TRUE flow of this valve down the line, just so we will populate 
				// The true flow and true pressure appropriately. 
				v.getPossibleFlowUp(originPump, flowArray[i] * ratio, volumePerSecond, mc, true, this);
			}
			trueFlow = trueFlow * ratio;
			setTrueFlowPercent(originPump, trueFlow);
			setTrueFlowVolume(originPump, trueFlow * volumePerSecond);
		}
		return Math.min(oldMinPercent, Math.min(trueFlow, totalPossibleFlow));
	}
}
