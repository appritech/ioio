package com.appritech.ioio.monitor;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexPwmOutput extends FlexIOBase
{
	public FlexPwmOutput(int pinNum, String description)
	{
		super(pinNum);
		this.description = description;
		eventName = Integer.toString(pinNum);
	}
	private String description;
	private PwmOutput pwmout;
	private Boolean needsInvert = false;
	private float lastValue = -1.0f;
	@SuppressWarnings("unused")
	private String eventName;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		
		if(description.endsWith("OD"))
		{
			pwmout = ioio.openPwmOutput(new DigitalOutput.Spec(pinNum, Mode.OPEN_DRAIN), 10000);
			needsInvert = true;			//Open Drain mode works backwards. True leaves pin floating (i.e. LED not on), and false pulls it to ground (i.e. LED on)
		}
		else
		{
			pwmout = ioio.openPwmOutput(pinNum, 10000);
		}
	}
	
	@Override
	public void update(float val) throws InterruptedException, ConnectionLostException
	{
		if(pwmout == null)
			return;
		
		if(val > 1.0f)
			val = 1.0f;
		if(val < 0.0f)
			val = 0.0f;
		
		if(val != lastValue)
		{
			lastValue = val;
			
			if(needsInvert)
				pwmout.setDutyCycle(1.0f - val);
			else
				pwmout.setDutyCycle(val);
		}
	}
}
