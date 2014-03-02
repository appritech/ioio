package ioio.examples.hello_servlet;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.IOIOConnectionManager.Thread;
import ioio.lib.util.pc.IOIOPcApplicationHelper;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.appritech.ioio.monitor.FlexIOIOLooper;

/**
 * Just testing the idea of having a running IOIO service that WebDriver starts
 * and passes to each of it's Servlets.
 * 
 * TODO: make this a singleton!
 * 
 * @author tgroshon
 * 
 */
public class IOIOBackgroundService implements IOIOLooperProvider {

	private static final long serialVersionUID = 1L;
	private boolean serviceRunning = true;
	private FlexIOIOLooper looper;
	private static final String CONFIG_FILENAME = "ioio_config.xml";

	/**
	 * Constructor
	 */
	public IOIOBackgroundService() {
		final IOIOBackgroundService self = this;
		Thread th = new Thread() {
			public synchronized void run() {
				runIOIOHelper();
			}

			@Override
			public void abort() {
				self.destroy();
			}
		};
		th.start();
	}

	/**
	 * Helper loop.
	 */
	private final void runIOIOHelper() {
		IOIOPcApplicationHelper helper = new IOIOPcApplicationHelper(this);
		helper.start();
		try {
			while (serviceRunning) {
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helper.stop();
		}
	}

	/**
	 * Destroy is called when the thread is aborted. We should flag the
	 * IOIOPcApplicationHelper to stop as well.
	 */
	public void destroy() {
		serviceRunning = false;
	}

	/**
	 * Copied from ConfigIOIOServlet.java
	 */
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		try {
			if (looper == null) {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				Document doc = docBuilder.parse(new File(CONFIG_FILENAME));
				looper = new FlexIOIOLooper(doc);
			}
			return looper;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// This is just backup plan. LED will flash on/off which tells us an
		// exception was thrown and it didn't confingure itself properly.
		return new BaseIOIOLooper() {
			private DigitalOutput led_;
			private Boolean ledOn;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				ledOn = !ledOn;
				led_.write(!ledOn);
				Thread.sleep(300);
			}
		};
	}
}
