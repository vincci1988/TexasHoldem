package evolution;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;

import evolvable_players.CandidStatistician;
import evolvable_players.CandidStatisticianGenome;
import holdem.PlayerBase;
import holdem.TournamentTable;

public class CandidStatisticianEvolution extends EvolutionBase {

	public CandidStatisticianEvolution() throws FileNotFoundException {
		super();
		for (int i = 0; i < populationSize; i++) {
			double baseRateHeadsUp = 0.25 + 0.5 * random.nextDouble();
			double baseRateFullTable = baseRateHeadsUp + (maxRateHeadsUp - baseRateHeadsUp) * random.nextDouble();
			double conservativeness = 1.0 + 4.0 * random.nextDouble();
			population.add(new Agent(new CandidStatistician(id++,
					new CandidStatisticianGenome(conservativeness, baseRateFullTable, baseRateHeadsUp))));
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
		((CandidStatistician) population.get(0).player).getGenome().writeToFile("CandidStatisticianChampionGenome.txt");
	}

	public String report() {
		String res = new String();
		for (int i = 0; i < population.size(); i++) {
			CandidStatistician player = (CandidStatistician) population.get(i).player;
			res += "[" + (i + 1) + "] " + player.getName() + ": fitness = " + population.get(i).fitness + ", csvt = "
					+ player.getConservativeness() + ", brft = " + player.getBaseRateFullTable() + ", brhu = "
					+ player.getBaseRateHeadsUp() + "\n";
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
			CandidStatisticianGenome childGenome = (CandidStatisticianGenome) mom.getGenome()
					.crossOver(dad.getGenome());
			childGenome.mutate(0, 0);
			CandidStatistician child = new CandidStatistician(id++, childGenome);
			population.add(new Agent(child));
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
