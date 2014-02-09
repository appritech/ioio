package com.appritech.ioio.monitor;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexDigitalOutput extends FlexIOBase
{
	public FlexDigitalOutput(int pinNum, String description)
	{
		super(pinNum);
		this.description = description;
		eventName = Integer.toString(pinNum);
	}
	private String description;
	private DigitalOutput dout;
	private Boolean needsInvert = false;
	private float lastValue = -1.0f;
	@SuppressWarnings("unused")
	private String eventName;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		
		if(description.endsWith("OD"))
		{
			dout = ioio.openDigitalOutput(pinNum, DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
			needsInvert = true;			//Open Drain mode works backwards. True leaves pin floating (i.e. LED not on), and false pulls it to ground (i.e. LED on)
		}
		else if(description.endsWith("FL"))		//Floating
			
		{
			dout = ioio.openDigitalOutput(pinNum);
		}
	}
	
	@Override
	public void close()
	{
		dout.close();
	}
	
	@Override
	public void update(float val) throws InterruptedException, ConnectionLostException
	{
		if(dout == null)
			return;
		
		if(val != lastValue)
		{
			lastValue = val;
			Boolean output = val > 0.5f;
			if(needsInvert)
				output = !output;
			
			dout.write(output);
		}
	}
}
