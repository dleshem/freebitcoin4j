package in.freebitco;

import static org.junit.Assert.*;
import net.recaptcha.ManualRecaptchaSolver;
import net.recaptcha.RecaptchaClient;
import net.recaptcha.RecaptchaSolver;
import net.recaptcha.RecaptchaClient.Endpoint;

import org.junit.Test;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class FreebitcoinClientTest {
	private static final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
	private static final RecaptchaClient recaptcha = new RecaptchaClient(requestFactory, 5000, 5000, Endpoint.HTTPS);
	
	// TODO: change to real values
	private static final String BITCOIN_ADDRESS = "XXX";
	private static final String PASSWORD = "XXX";
	private static final String TOKEN = "XXX";
	
	private static final RecaptchaSolver wrongRecaptchaSolver = new RecaptchaSolver() {
		@Override
		public String solve(String challenge) {
			return "123 456";
		}
	};
	private static final RecaptchaSolver manualRecaptchaSolver = new ManualRecaptchaSolver(recaptcha);
	
	@Test
	public void testLoginWrongCaptcha() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, wrongRecaptchaSolver);
		try {
			freebitcoin.login(BITCOIN_ADDRESS, PASSWORD);
			fail("Expected exception.");
		} catch (FreebitcoinException e) {
			// Expected exception
			assertEquals("e4", e.getMessage());
		}
	}

	@Test
	public void testLoginWrongPassword() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, manualRecaptchaSolver);
		try {
			freebitcoin.login(BITCOIN_ADDRESS, PASSWORD + "e");
			fail("Expected exception.");
		} catch (FreebitcoinException e) {
			// Expected exception
			assertEquals("e3", e.getMessage());
		}
	}

	@Test
	public void testLoginSuccess() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, manualRecaptchaSolver);
		freebitcoin.login(BITCOIN_ADDRESS, PASSWORD);
	}
	
	@Test
	public void testFreePlayWrongCaptcha() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, wrongRecaptchaSolver);
		try {
			freebitcoin.freePlay(BITCOIN_ADDRESS, TOKEN);
			fail("Expected exception.");
		} catch (FreebitcoinException e) {
			// Expected exception
			assertEquals("e2", e.getMessage());
		}
	}
	
	@Test
	public void testFreePlayWrongToken() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, wrongRecaptchaSolver);
		try {
			freebitcoin.freePlay(BITCOIN_ADDRESS, TOKEN + "e");
			fail("Expected exception.");
		} catch (FreebitcoinException e) {
			// Expected exception
			assertEquals("e4", e.getMessage());
		}
	}
	
	@Test
	public void testFreeSuccess() throws Exception {
		final FreebitcoinClient freebitcoin = new FreebitcoinClient(requestFactory, 5000, 5000, manualRecaptchaSolver);
		try {
			freebitcoin.freePlay(BITCOIN_ADDRESS, TOKEN);
		} catch (FreebitcoinException e) {
			// "e3:2918" means "must wait 2918 more seconds"
			if (!e.getMessage().startsWith("e3:")) {
				throw e;
			}
		}
	}
}
