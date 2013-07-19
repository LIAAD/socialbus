package pt.sapo.labs.crawl.twitter.streaming;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import pt.sapo.labs.crawl.twitter.TwitterEchoStatus;

public class SendAsyncMessage implements Runnable {

	private static Logger logger = Logger.getLogger(SendAsyncMessage.class);

	private String status;
	private String endPoint;

	public SendAsyncMessage(String endPoint,TwitterEchoStatus status) {
		this(endPoint,status.toJson());
	}
	
	public SendAsyncMessage(String endPoint,String status) {
		super();
		this.status = status;
		this.endPoint = endPoint;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("sending data");
		String url = this.endPoint;

		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "Streaming Client");

		
		String ipAddress = "localhost";
		
		try{
			ipAddress = new String(InetAddress.getLocalHost().getAddress());
		}catch(Exception e){
			logger.error("error getting ip address",e);
		}

		BufferedReader br = null;
		
		PostMethod method = new PostMethod(url);
		//method.addParameter("streaming", "1");
		method.addParameter("client_version", "2");
		method.addParameter("remoteAddress", ipAddress);
		method.addParameter("json", status);

		//method.setRequestHeader("charset", "utf-8");
		//method.setRequestHeader("Content-type", "multipart/form-data;charset=UTF-8");

		try{
			int returnCode = client.executeMethod(method);

			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				method.getResponseBodyAsString();
			} else {
				br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				String readLine;
				while(((readLine = br.readLine()) != null)) {
					logger.error(readLine);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			method.releaseConnection();
			if(br != null) try { br.close(); } catch (Exception fe) {logger.error(fe);}
		}
	}

}
