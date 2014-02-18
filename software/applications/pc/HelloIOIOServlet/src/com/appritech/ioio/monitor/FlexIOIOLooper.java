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
	
	static
	{
		//There are 48 IO ports + the LED (which is port 0). Thus, this gives up LED = 0, and IO Ports 1 - 48
		outputValues = new float[49];
	}
	
	private void initBlank() {
		//Create everything as Din
		for(int i = 1; i <= 46; i++)
			ioList.add(new FlexDigitalInput(i, ""));
	}
	
	public void updateIOIOState(Document doc)
	{
		for(FlexIOBase iter : ioList) {
			iter.close();
		}
		ioList.clear();
		
		NodeList listOfPins = doc.getElementsByTagName("pin");
        int totalPersons = listOfPins.getLength();
        System.out.println("Total no of pins : " + totalPersons);
        
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
        
        System.out.println("Finished - Total no of pins : " + totalPersons);
	}
	
	private FlexDigitalOutput led;
	private ArrayList<FlexIOBase> ioList = new ArrayList<FlexIOBase>();
	private float ledVal;
	public static float[] outputValues;
	
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
			iter.update(outputValues[iter.pinNum]);
		}
		
		Thread.sleep(50);
	}

	public float getLedVal() {
		return ledVal;
	}

	public void setLedVal(float ledVal) {
		this.ledVal = ledVal;
	}

}
