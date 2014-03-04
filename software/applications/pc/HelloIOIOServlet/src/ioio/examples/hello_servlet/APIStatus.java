package ioio.examples.hello_servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stripped down Servlet to get the status of one or more IOIO pins.
 * @author tgroshon
 *
 */
public class APIStatus extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		//Should be able to do something like the following:
//		int pinNum = 1;
//		float val = IOIOBackgroundService.getInstance().getInputValue(pinNum);
		//Then, if it is a digital input, val = 0.0f if it is 'off' and 1.0f if it is 'on' (or false/true, however you want to think about it).
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
	}
}
