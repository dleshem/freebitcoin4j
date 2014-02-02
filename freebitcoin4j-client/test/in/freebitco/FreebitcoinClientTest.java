package in.freebitco;

import static org.junit.Assert.*;
import net.recaptcha.ManualCaptchaSolver;
import net.recaptcha.CaptchaSolver;

import org.junit.Test;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

public class FreebitcoinClientTest {
	private static final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
	
	// TODO: change to real values
	private static final String BITCOIN_ADDRESS = "XXX";
	private static final String PASSWORD = "XXX";
	private static final String TOKEN = "XXX";
	
	private static final CaptchaSolver wrongRecaptchaSolver = new CaptchaSolver() {
		@Override
		public String solve(String imageUrl) {
			return "123 456";
		}
	};
	private static final CaptchaSolver manualRecaptchaSolver = new ManualCaptchaSolver();
	
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
