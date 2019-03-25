/**
 * 
 */
package fairness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import fairness.Transaction;

/**
 * @author Lagai
 *
 */
public class User {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private double minTransactionFee, maxTransactionFee, minTransactionAmount, maxTransactionAmount;
	private int transactionWill; // Percentage of luck to release a transaction

	public long ID;
	public double blockReward, gain;
	public List<Transaction> transactionsToValidate;

	public User(long ID, ContinuousSpace<Object> space, Grid<Object> grid, double blockReward, double gain,
			double minTransactionFee, double maxTransactionFee, double minTransactionAmount,
			double maxTransactionAmount, int transactionWill) {
		this.ID = ID;
		this.space = space;
		this.grid = grid;
		this.blockReward = blockReward;
		this.gain = gain;
		this.minTransactionFee = minTransactionFee;
		this.maxTransactionFee = maxTransactionFee;
		this.minTransactionAmount = minTransactionAmount;
		this.maxTransactionAmount = maxTransactionAmount;
		this.transactionWill = transactionWill;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void main() {
		int releaseTransaction = ThreadLocalRandom.current().nextInt(0, 100);
		
		if(releaseTransaction > transactionWill) {

			// Create a transaction amount and fee
			double transactionAmount = ThreadLocalRandom.current().nextDouble(minTransactionAmount, maxTransactionAmount + 1);
			double transactionFee = ThreadLocalRandom.current().nextDouble(minTransactionFee, maxTransactionFee + 1);
			double timeStamp = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

			// Select all users
			GridPoint pt = grid.getLocation(this);
			List<Object> users = new ArrayList<Object>();
			for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
				if (obj instanceof User) {
					users.add(obj);
				}
			}

			// Select a random user
			Random randomizer = new Random();
			User receiver = (User) users.get(randomizer.nextInt(users.size()));
			
			// Create a new transaction
			Transaction transaction = new Transaction(false, timeStamp, this, receiver, transactionAmount, transactionFee);

			// Update the public database
			for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
				if (obj instanceof User) {
					User user = (User) obj;
					user.transactionsToValidate.add(transaction);
				}
				if (obj instanceof Miner) {
					Miner miner = (Miner) obj;
					miner.transactionsToValidate.add(transaction);
				}
			}
		}
	}

}
