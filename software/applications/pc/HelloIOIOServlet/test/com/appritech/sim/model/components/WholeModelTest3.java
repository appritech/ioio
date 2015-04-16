package com.appritech.sim.model.components;

import static org.junit.Assert.*;

import org.junit.Test;

import com.appritech.sim.model.MimicContainer;

public class WholeModelTest3 {

	@Test
	public void test() {
		
		MimicContainer mc = new MimicContainer();
		
		//Tanks t1 and t2 are the base sources, and go to v1 and v2 directly (return comes back through v11 and v12)
		mc.addComponent(new Tank("t1", Double.MAX_VALUE, Double.MAX_VALUE / 2, "v1"));
		mc.addComponent(new Tank("t2", Double.MAX_VALUE, Double.MAX_VALUE / 2, "v2"));
		mc.addComponent(new Valve("v1", "c1"));			//v1 and v2 both go into c1
		mc.addComponent(new Valve("v2", "c1"));
		
		//Valves v1 and v2 combine into c1, and then to a totalIgnore common pipe.
		mc.addComponent(new Combiner("c1", "totalIgnore"));
		mc.addComponent(new Valve("totalIgnore", "s1"));		//TODO: We should probably have a better name...

		//Splitter s1 splits totalIgnore into v3 and v4, so that they can got to p1 and p2, which go further on to v5 and v6
		mc.addComponent(new Splitter("s1", new String[] {"v3", "v4"}));
		mc.addComponent(new Valve("v3", "p1"));
		mc.addComponent(new Valve("v4", "p2"));
		mc.addComponent(new Pump("p1", 100, 100, "v5"));
		mc.addComponent(new Pump("p2", 100, 100, "v6"));
		mc.addComponent(new Valve("v5", "c2"));
		mc.addComponent(new Valve("v6", "c2"));
		
		//Valves v5 and v6 then get combined into Ignore2.
		mc.addComponent(new Combiner("c2", "Ignore2"));
		mc.addComponent(new Valve("Ignore2", "s2"));
		
		//Valves v7, v8, and v9 all go in between s2 and c3
		mc.addComponent(new Splitter("s2", new String[] {"v7", "v8", "v9"}, new double[]{.1,.6,1.0}, new double[]{.1,.4,.5}));
		mc.addComponent(new Valve("v7", "c3"));
		mc.addComponent(new Valve("v8", "c3"));
		mc.addComponent(new Valve("v9", "c3"));
		mc.addComponent(new Combiner("c3", "v10"));
		
		//Valve v10 goes between c3 and s3
		mc.addComponent(new Valve("v10", "s3"));
		mc.addComponent(new Splitter("s3", new String[] {"v11", "v12"}));
		
		//Valves v11 and v12 go from s3 back to the tanks t1 and t2
		mc.addComponent(new Valve("v11", "t1"));
		mc.addComponent(new Valve("v12", "t2"));
		
		mc.getComponent("v11").setMaxVolume(150);
		mc.getComponent("v12").setMaxVolume(150);
		
		//Valve v11 = (Valve) mc.getComponent("v11");
		//v11.setOpenPercentage(0);
		((Valve) mc.getComponent("v9")).setOpenPercentage(0);
		
		mc.connectComponents();
		
		mc.solveMimic();
		System.out.println(mc);
	}

}
