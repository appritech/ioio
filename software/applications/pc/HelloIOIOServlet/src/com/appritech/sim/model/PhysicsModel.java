package com.appritech.sim.model;

public class PhysicsModel {

	private PhysicsModel() {
		//Create all of the valves and connect them up.
	}
	
	public void update() {
		//Run through the graph and update everything...
	}
	
	public PhysicsModel getInstance() {
		return Holder.instance;
	}
	
	//Trying this pattern for singleton... never used before.
	private static class Holder {
        private static PhysicsModel instance = new PhysicsModel();
    }  
}
