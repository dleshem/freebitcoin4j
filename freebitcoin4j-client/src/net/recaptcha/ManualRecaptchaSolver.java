package net.recaptcha;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ManualRecaptchaSolver implements RecaptchaSolver {
	private final RecaptchaClient recaptcha;
	
	public ManualRecaptchaSolver(RecaptchaClient recaptcha) {
		this.recaptcha = recaptcha;
	}
	
	private static final InputStream nonClosableSystemIn = new InputStream() {
		@Override
		public int read(byte[] b) throws IOException {
			return System.in.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return System.in.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			return System.in.skip(n);
		}

		@Override
		public int available() throws IOException {
			return System.in.available();
		}

		@Override
		public synchronized void mark(int readlimit) {
			System.in.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			System.in.reset();
		}

		@Override
		public boolean markSupported() {
			return System.in.markSupported();
		}

		@Override
		public int read() throws IOException {
			return System.in.read();
		}
	};
	
	@Override
	public String solve(String challenge) {
		System.out.print("Solve: " + recaptcha.getImageUrl(challenge));
		
        final Scanner scanner = new Scanner(nonClosableSystemIn);
        try {
			return scanner.nextLine();
        } finally {
        	scanner.close();
        }
	}
}
