package com.appritech.sim.model;

import com.appritech.sim.model.components.Combiner;
import com.appritech.sim.model.components.Pump;
import com.appritech.sim.model.components.Splitter;
import com.appritech.sim.model.components.Tank;
import com.appritech.sim.model.components.Valve;

public class PhysicsModel {
	
	private MimicContainer mimicContainer;
	
	public MimicContainer getMimicContainer() {
		return mimicContainer;
	}

	public void init() {
		//Create all of the valves and connect them up.
		mimicContainer = new MimicContainer();
		
		float top = 0.2f;
		float middle = 0.4f;
		float bottom = 0.6f;
		
		float x = 0.1f;
		
		//Tanks t1 and t2 are the base sources, and go to v1 and v2 directly (return comes back through v11 and v12)
		mimicContainer.addComponent(new Tank("t1", Double.MAX_VALUE, Double.MAX_VALUE / 2, "v1", x, top - 0.1f));
		mimicContainer.addComponent(new Tank("t2", Double.MAX_VALUE, Double.MAX_VALUE / 2, "v2", x, bottom + 0.05f));
		x += 0.1f;
		mimicContainer.addComponent(new Valve("v1", "c1", x, top));			//v1 and v2 both go into c1
		mimicContainer.addComponent(new Valve("v2", "c1", x, bottom));
		
		x += 0.05f;
		//Valves v1 and v2 combine into c1, and then to a totalIgnore common pipe.
		mimicContainer.addComponent(new Combiner("c1", "totalIgnore", x, middle));
		x += 0.05f;
		mimicContainer.addComponent(new Valve("totalIgnore", "s1", x, middle));		//TODO: We should probably have a better name...

		x += 0.05f;
		//Splitter s1 splits totalIgnore into v3 and v4, so that they can got to p1 and p2, which go further on to v5 and v6
		mimicContainer.addComponent(new Splitter("s1", new String[] {"v3", "v4"}, x, middle));
		x += 0.05f;
		mimicContainer.addComponent(new Valve("v3", "p1", x, top));
		mimicContainer.addComponent(new Valve("v4", "p2", x, bottom));
		x += 0.05f;
		mimicContainer.addComponent(new Pump("p1", 100, 100, "v5", x, top));
		mimicContainer.addComponent(new Pump("p2", 100, 100, "v6", x, bottom));
		x += 0.05f;
		mimicContainer.addComponent(new Valve("v5", "c2", x, top));
		mimicContainer.addComponent(new Valve("v6", "c2", x, bottom));

		x += 0.05f;
		//Valves v5 and v6 then get combined into Ignore2.
		mimicContainer.addComponent(new Combiner("c2", "Ignore2", x, middle));
		x += 0.05f;
		mimicContainer.addComponent(new Valve("Ignore2", "s2", x, middle));

		x += 0.05f;
		//Valves v7, v8, and v9 all go in between s2 and c3
		mimicContainer.addComponent(new Splitter("s2", new String[] {"v7", "v8", "v9"}, x, middle));
		x += 0.05f;
		mimicContainer.addComponent(new Valve("v7", "c3", x, top));
		mimicContainer.addComponent(new Valve("v8", "c3", x, middle));
		mimicContainer.addComponent(new Valve("v9", "c3", x, bottom));
		x += 0.05f;
		mimicContainer.addComponent(new Combiner("c3", "v10", x, middle));
		
		x += 0.05f;
		//Valve v10 goes between c3 and s3
		mimicContainer.addComponent(new Valve("v10", "s3", x, middle));
		x += 0.05f;
		mimicContainer.addComponent(new Splitter("s3", new String[] {"v11", "v12"}, x, middle));

		x += 0.05f;
		//Valves v11 and v12 go from s3 back to the tanks t1 and t2
		mimicContainer.addComponent(new Valve("v11", "t1", x, top - 0.05f));
		mimicContainer.addComponent(new Valve("v12", "t2", x, bottom + 0.05f));
		
		mimicContainer.connectComponents();

	}  
	
	public void update() {
		//Run through the graph and update everything...
	}
	
	public static PhysicsModel getInstance() {
		return Holder.instance;
	}
	
	//Trying this pattern for singleton... never used before.
	private static class Holder {
        private static PhysicsModel instance = new PhysicsModel();
    }
}
