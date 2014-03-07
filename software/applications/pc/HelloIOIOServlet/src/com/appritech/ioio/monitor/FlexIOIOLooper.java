package com.appritech.ioio.monitor;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class FlexIOIOLooper extends BaseIOIOLooper 
{
	public FlexIOIOLooper(Document doc)
	{
		super();
		updateIOIOState(doc);
	}
	
	public void updateIOIOState(Document doc)
	{
		for(FlexIOBase iter : ioList) {
			iter.close();
		}
		ioList.clear();
		
		NodeList listOfPins = doc.getElementsByTagName("pin");
        int numPins = listOfPins.getLength();
        System.out.println("Total no of pins : " + numPins);
        
        for(int i = 0; i < listOfPins.getLength(); i++) {
        	Element iter = (Element) listOfPins.item(i);
        	if(iter != null) {
        		int pinNum = Integer.parseInt(iter.getAttribute("num"));
        		switch(iter.getAttribute("type")) {
        		case "din":
        			ioList.add(new FlexDigitalInput(pinNum, iter.getAttribute("subtype")));
        			break;
        		case "dout":
        			ioList.add(new FlexDigitalOutput(pinNum, iter.getAttribute("subtype")));
        			break;
        		case "ain":
        			ioList.add(new FlexAnalogInput(pinNum, iter.getAttribute("subtype")));
        			break;
        		}
        	}
        }
        
        System.out.println("Finished - Total no of pins : " + numPins);
	}
	
	private FlexDigitalOutput led;
	private ArrayList<FlexIOBase> ioList = new ArrayList<FlexIOBase>();
	private float ledVal;
	//There are 48 IO ports + the LED (which is port 0). Thus, this gives up LED = 0, and IO Ports 1 - 48
	private float[] outputValues = new float[49];
	private float[] inputValues = new float[49];
	
	/** Sets the current status of an output pin. For digital outputs, value of 0.0f to turn off, and 1.0f to turn on */
	public void setOutputValue(int pinNum, float val) {
		outputValues[pinNum] = val;
	}
	
	/** Returns the current status of an input pin. For digital inputs, value will be 0.0f if off(False), and 1.0f if on(True) */
	public float getInputValue(int pinNum) {
		return inputValues[pinNum];
	}
	
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException 
	{
		led = new FlexDigitalOutput(IOIO.LED_PIN, "FL");
		led.setup(ioio_);
		for (FlexIOBase iter : ioList)
		{
			iter.setup(ioio_);
		}
	}

	@Override
	public void loop() throws ConnectionLostException, InterruptedException 
	{
		led.update(ledVal);
		for (FlexIOBase iter : ioList)
		{
			inputValues[iter.pinNum] = iter.update(outputValues[iter.pinNum]);
		}
		
//		outputValues[11] = inputValues[1];
//		outputValues[12] = inputValues[2];
//		ledVal = inputValues[2];
		
		Thread.sleep(50);
	}

	public float getLedVal() {
		return ledVal;
	}

	public void setLedVal(float ledVal) {
		this.ledVal = ledVal;
	}

}
