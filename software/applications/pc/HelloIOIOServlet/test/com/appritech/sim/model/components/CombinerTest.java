package com.appritech.sim.model.components;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.appritech.sim.model.MimicContainer;

public class CombinerTest {

	@Test
	public void test() {
		//Here is the mimic:
		//On tank, t1, that is unrestricted
		// One splitter, s1, that splits into v1 and v2, both unrestricted
		// one combiner, that then combines v1 and v2 back
		// followed by one valve, v3, which is restricted to 50. 
		// The goal is to make sure that we combine appropriately.
		
		MimicContainer mc = new MimicContainer();
		mc.addComponent(new Tank("t1", Double.MAX_VALUE, Double.MAX_VALUE / 2, "ignore"));
		mc.addComponent(new Valve("ignore", "p1"));
		mc.addComponent(new Valve("ignore2", "s1"));
		mc.addComponent(new Splitter("s1", new String[]{"v1","v2"}));
		mc.addComponent(new Pump("p1", 100, 100, "ignore2"));
		mc.addComponent(new Valve("v1", "c1"));
		mc.addComponent(new Valve("v2", "c1"));
		mc.addComponent(new Combiner("c1", "v3"));
		mc.addComponent(new Valve("v3", "t1"));
		
		mc.getComponent("v3").setMaxVolume(50);
		
		mc.connectComponents();
		
		mc.solveMimic();
		
		
		
		
		
		
		
		fail("Not yet implemented");
	}

}
