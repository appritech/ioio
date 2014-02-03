package ioio.examples.hello_servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestXMLSender {

	private static final String USER_AGENT = "Mozilla/5.0";
	
	public static void main(String[] args) throws Exception {
		String url = "http://localhost:8182/saveConfig";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		String postdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<GetAccount xmlns='http://com.mysite/api/v1.0'>"
				+ "<CustomerRequest><AccNum>7128AR</AccNum></CustomerRequest>"
				+ "<CountryCode>CA</CountryCode>" + "<Branch>120</Branch>"
				+ "</GetAccount>";

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.write(createDummyXml().getBytes());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
	}
	
	private static String createDummyXml() {
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(System.lineSeparator());

		sb.append("<pins>");
		sb.append(System.lineSeparator());
		for(int i = 0; i < 24; i++) {
			sb.append("\t<pin num=\"" + i + "\" type=\"din\"/>");
			sb.append(System.lineSeparator());
		}
		sb.append("</pins>");
		
		return sb.toString();
	}
}
