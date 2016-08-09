package evolution;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;

import holdem.PlayerBase;
import holdem.TournamentTable;
import players.CandidStatistician;

public class CandidStatisticianEvolution extends EvolutionBase {

	public CandidStatisticianEvolution() throws FileNotFoundException {
		super();
		for (int i = 0; i < populationSize; i++) {
			double baseRateHeadsUp = 0.25 + 0.5 * random.nextDouble();
			double baseRateFullTable = baseRateHeadsUp + (maxRateHeadsUp - baseRateHeadsUp) * random.nextDouble();
			double conservativeness = 1.0 + 4.0 * random.nextDouble();
			population
					.add(new Agent(new CandidStatistician(id++, conservativeness, baseRateFullTable, baseRateHeadsUp)));
		}

	}

	@Override
	public void run() throws Exception {
		PrintWriter log = new PrintWriter(logPath);
		for (int i = 0; i < maxGenCnt; i++) {
			System.out.println("<BEGIN GENERATION " + (i + 1) + ">");
			log.println("<BEGIN GENERATION " + (i + 1) + ">");
			select();
			String report = report();
			System.out.print(report);
			log.print(report);
			reproduce();
			System.out.println("<END GENERATION " + (i + 1) + ">");
			log.println("<END GENERATION " + (i + 1) + ">\n");
		}
		log.close();
	}

	public String report() {
		String res = new String();
		for (int i = 0; i < population.size(); i++) {
			CandidStatistician player = (CandidStatistician) population.get(i).player;
			res += "[" + (i + 1) + "] " + player.getName() + ": fitness = " + population.get(i).fitness + ", csvt = "
					+ player.conservativeness + ", brft = " + player.baseRateFullTable + ", brhu = "
					+ player.baseRateHeadsUp + "\n";
		}
		return res;
	}

	@Override
	void select() throws Exception {
		for (int round = 0; round < roundsPerGen; round++) {
			Collections.shuffle(population);
			for (int j = 0; j < populationSize / tableSize; j++) {
				TournamentTable table = new TournamentTable(SBAmt, ante, blindRaisingFrequency, tableSize);
				for (int k = 0; k < tableSize; k++) {
					PlayerBase player = population.get(j * tableSize + k).player;
					player.deposit(buyInAmt);
					player.buyIn(table, buyInAmt);
				}
				table.start();
				for (int k = 0; k < tableSize; k++) {
					int index = j * tableSize + k;
					population.get(index).fitness += Math.pow(2.0,
							10 - table.performances.get(population.get(index).player));
				}
			}
		}
		Collections.sort(population);
		int survivorCnt = ((int) (populationSize * survivalRate));
		population.subList(survivorCnt, populationSize).clear();
	}

	@Override
	void reproduce() {
		int survivorCnt = ((int) (populationSize * survivalRate));
		for (int i = 0; i < population.size(); i++)
			population.get(i).fitness = 0.0;
		while (population.size() != populationSize) {
			CandidStatistician mom = (CandidStatistician) population.get(random.nextInt(survivorCnt)).player;
			CandidStatistician dad = null;
			while ((dad = (CandidStatistician) population.get(random.nextInt(survivorCnt)).player) == mom)
				;
			CandidStatistician child = new CandidStatistician(id++, mom.conservativeness, dad.baseRateFullTable,
					mom.baseRateHeadsUp);
			mutate(child);
			population.add(new Agent(child));
		}
	}

	private void mutate(CandidStatistician player) {
		player.conservativeness += 0.25 * random.nextGaussian();
		if (player.conservativeness < 1.0)
			player.conservativeness = 1.0;
		player.baseRateHeadsUp += 0.02 * random.nextGaussian();
		if (player.baseRateHeadsUp < 0.25)
			player.baseRateHeadsUp = 0.25;
		player.baseRateFullTable += 0.02 * random.nextGaussian();
		if (player.baseRateFullTable > 0.8)
			player.baseRateFullTable = 0.8;
		if (player.baseRateFullTable < player.baseRateHeadsUp) {
			double temp = player.baseRateFullTable;
			player.baseRateFullTable = player.baseRateHeadsUp;
			player.baseRateHeadsUp = temp;
		}
	}

	static final int populationSize = 50;
	static final int maxGenCnt = 20;
	static final int SBAmt = 10;
	static final int ante = 0;
	static final int blindRaisingFrequency = 10;
	static final int tableSize = 10;
	static final int buyInAmt = 5000;
	static final int roundsPerGen = 10;
	static final double maxRateHeadsUp = 0.8;
	static final double survivalRate = 0.2;
	static final String logPath = "CandidStatisticianEvolutionLog.txt";
	static int id = 0;
}
