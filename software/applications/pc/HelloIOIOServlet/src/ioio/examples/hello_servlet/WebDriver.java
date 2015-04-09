package ioio.examples.hello_servlet;

import ioio.examples.hello_servlet.APIConfig;
import ioio.examples.hello_servlet.APIStatus;
import ioio.examples.hello_servlet.APITrigger;
import ioio.examples.hello_servlet.IOIOBackgroundService;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import com.appritech.sim.SimEventServlet;
import com.appritech.sim.SimServlet;

/**
 * Abstraction object to act as the main runner of Tomcat and manager of Servlets & WebApps
 * @author tgroshon
 *
 */
public class WebDriver {
	
	public final int port = 8181;
	private static HashMap<String, Context> contextMap = new HashMap<String, Context>();
	
	private final URLMapper[] URLPatterns = new URLMapper[]{
			new URLMapper("/api/config", new APIConfig()),
			new URLMapper("/api/status", new APIStatus()),
			new URLMapper("/api/trigger", new APITrigger()),
			
			new URLMapper("/sim/core", new SimServlet()),
			new URLMapper("/sim/event", new SimEventServlet()),

//			new URLMapper("/ioiohello", new HelloIOIOServlet()),
//			new URLMapper("/ioioconfig", new ConfigIOIOServlet()),
	};
	
	
	/**
	 * Entry point to the system.  Creates an Embedded Tomcat instance, WebDriver instance, and maps
	 * URLs to Servlets and static content.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String baseDir = new File("web").getAbsolutePath();

		WebDriver driver = new WebDriver();
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(driver.port);
		
		try {
			IOIOBackgroundService.getInstance().start();
			
			// By using 'addWebapp', I can setup a static web contents folder and still map servlets	
			Context	ctx = tomcat.addWebapp("/", baseDir);
			contextMap.put(ctx.getName(), ctx);
			
			// Add servlets to Tomcat Instance
			for(URLMapper mapper: driver.URLPatterns){
				if (mapper.context != null){
					ctx = contextMap.get(mapper.getContext());
					if (ctx == null){
						ctx = tomcat.addContext(mapper.getContext(), baseDir);
						contextMap.put(ctx.getName(), ctx);
					}
				}
				Tomcat.addServlet(ctx, mapper.getServletName(), mapper.getServlet());
				ctx.addServletMapping(mapper.getPattern(), mapper.getServletName());
			}
			
			// Start Tomcat
			tomcat.start();
			tomcat.getServer().await();
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
		finally {
			IOIOBackgroundService.getInstance().destroy();
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
		private HttpServlet servlet;
		private String context;
		
		/**
		 * Constructor
		 * 
		 * @param url
		 * @param servletName
		 * @param servlet
		 */
		public URLMapper(String url, HttpServlet servlet){
			this.parsePattern(url);
			this.servlet = servlet;
		}
		
		
		/**
		 * Split URL into context and pattern strings.
		 * Context is the start of URL path
		 * Pattern is the end of URL path
		 * 
		 * @param pattern
		 */
		private void parsePattern(String pattern){
			String[] pieces = pattern.split("/");
			int end = pieces.length - 1;
			this.pattern = "/" + pieces[end];
			
			if (end > 0){
				StringBuilder builder = new StringBuilder();
				for (int i=0; i < end; i++){
					if(pieces[i].equals("")){
						continue;
					}
					builder.append("/");
					builder.append(pieces[i]);	
				}
				this.context = builder.toString();
			}
		}
		public String getPattern(){
			return this.pattern;
		}
		
		public String getServletName(){
			return this.servlet.getClass().getSimpleName();
		}
		
		public HttpServlet getServlet(){
			return this.servlet;
		}
		
		public String getContext() {
			return this.context;
		}
	}

}
