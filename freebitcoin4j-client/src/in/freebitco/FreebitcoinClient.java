package in.freebitco;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.recaptcha.RecaptchaClient;
import net.recaptcha.RecaptchaClient.Endpoint;
import net.recaptcha.CaptchaSolver;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;

public class FreebitcoinClient {
	private static final String ENDPOINT = "http://freebitco.in/";
	
	/** Extracted from http://freebitco.in/?op=home */ 
	private static final String RECAPTCHA_PUBLIC_KEY = "6LfZpugSAAAAAA_U3jfNoz21UDL59hZMsDQI37nU";
	
	private final HttpRequestFactory requestFactory;
	private final Integer connectTimeout;
	private final Integer readTimeout;
	private final CaptchaSolver captchaSolver;
	private final RecaptchaClient recaptcha;
	
	public FreebitcoinClient(HttpRequestFactory requestFactory, Integer connectTimeout, Integer readTimeout,
			CaptchaSolver captchaSolver) {
		this.requestFactory = requestFactory;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.captchaSolver = captchaSolver;
		recaptcha = new RecaptchaClient(requestFactory, connectTimeout, readTimeout, Endpoint.HTTPS);
	}
	
	public String login(String btcAddress, String password) throws IOException, FreebitcoinException {
		final String recaptchaChallenge = recaptcha.createChallenge(RECAPTCHA_PUBLIC_KEY);
		final String recaptchaResponse = captchaSolver.solve(recaptcha.getImageUrl(recaptchaChallenge));
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("op", "login");
		params.put("btc_address", btcAddress);
		params.put("password", password);
		params.put("recaptcha_challenge_field", recaptchaChallenge);
		params.put("recaptcha_response_field", recaptchaResponse);
		
		final HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(ENDPOINT), new UrlEncodedContent(params));
		if (connectTimeout != null) {
			request.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != null) {
			request.setConnectTimeout(readTimeout);
		}
		
		final HttpResponse response = request.execute();
		try {
			final String responseText = readFully(response.getContent());
			final String[] parts = responseText.split(":");
			if ((parts.length != 3) || (!"s1".equals(parts[0]))) {
				throw new FreebitcoinException(responseText);
			}
			
			return parts[2];
		} finally {
			response.ignore();
		}
	}
	
	private static final Random rand = new Random();
	
	/** @see https://github.com/carlo/jquery-browser-fingerprint */
	private static String randomFingerprint() {
		final StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < 32; ++i) {
			builder.append(Integer.toString(rand.nextInt(16), 16));
		}
		
		return builder.toString();
	}
	
	public FreePlayResult freePlay(String btcAddress, String token) throws IOException, FreebitcoinException {
		final String recaptchaChallenge = recaptcha.createChallenge(RECAPTCHA_PUBLIC_KEY);
		final String recaptchaResponse = captchaSolver.solve(recaptcha.getImageUrl(recaptchaChallenge));
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("op", "free_play");
		params.put("recaptcha_challenge_field", recaptchaChallenge);
		params.put("recaptcha_response_field", recaptchaResponse);
		params.put("fingerprint", randomFingerprint());
		
		final HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(ENDPOINT), new UrlEncodedContent(params));
		if (connectTimeout != null) {
			request.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != null) {
			request.setConnectTimeout(readTimeout);
		}
		
		request.getHeaders().setCookie("btc_address=" + btcAddress + "; password=" + token);
				
		final HttpResponse response = request.execute();
		try {
			final String responseText = readFully(response.getContent());
			final String[] parts = responseText.split(":");
			if ((parts.length < 5) || (!"s1".equals(parts[0]))) {
				throw new FreebitcoinException(responseText);
			}
			
			return new FreePlayResult(Integer.parseInt(parts[1]), Double.parseDouble(parts[2]),
					Double.parseDouble(parts[3]), Long.parseLong(parts[4]));
		} finally {
			response.ignore();
		}
	}
	
	private static String readFully(InputStream is) throws IOException {
		final byte[] buffer = new byte[1024*64];
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		int numRead;
		while ((numRead = is.read(buffer)) != -1) {
			baos.write(buffer,  0,  numRead);
		}
		
		return new String(baos.toByteArray(), "UTF-8");
	}
}
