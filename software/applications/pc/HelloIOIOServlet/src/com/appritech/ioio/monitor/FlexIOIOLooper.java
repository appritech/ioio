package com.appritech.ioio.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
	
	private Document updateDoc = null;
	
	public void flagUpdateState(Document doc) {
		updateDoc = doc;
	}
	
	private void updateIOIOState(Document doc)
	{
		for(FlexIOBase iter : ioList) {
			iter.close();
		}
		ioList.clear();
		nameMap.clear();
		
		if(doc == null)
			return;
		
		NodeList listOfPins = doc.getElementsByTagName("pin");
        int numPins = listOfPins.getLength();
        System.out.println("Total no of pins : " + numPins);
        
        for(int i = 0; i < listOfPins.getLength(); i++) {
        	Element iter = (Element) listOfPins.item(i);
        	if(iter != null) {
        		int pinNum = Integer.parseInt(iter.getAttribute("num"));
        		String type = iter.getAttribute("type");
        		types[pinNum] = type;
        		String name = iter.getAttribute("name");
        		names[pinNum] = name;
        		
        		if(!nameMap.containsKey(name))
        			nameMap.put(name, new LinkedList<Integer>());
        		nameMap.get(name).add(pinNum);
        		
        		switch(type) {
        		case "din":
        			ioList.add(new FlexDigitalInput(pinNum, iter));
        			break;
        		case "dout":
        			ioList.add(new FlexDigitalOutput(pinNum, iter));
        			break;
        		case "ain":
        			ioList.add(new FlexAnalogInput(pinNum, iter));
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
	private float[] inputValuesCalibrated = new float[49];
	private String[] types = new String[49];
	private String[] names = new String[49];
	private HashMap<String, LinkedList<Integer>> nameMap = new HashMap<String, LinkedList<Integer>>();
	private final Cache<Integer, Float> digitalInputMap = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(10, TimeUnit.SECONDS).build();
	// Digital Inputs do have calibration in cases like 4-position switches, etc...
	private final Cache<Integer, Float> digitalCalibratedInputMap = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(10, TimeUnit.SECONDS).build();
	
	/** Sets the current status of an output pin. For digital outputs, value of 0.0f to turn off, and 1.0f to turn on */
	public void setOutputValue(int pinNum, float val) {
		outputValues[pinNum] = val;
	}
	
	public void setOutputValueByName(String name, float val) {
		if(nameMap.containsKey(name)) {
			for(int i : nameMap.get(name))
				outputValues[i] = val;
		}
			
	}
	
	/** Returns the current status of an input pin. For digital inputs, value will be 0.0f if off(False), and 1.0f if on(True) */
	public float getInputValue(int pinNum) {
		
		Float overrideValue = digitalInputMap.getIfPresent(pinNum);			//This will only be populated for digital inputs
		if(overrideValue != null) {
			digitalInputMap.invalidate(pinNum);				//Only get it once
			return overrideValue;
		}
		
		return inputValues[pinNum];
	}
	
	/** Returns the calibrated value from an input pin. For digital inputs, value will be 0.0f if off(False), and 1.0f if on(True) */
	public float getInputValueCalibrated(int pinNum) {
		Float overrideValue = digitalCalibratedInputMap.getIfPresent(pinNum);			//This will only be populated for digital inputs
		if(overrideValue != null) {
			digitalCalibratedInputMap.invalidate(pinNum);				//Only get it once
			return overrideValue;
		}
		return inputValuesCalibrated[pinNum];
	}
	
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException 
	{
		if(led == null) {
			led = new FlexDigitalOutput(IOIO.LED_PIN, null);
			led.setup(ioio_);
		}
		for (FlexIOBase iter : ioList)
		{
			iter.setup(ioio_);
		}
	}
	
	private static boolean flashFlag = false;
	public static boolean getFlashFlag() {
		return flashFlag;
	}

	@Override
	public void loop() throws ConnectionLostException, InterruptedException 
	{
		long milliseconds = System.currentTimeMillis();
		milliseconds = milliseconds % 1000;			// mod 1000 should give just the ms portion
		flashFlag = milliseconds > 500;
		
		if(updateDoc != null) {
			updateIOIOState(updateDoc);
			setup();
			updateDoc = null;
		}
		led.update(ledVal);
		for (FlexIOBase iter : ioList)
		{
			float newValue = iter.update(outputValues[iter.pinNum]);
			if(iter instanceof FlexDigitalInput) {
				if(inputValuesCalibrated[iter.pinNum] != newValue) {
					Float overrideValue = digitalInputMap.getIfPresent(iter.pinNum);			//This will only be populated for digital inputs
					if(overrideValue == null) {
						digitalInputMap.put(iter.pinNum, newValue);
					}
				}
				inputValues[iter.pinNum] = newValue;
				
				float newCalibratedValue = iter.getCalibratedValue();
				if(inputValuesCalibrated[iter.pinNum] != newCalibratedValue) {
					Float overrideValue = digitalCalibratedInputMap.getIfPresent(iter.pinNum);			//This will only be populated for digital inputs
					if(overrideValue == null) {
						digitalCalibratedInputMap.put(iter.pinNum, newCalibratedValue);
					}
				}
				inputValuesCalibrated[iter.pinNum] = newCalibratedValue;
			}
			else {
				inputValues[iter.pinNum] = newValue;
				inputValuesCalibrated[iter.pinNum] = iter.getCalibratedValue();
			}
		}
		
//		outputValues[11] = inputValues[1];
//		outputValues[12] = inputValues[2];
//		ledVal = inputValues[2];
		
		Thread.sleep(25);
	}

	public float getLedVal() {
		return ledVal;
	}

	public void setLedVal(float ledVal) {
		this.ledVal = ledVal;
	}

	public String getType(int pinNum) {
		return types[pinNum];
	}

	public String getName(int pinNum) {
		return names[pinNum];
	}

}
