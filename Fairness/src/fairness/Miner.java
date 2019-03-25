/**
 * 
 */
package fairness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * @author Lagai
 *
 */
public class Miner {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int creationTime;
	private double minTransactionFee;
	private double startTime;

	public long ID, numberOfBlocks;
	public List<Transaction> transactionsToValidate;
	public double blockReward, gain;
	public boolean available, chosen;

	public Miner(long ID, long numberOfBlocks, ContinuousSpace<Object> space, Grid<Object> grid, boolean chosen, double blockReward, double gain,
			List<Transaction> transactionsToValidate, boolean available, int creationTime, double minTransactionFee) {
		this.ID = ID;
		this.numberOfBlocks = numberOfBlocks;
		this.space = space;
		this.grid = grid;
		this.creationTime = creationTime;
		this.blockReward = blockReward;
		this.gain = gain;
		this.minTransactionFee = minTransactionFee;
		this.available = available;
		this.transactionsToValidate = transactionsToValidate;
		this.chosen = chosen;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void main() {

		if (chosen) {

			startTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

			available = false;

			// Select all users
			GridPoint pt = grid.getLocation(this);
			
			for (Transaction transaction : transactionsToValidate) {
				if (!transaction.validated && transaction.fee >= minTransactionFee) {
					transaction.validated = true;
					gain += transaction.fee;
					transactionsToValidate.remove(transaction);

					// Update the public database
					for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
						if (obj instanceof User) {
							User user = (User) obj;
							user.transactionsToValidate.remove(transaction);
						}
						if (obj instanceof Miner) {
							Miner miner = (Miner) obj;
							miner.transactionsToValidate.remove(transaction);
						}
					}
				}
			}
			
			gain += blockReward;

			chosen = false;
		}
		
		if (!available) {
			if(RunEnvironment.getInstance().getCurrentSchedule().getTickCount() - startTime > creationTime) {
				available = true;
				updateBlockReward();
			}
		}
		
		if(!chosen && available) {
			if(RunEnvironment.getInstance().getCurrentSchedule().getTickCount() % 2 == 0) {
				POW();
			}
		}
			
	}
	
	public void POW() {

		GridPoint pt = grid.getLocation(this);

		// Update the public database
		List<Object> miners = new ArrayList<Object>();
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Miner) {
				Miner miner = (Miner) obj;
				if(miner.available) {
					miners.add(miner);
				}
			}
		}

		Random randomizer = new Random();
		Miner chosenOne = (Miner) miners.get(randomizer.nextInt(miners.size()));
		chosenOne.chosen = true;
	}
	
	public void updateBlockReward() {

		GridPoint pt = grid.getLocation(this);
		
		// Update the public database
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Miner) {
				Miner miner = (Miner) obj;
				miner.numberOfBlocks += 1;
			}
		}

		if(this.numberOfBlocks % 50 == 0) {
		
			// Update the public database
			for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
				if (obj instanceof Miner) {
					Miner miner = (Miner) obj;
					miner.numberOfBlocks += 1;
					miner.blockReward = miner.blockReward / 2;
				}
				if (obj instanceof User) {
					User user = (User) obj;
					user.blockReward = user.blockReward / 2;
				}
			}
		}
	}

}
