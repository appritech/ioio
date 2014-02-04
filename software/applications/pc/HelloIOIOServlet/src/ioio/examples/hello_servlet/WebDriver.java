package ioio.examples.hello_servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * Abstraction object to act as the main runner of Tomcat and manager of Servlets & WebApps
 * @author tgroshon
 *
 */
public class WebDriver {
	
	public final int port = 8181;
	
	// Configure Servlets and their URL patterns
	private final URLMapper[] URLPatterns = new URLMapper[]{
			new URLMapper("/ioiohello", "HelloIOIOServlet", new HelloIOIOServlet()),
			new URLMapper("/ioioconfig", "ConfigIOIOServlet", new ConfigIOIOServlet()),
	};
	
	public static void main(String[] args) {
		WebDriver driver = new WebDriver();
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(driver.port);
		try {
			// By using 'addWebapp', I can setup a static web contents folder and still map servlets	
			Context	ctx2 = tomcat.addWebapp("/", new File("web").getAbsolutePath());
			
			// Add servlets to Tomcat Instance
			for(URLMapper mapper: driver.URLPatterns){
				Tomcat.addServlet(ctx2, mapper.getServletName(), mapper.getServlet());
				ctx2.addServletMapping(mapper.getPattern(), mapper.getServletName());
			}
			
			// Start Tomcat
			tomcat.start();
			tomcat.getServer().await();
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Helper class for use in URLPatterns Array
	 * Map Servlet to a Name and URL pattern
	 * 
	 * @author tgroshon
	 *
	 */
	public class URLMapper {
		private String pattern;
		private String servletName;
		private IOIOServlet servlet;
		
		public URLMapper(String pattern, String servletName, IOIOServlet servlet){
			this.pattern = pattern;
			this.servletName = servletName;
			this.servlet = servlet;
		}
		
		public String getPattern(){
			return this.pattern;
		}
		
		public String getServletName(){
			return this.servletName;
		}
		
		public IOIOServlet getServlet(){
			return this.servlet;
		}
	}

}
