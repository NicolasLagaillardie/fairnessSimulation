package fairness;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class fiarnessMinersBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("fairness");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
				new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));

		int blockReward = 100;
		
		int minerCount = 5;
		for (int i = 0; i < minerCount; i++) {
			Random randomizer = new Random();
			context.add(new Miner(i, 0, space, grid, false, blockReward, 0, null, false, randomizer.nextInt(20), 10 * randomizer.nextDouble()));
		}
		
		int userCount = 100;
		for (int i = 0; i < userCount; i++) {
			Random randomizer = new Random();
			int minTransactionAmount  = randomizer.nextInt(100);
			int maxTransactionAmount = minTransactionAmount + randomizer.nextInt(100);
			double minTransactionFee = 10 * randomizer.nextDouble();
			double maxTransactionFee  = minTransactionFee + 10 * randomizer.nextDouble();
			context.add(new User(i, space, grid, blockReward, 0, minTransactionFee, maxTransactionFee, minTransactionAmount, maxTransactionAmount, randomizer.nextInt(100)));
		}

		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}

		return context;
	}

}
