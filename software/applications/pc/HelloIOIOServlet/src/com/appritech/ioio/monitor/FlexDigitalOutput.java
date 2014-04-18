package com.appritech.ioio.monitor;

import org.w3c.dom.Element;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexDigitalOutput extends FlexIOBase
{
	public FlexDigitalOutput(int pinNum, Element xml)
	{
		super(pinNum);
		this.xmlElement = xml;
		eventName = Integer.toString(pinNum);
	}
	private Element xmlElement;
	private DigitalOutput dout;
	private Boolean needsInvert = false;
	private float lastValue = -1.0f;
	@SuppressWarnings("unused")
	private String eventName;
	private float trueValue = 1.0f;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		if(xmlElement == null)
		{
			//This is for the LED. 
			dout = ioio.openDigitalOutput(pinNum);
			return;
		}
		
		String subType = xmlElement.getAttribute("subtype");
		if(subType.endsWith("OD"))
		{
			dout = ioio.openDigitalOutput(pinNum, DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
			needsInvert = true;			//Open Drain mode works backwards. True leaves pin floating (i.e. LED not on), and false pulls it to ground (i.e. LED on)
		}
		else if(subType.endsWith("NL"))		//Floating
		{
			dout = ioio.openDigitalOutput(pinNum);
		}
		
		String trueVal = xmlElement.getAttribute("TrueValue");
		if(trueVal != null) {
			try {
				trueValue = Float.parseFloat(trueVal);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void close()
	{
		if(dout != null)
			dout.close();
	}
	
	@Override
	public float update(float val) throws InterruptedException, ConnectionLostException
	{
		if(dout == null)
			return val;
		
		if(val != lastValue)
		{
			lastValue = val;
			float difference = val - trueValue;
			Boolean output = difference < 0.1f && difference > -0.1f;		//Basically Math.abs(difference) < 0.1
			if(needsInvert)
				output = !output;
			
			System.out.println("Dout valueChanged. pinNum: " + pinNum + "\t output: " + output);
			
			dout.write(output);
		}
		return lastValue;
	}
	
	@Override
	public float getCalibratedValue() {
		return lastValue;
	}
}
