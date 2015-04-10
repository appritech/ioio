package com.appritech.sim.model.components;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.appritech.sim.model.components.helper.SplitValve;

public class WholeModelTest {

	@Test
	public void test() {
		Tank t1 = new Tank("t1", Double.MAX_VALUE, Double.MAX_VALUE / 2);
		Tank t2 = new Tank("t2", Double.MAX_VALUE, Double.MAX_VALUE / 2);
		Valve v1 = new Valve("v1");
		Valve v2 = new Valve("v2");

		t1.setSink(v1);
		v1.setSource(t1);
		t2.setSink(v2);
		v2.setSource(t2);
		
		Valve v3 = new Valve("v3");
		Valve v4 = new Valve("v4");
		
		Pump p1 = new Pump("p1", 100, 100);
		Pump p2 = new Pump("p2", 100, 100);
		
		Valve v5 = new Valve("v5");
		Valve v6 = new Valve("v6");
		
		Valve v7 = new Valve("v7");
		Valve v8 = new Valve("v8");
		Valve v9 = new Valve("v9");
		
		Valve v10 = new Valve("v10");
		
		Valve v11 = new Valve("v11");
		Valve v12 = new Valve("v12");
		
		Valve totalIgnoreValve = new Valve("totalIgnore");
		
		Combiner c1 = new Combiner("c1", Arrays.asList(v1, v2), totalIgnoreValve);
		Splitter s1 = new Splitter("s1", totalIgnoreValve, Arrays.asList(new SplitValve(v3, .5, 1), new SplitValve(v4, .5, 1)));
		
		Valve totalIgnoreValve2 = new Valve("Ignore2");
		
		Combiner c2 = new Combiner("c2", Arrays.asList(v5, v6), totalIgnoreValve2);
		Splitter s2 = new Splitter("s2", totalIgnoreValve2, Arrays.asList(new SplitValve(v7, 1.0/3, 1), new SplitValve(v8, 1.0/3, 1), new SplitValve(v9, 1.0/3, 1)));;
	
		Combiner c3 = new Combiner("c3", Arrays.asList(v7, v8, v9), v10);
	
		Splitter s3 = new Splitter("s3", v10, Arrays.asList(new SplitValve(v11, .5, 1), new SplitValve(v12, .5, 1)));
		
		v11.setSink(t1);
		v12.setSink(t2);
		
		v3.setSink(p1);
		p1.setSource(v3);
		v4.setSink(p1);
		p2.setSource(v4);
		
		p1.setSink(v5);
		v5.setSource(p1);
		
		p2.setSink(v6);
		v6.setSource(p2);
		
		List<Component> allComponents = Arrays.asList(t1, t2, v1, v2, c1, s1, totalIgnoreValve, totalIgnoreValve2, v3, v4, p1, p2, v6, v5, c2, s2, v7, v8, v9, c3, v10, s3, v11, v12);
		
		double p1Down = p1.getPossibleFlowDown(p1, 1.0, p1.getMcrRating());
		double p1Up = p1.getPossibleFlowUp(p1, 1.0, p1.getMcrRating());
		
		double p2Down = p2.getPossibleFlowDown(p2, 1.0, p2.getMcrRating());
		double p2Up = p2.getPossibleFlowUp(p2, 1.0, p2.getMcrRating());
		
		System.out.println("p1 down = " + p1Down);
		System.out.println("p1 up = " + p1Up);
		System.out.println("p2 down = " + p2Down);
		System.out.println("p2 up = " + p2Up);
	
		System.out.println("Holy moly, we made it through");
	}

}
