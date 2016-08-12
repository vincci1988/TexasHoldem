package evolution;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import evolvable_players.*;
import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import simple_players.*;

public class LSTMHeadsUpPlayerEvolution extends EvolutionBase {

	public LSTMHeadsUpPlayerEvolution() throws IOException {
		super();
		// opponent = new CandidStatistician(0, new CandidStatisticianGenome("CandidStatisticianChampionGenome.txt"));
		// opponent = new ScaredLimper(0);
		 opponent = new HotheadManiac(0);
		avgSurvivorFitness = 0;
		for (int i = 0; i < populationSize; i++)
			population.add(new Agent(new LSTMHeadsUpPlayer(id++)));
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
		LSTMHeadsUpPlayer champion = (LSTMHeadsUpPlayer) population.get(0).player;
		((LSTMHeadsUpPlayerGenome)champion.getGenome()).writeToFile("LSTMHeadsUpChampionGenome.txt");
		NLHeadsUpTable headsUpTable = new NLHeadsUpTable(champion, opponent, SBAmt, buyInAmt, champDeckCnt);
		System.out.println(
				"Champion fitness = " + headsUpTable.start("NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt"));
	}

	public String report() {
		String res = new String();
		avgSurvivorFitness = 0;
		for (int i = 0; i < population.size(); i++) {
			LSTMHeadsUpPlayer player = (LSTMHeadsUpPlayer) population.get(i).player;
			res += "[" + (i + 1) + "] " + player.getName() + ": fitness = " + population.get(i).fitness + "\n";
			avgSurvivorFitness += population.get(i).fitness;
		}
		avgSurvivorFitness /= population.size();
		res += "Average Survivior Fitness = " + avgSurvivorFitness + "\n";
		return res;
	}

	@Override
	void select() throws Exception {
		for (int i = 0; i < populationSize; i++) {
			//System.out.println("Evaluating agent[" + i + "]...");
			NLHeadsUpTable headsUpTable = new NLHeadsUpTable(population.get(i).player, opponent, SBAmt, buyInAmt,
					maxDeckCnt);
			population.get(i).fitness = headsUpTable.start();
			//System.out.println("Evaluation of agent[" + i + "] completed: fitness = " + population.get(i).fitness);
		}
		Collections.sort(population);
		int survivorCnt = ((int) (populationSize * survivalRate));
		population.subList(survivorCnt, populationSize).clear();
	}

	@Override
	void reproduce() throws Exception {
		int survivorCnt = ((int) (populationSize * survivalRate));
		for (int i = 0; i < population.size(); i++)
			population.get(i).fitness = 0.0;
		for (int i = 0; population.size() != populationSize; i++) {
			LSTMHeadsUpPlayer mom = (LSTMHeadsUpPlayer) population.get(i).player;
			LSTMHeadsUpPlayer dad = null;
			while ((dad = (LSTMHeadsUpPlayer) population.get(random.nextInt(survivorCnt)).player) == mom)
				;
			LSTMHeadsUpPlayerGenome dadGenome = (LSTMHeadsUpPlayerGenome)dad.getGenome();
			LSTMHeadsUpPlayerGenome momGenome = (LSTMHeadsUpPlayerGenome)mom.getGenome();
			LSTMHeadsUpPlayerGenome childGenome = (LSTMHeadsUpPlayerGenome) dadGenome.crossOver(momGenome);
			childGenome.mutate(mutationRate, mutationStrength);
			LSTMHeadsUpPlayer child = new LSTMHeadsUpPlayer(id++, childGenome);
			population.add(new Agent(child));
		}
	}

	PlayerBase opponent;
	double avgSurvivorFitness;

	static final int populationSize = 20;
	static final int maxGenCnt = 20;
	static final int maxDeckCnt = 500;
	static final int champDeckCnt = 1500;
	static final int SBAmt = 50;
	static final int buyInAmt = 20000;
	static final double survivalRate = 0.5;
	static final double mutationRate = 0.1;
	static final double mutationStrength = 0.5;
	static final String logPath = "LSTMHeadsUpPlayerEvolutionLog.txt";
	static int id = 0;
}
