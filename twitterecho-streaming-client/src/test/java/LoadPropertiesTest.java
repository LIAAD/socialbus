import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;


public class LoadPropertiesTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testLoadTwitterAccounts(){
	
		String rawString = "561129992-BbPZ3CiZGKxnqOojvLRPct3ZmtqMCBhUqFYf0rac,9JUKvMBqr87k8QEXyYuuCtSNQ3RzCOYfkshDWq20uXk;561218910-af0fgygj7LkXgzRaGohfosnwAbZdKV6ZgLXAJiaz,d1d3iK2ZoTwZz5WS64jXLKMJiXIngActEktNWO9e63E;561222966-Ox2C5Wx1819Jfd6iuTilc2pWaNdQwLFX97xUhXom,bQwgHujSZ2l8spCmWXMJgOTX0u1ht0RAoc8GtHcmWnk;561225288-5v2oRql4JDKUCpC9AnPJSpCycC0PnGdQ9XKwusKR,4bMxinVBLFof5xTmgxg9krYbjK6WERx2fL7ZGGvMs;561229632-u4eUfNToJ56aZmdeQvQQJH6hIOqJw0RDySmSnyB9,NHBY3ZY8t1dq7pAOtxtoKbXyz2kmlbE7TtAnpCU0;561147728-x6YH7JFXvSaFXH2oiwcdYemArL3OTkHmL2JGZI8,ybUl99X6HRfNjFw4UkuiyynQwyRGic9AwywvPpl0eg;561241713-Iu0qm4FfyB666LJzrQwtcqVDqyHiuEIqq5nHlGAf,YBFNfEEXefJT8pHOMTtqtF8uV3Vv9F7lJTnGJHuNNQQ;561243123-tBCdvw8tW69MRypGC4xSllghnZj6YSZWFeafjx2K,21tJObGtlTNXu1IqNuSiwHSfHsh79Bfq2wGdcA7jg;561161078-MuLbZxgt0waW1w91ESJAovmN0JWM4w1xW2jqRt9j,mnNk4x9ilVEh5eKO2Te1vJobI9vQBPrNNVL32PzzAc;561319578-4uyJk7CwRkOGnCWqV5NB2ZfY6Hq1cprgp5iBdXMw,MGV8R7cZ6a89DFopd9tu9fSJWZlx53omJfJ1H9kujAY";
		String[] accounts = rawString.split(";");
		
		assertEquals(accounts.length, 10);
		
		//String twitterAccounts = (String) Main.applicationProperties.get("twitter.accounts");
		
		Stack<String[]> authenticationPool = new Stack<String[]>();
		for (int i = 0; i < accounts.length; i++) {
			String [] twitterAccount = accounts[i].split(",");
			authenticationPool.add(twitterAccount);
		}
		
		String[] taccount = authenticationPool.get(0);
		
		assertEquals(authenticationPool.size(), 10);
		assertEquals(taccount.length, 2);
		assertNotNull(taccount[0]);
		assertNotNull(taccount[1]);
	}
	
	private String deserializeString(File file)
			  throws IOException {
			      int len;
			      char[] chr = new char[4096];
			      final StringBuffer buffer = new StringBuffer();
			      final FileReader reader = new FileReader(file);
			      try {
			          while ((len = reader.read(chr)) > 0) {
			              buffer.append(chr, 0, len);
			          }
			      } finally {
			          reader.close();
			      }
			      return buffer.toString();
			  }
	
	public void testSendPostMessage(){
		try {
			
			String json = deserializeString(new File("/Users/arianpasquali/Developer/workspace/twitter-streaming-client/src/main/resources/json.test"));
			
//			my $response = $ua->post('http://localhost/twitter/services/lookup/put.php',
//					{version => $version,
//					username => $hostname, 
//					json => $utf8_string}
//					);
			
		    String url = "http://192.168.102.194/twitter/services/lookup/put.php";

		    
		    HttpClient client = new HttpClient();
		    client.getParams().setParameter("http.useragent", "Streaming Client");
		    
		    BufferedReader br = null;
		    //application/json; charset=UTF-8
		    PostMethod method = new PostMethod(url);
		    method.addParameter("streaming", "1");
		    method.addParameter("version", "2");
		    method.addParameter("username", "192.168.102.191");
		    method.addParameter("json", json);
//		    method.setRequestHeader("charset", "utf-8");
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
		          System.err.println(readLine);
		      }
		      }
		    } catch (Exception e) {
		      System.err.println(e);
		    } finally {
		      method.releaseConnection();
		      if(br != null) try { br.close(); } catch (Exception fe) {}
		    }
		    
		} catch (Exception e) {
		}
	}
}
