package com.appritech.ioio.monitor;

import org.w3c.dom.Element;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexAnalogInput extends FlexIOBase
{
	public FlexAnalogInput(int pinNum, Element xml)
	{
		super(pinNum);
		this.xmlElement = xml;
		this.pinNum = pinNum;
		eventName = Integer.toString(pinNum);
	}
	private Element xmlElement;
	private AnalogInput ain;
	private Boolean needsInvert = false;
	public float lastValue = -1.0f;
	private String eventName;
	
	private float minInput;
	private float centerInput;
	private float maxInput;
	private float minOutput;
	private float centerOutput;
	private float maxOutput;
	private float deadband;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		String subType = xmlElement.getAttribute("subtype");
		ain = ioio.openAnalogInput(pinNum);
		if(subType.endsWith("Invert"))
			needsInvert = true;
	}
	
	@Override
	public void close()
	{
		ain.close();
	}
	
	@Override
	public float update(float val) throws InterruptedException, ConnectionLostException
	{
		if(ain == null)
			return 0.0f;
		
		float readValue = ain.read();
		if(needsInvert)
			readValue = 1.0f - readValue;
		
		if(lastValue != readValue)
		{
			lastValue = readValue;
			//TODO: Dispatch event on change
//			if(pinNum == 44)
//				System.out.println("Ain valueChanged. pinNum: " + pinNum + "\t value: " + readValue);
		}
		return lastValue;
	}
	
	@Override
	public float getCalibratedValue() {
		if(lastValue < minInput) {
			return minOutput;
		}
		else if(lastValue < centerInput - deadband) {
			//Lower half
			float percent = (lastValue - minInput) / ((centerInput - deadband) - minInput);
			return minOutput + percent * ((centerInput - deadband) - minInput);
		}
		else if(lastValue < centerInput + deadband) {
			//deadband
			return centerOutput;
		}
		else if(lastValue < maxOutput) {
			//Upper half
			float percent = (lastValue - (centerInput + deadband)) / (maxInput - (centerInput + deadband));
			return (centerInput + deadband) + percent * (maxInput - (centerInput + deadband));
		}
		else {
			return maxOutput;
		}
	}
}
