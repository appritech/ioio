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
	private boolean needsInvert = false;
	private float lastValue = -1.0f;
	@SuppressWarnings("unused")
	private String eventName;
	private float trueValue = 1.0f;
	
	private boolean useFlashFlag = true;
	
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
		
		String falseVal = xmlElement.getAttribute("FalseValue");
		
		if("1".equals(trueVal) && "0".equals(falseVal))
			useFlashFlag = true;
		else if("0".equals(trueVal) && "1".equals(falseVal))
			useFlashFlag = true;
		else
			useFlashFlag = false;
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
			boolean output = true;
			if(useFlashFlag) {
				if(val > 1.5f)			//This is 2, which means flash
					output = FlexIOIOLooper.getFlashFlag();
				else if(val > 0.5f)		//This is 1, which mean on
					output = true;
				else					//This should be 0 (or could be negative), which means off
					output = false;
			}
			else {
				float difference = val - trueValue;
				output = difference < 0.1f && difference > -0.1f;		//Basically Math.abs(difference) < 0.1
			}
			
			if(needsInvert)
				output = !output;
			
			System.out.println("Dout valueChanged. pinNum: " + pinNum + "\t val: " + val + "\t output: " + output);
			dout.write(output);
		}
		else if(useFlashFlag && lastValue > 1.5f)
		{
			boolean output = FlexIOIOLooper.getFlashFlag();
			if(needsInvert)
				output = !output;
			dout.write(output);
		}
		return lastValue;
	}
	
	@Override
	public float getCalibratedValue() {
		return lastValue;
	}
}
