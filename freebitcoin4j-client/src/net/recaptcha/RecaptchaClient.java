package net.recaptcha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

public class RecaptchaClient {
	private final HttpRequestFactory requestFactory;
	private final Integer connectTimeout;
	private final Integer readTimeout;
	private final Endpoint endpoint;
	
	public static enum Endpoint {
		HTTP("http://www.google.com/recaptcha/api/"),
		HTTPS("https://www.google.com/recaptcha/api/");
		
		public final String url;
		
		private Endpoint(String url) {
			this.url = url;
		}
	}
	
	public RecaptchaClient(HttpRequestFactory requestFactory, Integer connectTimeout, Integer readTimeout, Endpoint endpoint) {
		this.requestFactory = requestFactory;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.endpoint = endpoint;
	}
	
	private static final Pattern challengePattern = Pattern.compile("^.*challenge\\s*:\\s*'(.+)'.*$");
	
	public String createChallenge(String publicKey) throws IOException {
		final HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(
				endpoint.url + "challenge?k=" + encode(publicKey)));
		if (connectTimeout != null) {
			request.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != null) {
			request.setConnectTimeout(readTimeout);
		}
		
		final HttpResponse response = request.execute();
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
			try {
				String line;
				while ((line = br.readLine()) != null) {
					final Matcher matcher = challengePattern.matcher(line);
					if (!matcher.matches()) {
						continue;
					}
					
					return matcher.group(1);
				}
				
				return null; // TODO: throw exception
			} finally {
				br.close();
			}
		} finally {
			response.ignore();
		}
	}
	
	public String getImageUrl(String challenge) {
		return endpoint.url + "image?c=" + encode(challenge);
	}
	
	private static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
