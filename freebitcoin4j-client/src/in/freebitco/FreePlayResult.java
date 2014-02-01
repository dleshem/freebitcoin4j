package in.freebitco;

public class FreePlayResult {
	public final int roll;
	public final double balance;
	public final double payout;
	public final long timestamp;
	
	public FreePlayResult(int roll, double balance, double payout, long timestamp) {
		this.roll = roll;
		this.balance = balance;
		this.payout = payout;
		this.timestamp = timestamp;
	}
}
