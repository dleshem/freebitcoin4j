package in.freebitco;

public class FreebitcoinException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FreebitcoinException(String message) {
        super(message);
    }

    public FreebitcoinException(String message, Throwable cause) {
    	super(message, cause);
    }
}
