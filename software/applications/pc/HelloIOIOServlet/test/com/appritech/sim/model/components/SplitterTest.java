package com.appritech.sim.model.components;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.appritech.sim.model.components.helper.SplitValve;

public class SplitterTest {

	@Test
	public void testOne() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		SplitValve onlyValve = new SplitValve(steve, 0.5, 0.75);
		Splitter split = new Splitter("split", null, Arrays.asList(onlyValve));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, 0.75, 0.00005);
	}
	
	@Test
	public void testTwoFine() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		Valve bruce = new Valve("Bruce");
		bruce.setSink(t);
		
		SplitValve splitSteve = new SplitValve(steve, .25, .5);
		SplitValve splitBruce = new SplitValve(bruce, .25, .75);
		
		Splitter split = new Splitter("split", null, Arrays.asList(splitSteve, splitBruce));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, 1.0, 0.00005);
		
		assertEquals(steve.getTrueFlow(), 0.4, 0.00005);
		assertEquals(bruce.getTrueFlow(), 0.6, 0.00005);
		
	}
	
	@Test
	public void testTwoRestricted() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		Valve bruce = new Valve("Bruce");
		bruce.setSink(t);
		
		SplitValve splitSteve = new SplitValve(steve, .25, .4);
		SplitValve splitBruce = new SplitValve(bruce, .25, .5);
		
		Splitter split = new Splitter("split", null, Arrays.asList(splitSteve, splitBruce));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, 0.9, 0.00005);
		
		assertEquals(steve.getTrueFlow(), 0.4, 0.00005);
		assertEquals(bruce.getTrueFlow(), 0.5, 0.00005);
		
	}
	
	@Test
	public void testThreeFine() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		Valve bruce = new Valve("Bruce");
		bruce.setSink(t);
		
		Valve archer = new Valve("Archer");
		archer.setSink(t);
		
		SplitValve splitSteve = new SplitValve(steve, .3, 1);
		SplitValve splitBruce = new SplitValve(bruce, .5, 1);
		SplitValve splitArcher = new SplitValve(archer, .2, 1);
		
		Splitter split = new Splitter("split", null, Arrays.asList(splitSteve, splitBruce, splitArcher));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, 1.0, 0.00005);
		
		assertEquals(steve.getTrueFlow(), .3, 0.00005);
		assertEquals(bruce.getTrueFlow(), .5, 0.00005);
		assertEquals(archer.getTrueFlow(), .2, 0.00005);
	}

	@Test
	public void testThreeRestricted() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		Valve bruce = new Valve("Bruce");
		bruce.setSink(t);
		
		Valve archer = new Valve("Archer");
		archer.setSink(t);
		
		SplitValve splitSteve = new SplitValve(steve, .1, .15);
		SplitValve splitBruce = new SplitValve(bruce, .5, .5);
		SplitValve splitArcher = new SplitValve(archer, .2, .225);
		
		Splitter split = new Splitter("split", null, Arrays.asList(splitSteve, splitBruce, splitArcher));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, .875, 0.00005);
		
		assertEquals(steve.getTrueFlow(), .15, 0.00005);
		assertEquals(bruce.getTrueFlow(), .5, 0.00005);
		assertEquals(archer.getTrueFlow(), .225, 0.00005);
	}

	@Test
	public void testThreePartialRestriction() {
		Tank t = new Tank("t", Double.MAX_VALUE, 0.0);
		Valve steve = new Valve("Steve");
		steve.setSink(t);
		
		Valve bruce = new Valve("Bruce");
		bruce.setSink(t);
		
		Valve archer = new Valve("Archer");
		archer.setSink(t);
		
		SplitValve splitSteve = new SplitValve(steve, .1, .15);
		SplitValve splitBruce = new SplitValve(bruce, .5, 1);
		SplitValve splitArcher = new SplitValve(archer, .2, .2);
		
		Splitter split = new Splitter("split", null, Arrays.asList(splitSteve, splitBruce, splitArcher));
		
		double possibleFlow = split.getPossibleFlowDown(null, 1.0, Double.MAX_VALUE);
		assertEquals(possibleFlow, 1, 0.00005);
		
		assertEquals(steve.getTrueFlow(), .15/1.35, 0.00005);
		assertEquals(bruce.getTrueFlow(), 1/1.35, 0.00005);
		assertEquals(archer.getTrueFlow(), .2/1.35, 0.00005);
	}
	
}
