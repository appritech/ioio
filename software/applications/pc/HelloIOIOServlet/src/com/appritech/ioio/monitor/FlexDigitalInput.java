package com.appritech.ioio.monitor;

import org.w3c.dom.Element;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class FlexDigitalInput extends FlexIOBase
{
	public FlexDigitalInput(int pinNum, Element xml)
	{
		super(pinNum);
		this.xmlElement = xml;
		this.pinNum = pinNum;
	}
	private Element xmlElement;
	private DigitalInput din;
	private Boolean needsInvert = false;
	public Boolean lastValue = false;
	
	private float trueValue = 1.0f;
	private float falseValue = 0.0f;
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException
	{
		String subType = xmlElement.getAttribute("subtype");
		if(subType.endsWith("FL"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.FLOATING);
		}
		else if(subType.endsWith("PU"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);
			needsInvert = true;			//In order to read if the 'switch' is pressed or not, need to invert (i.e. pressed = low = 0)
		}
		else if(subType.endsWith("PD"))
		{
			din = ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_DOWN);
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
		
		String falseVal = xmlElement.getAttribute("TrueValue");
		if(falseVal != null) {
			try {
				falseValue = Float.parseFloat(falseVal);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
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
//			System.out.println("Din valueChanged. pinNum: " + pinNum + "\t value: " + readValue);
		}
		return lastValue ? trueValue : falseValue;
	}
	
	@Override
	public float getCalibratedValue() {
		return lastValue ? trueValue : falseValue;
	}
}
