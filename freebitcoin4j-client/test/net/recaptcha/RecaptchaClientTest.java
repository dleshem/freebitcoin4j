package net.recaptcha;

import static org.junit.Assert.*;
import net.recaptcha.RecaptchaClient.Endpoint;

import org.junit.Test;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class RecaptchaClientTest {
	private static final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
	private static final RecaptchaClient recaptcha = new RecaptchaClient(requestFactory, 5000, 5000, Endpoint.HTTPS);

	/** Extracted from http://freebitco.in/?op=home */ 
	private static final String RECAPTCHA_PUBLIC_KEY = "6LfZpugSAAAAAA_U3jfNoz21UDL59hZMsDQI37nU";
	
	@Test
	public void test() throws Exception {
		final String challenge = recaptcha.createChallenge(RECAPTCHA_PUBLIC_KEY);
		assertNotNull(challenge);
		
		final String imageUrl = recaptcha.getImageUrl(challenge);
		assertNotNull(imageUrl);
	}
}
