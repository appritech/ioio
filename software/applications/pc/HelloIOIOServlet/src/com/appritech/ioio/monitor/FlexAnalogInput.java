package com.appritech.ioio.monitor;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexAnalogInput extends FlexIOBase
{
	public FlexAnalogInput(int pinNum, String description)
	{
		super(pinNum);
		this.description = description;
		this.pinNum = pinNum;
		eventName = Integer.toString(pinNum);
	}
	private String description;
	private AnalogInput ain;
	private Boolean needsInvert = false;
	public float lastValue = -1.0f;
	private String eventName;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		ain = ioio.openAnalogInput(pinNum);
		if(description.endsWith("Invert"))
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
			System.out.println("Ain valueChanged. pinNum: " + pinNum + "\t value: " + readValue);
		}
		return readValue;
	}
}
