package com.appritech.ioio.monitor;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexDigitalInput extends FlexIOBase
{
	public FlexDigitalInput(int pinNum, String description)
	{
		super(pinNum);
		this.description = description;
		this.pinNum = pinNum;
		eventName = Integer.toString(pinNum);
	}
	private String description;
	private DigitalInput din;
	private Boolean needsInvert = false;
	public Boolean lastValue = false;
	private String eventName;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		if(description.endsWith("FL"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.FLOATING);
		}
		else if(description.endsWith("PU"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);
			needsInvert = true;			//In order to read if the 'switch' is pressed or not, need to invert (i.e. pressed = low = 0)
		}
		else if(description.endsWith("PD"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_DOWN);
		}
	}
	
	@Override
	public void close()
	{
		din.close();
	}
	
	@Override
	public float update(float val) throws InterruptedException, ConnectionLostException
	{
		if(din == null)
			return 0.0f;
		
		Boolean readValue = din.read();
		if(needsInvert)
			readValue = !readValue;
		
		if(lastValue != readValue)
		{
			lastValue = readValue;
			//TODO: Dispatch event on change
			System.out.println("Din valueChanged. pinNum: " + pinNum + "\t value: " + readValue);
		}
		return readValue ? 1.0f : 0.0f;
	}
}
