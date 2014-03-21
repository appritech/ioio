package com.appritech.ioio.monitor;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

//TODO: This should be an abstract class, with abstract methods.
public class FlexIOBase 
{
	protected FlexIOBase(int pinNum)
	{
		this.pinNum = pinNum;
	}
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		//Should be overridden
	}
	public float update(float val) throws InterruptedException, ConnectionLostException
	{
		//Should be overridden
		return 0.0f;
	}
	public float getCalibratedValue() {
		//Should be overridden
		return 0.0f;
	}
	public void close()
	{
		//Should be overridden
	}
	
	public int pinNum;
}
