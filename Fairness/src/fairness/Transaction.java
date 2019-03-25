package fairness;

public class Transaction {

	public boolean validated;
	public User emitter, receiver;
	public double tickCount, amount, fee;

	public Transaction(boolean validated, double tickCount, User emitter, User receiver, double amount, double fee) {
		this.validated = validated;
		this.emitter = emitter;
		this.receiver = receiver;
		this.tickCount = tickCount;
		this.amount = amount;
		this.fee= fee; 
	}

}
