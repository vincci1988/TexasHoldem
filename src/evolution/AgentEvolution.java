package evolution;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import LSTM.Cell;
import advanced_players.Shaco;
import ashe_rulebased.Ashe_RB;
import ASHE.Ashe;
import ASHE.AsheGenome;
import evolvable_players.*;
import experiments.NLHeadsUpEvaluation;
import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import simple_players.*;

@SuppressWarnings("unused")
public class AgentEvolution extends EvolutionBase {

	public AgentEvolution() throws Exception {
		super();
		opponents = new PlayerBase[3];
		//opponents[0] = new CandidStatistician(-1);
		//opponents[2] = new HotheadManiac(-3);
		//opponents[3] = new CallingMachine(-4);
		opponents[0] = new Shaco(-1); //Ashe_RB(-1);
		opponents[1] = new HotheadManiac(-2);
		opponents[2] = new ScaredLimper(-3);
		avgSurvivorFitness = 0;
		std = 0;
		mutationRate = initialMutationRate;
		mutationStrength = initialMutationStrength;
		for (int i = 0; i < populationSize; i++)
			population.add(new Agent(new Ashe(id++)));//, "AsheGenome_Gen10.txt")));
	}

	@Override
	public void run() throws Exception {
		PrintWriter log = new PrintWriter(logPath);
		for (int i = 0; i < maxGenCnt; i++) {
			mutationRate = initialMutationRate + (finalMutationRate - initialMutationRate) * i / maxGenCnt;
			mutationStrength = initialMutationStrength
					+ (finalMutationStrength - initialMutationStrength) * i / maxGenCnt;
			System.out.println("<BEGIN GENERATION " + (i + 1) + ">");
			log.println("<BEGIN GENERATION " + (i + 1) + ">");
			select();
			String report = report();
			System.out.print(report);
			log.print(report);
			reproduce();
			System.out.println("<END GENERATION " + (i + 1) + ">");
			log.println("<END GENERATION " + (i + 1) + ">\n");
			if ((i + 1) % 10 == 0) {
				Ashe champion = (Ashe) population.get(population.size() - 1).player;
				((AsheGenome) champion.getGenome()).writeToFile("AsheGenome_Gen" + (i + 1) + ".txt");
			}
			
		}
		log.close();
		System.out.println();
		System.out.println("<BEGIN: CHAMPION EVALUATION>");
		Ashe champion = (Ashe) population.get(population.size() - 1).player;
		((AsheGenome) champion.getGenome()).writeToFile("AsheGenome.txt");
		NLHeadsUpEvaluation eval = new NLHeadsUpEvaluation(champion, opponents, champDeckCnt,
				"pfm_ashe", "glog_ashe");
		eval.run();
		System.out.println("<END: CHAMPION EVALUATION>");
	}

	public String report() {
		String res = new String();
		avgSurvivorFitness = 0;
		std = 0;
		for (int i = 0; i < population.size(); i++) {
			Ashe player = (Ashe) population.get(i).player;
			res += "[" + (i + 1) + "] " + player.getName() + ": fitness = " + population.get(i).fitness
					+ ", stats = { ";
			//res += "CS = " + population.get(i).stats[0] + " / " + maxStats[0] + ", ";
			res += "SL = " + (population.get(i).stats[2] - 750) + " / " + maxStats[2] + ", ";
			//res += "HM = " + population.get(i).stats[2] + " / " + maxStats[2] + ", ";
			//res += "CM = " + population.get(i).stats[3] + " / " + maxStats[3] + ", ";
			res += "HM = " + population.get(i).stats[1] + " / " + maxStats[1] + ", ";
			res += "SH = " + population.get(i).stats[0] + " / " + maxStats[0] + " }\n";
			avgSurvivorFitness += population.get(i).fitness;
			std += population.get(i).fitness * population.get(i).fitness;
		}
		avgSurvivorFitness /= population.size();
		std = Math.sqrt(std / population.size() - Math.pow(avgSurvivorFitness, 2));
		res += "Average Survivior Fitness = " + avgSurvivorFitness + "\n";
		return res;
	}

	@Override
	void select() throws Exception {
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < opponents.length; j++) {
				NLHeadsUpTable headsUpTable = new NLHeadsUpTable(population.get(i).player, opponents[j], SBAmt,
						buyInAmt, maxDeckCnt);
				double[] performances = headsUpTable.start();
				population.get(i).stats[j] = performances[0];
				if (population.get(i).stats[j] > maxStats[j] && !(opponents[j] instanceof ScaredLimper))
					maxStats[j] = population.get(i).stats[j];
			}
			population.get(i).fitness = 0;

			for (int j = 0; j < opponents.length; j++) {
				if (opponents[j] instanceof ScaredLimper)
					population.get(i).fitness += (population.get(i).stats[j] - 750) / maxStats[j];
				else
					population.get(i).fitness += population.get(i).stats[j] / maxStats[j];
			}
			population.get(i).fitness /= opponents.length;
			System.out.println(population.get(i).player.getName() + ": " + population.get(i).fitness);
		}
		Collections.sort(population);
		int survivorCnt = ((int) (populationSize * survivalRate));
		population.subList(survivorCnt, populationSize).clear();
	}

	@Override
	void reproduce() throws Exception {
		int survivorCnt = population.size();
		int elitePoolSize = 0;
		for (int i = 0; i < population.size(); i++) {
			if (population.get(i).fitness >= avgSurvivorFitness)
				elitePoolSize++;
			else {
				AsheGenome survivorGenome = (AsheGenome) ((Ashe) population
						.get(i).player).getGenome();
				survivorGenome.mutate(mutationRate, mutationStrength);
				population.get(i).player = new Ashe(population.get(i).player.getID(), survivorGenome);
			}
			population.get(i).fitness = 0.0;
			for (int j = 0; j < Agent.opponentCnt; j++)
				population.get(i).stats[j] = 0.0;
		}
		for (int i = 0; population.size() != populationSize; i++) {
			Ashe mom = (Ashe) population.get(i % elitePoolSize).player;
			Ashe dad = (Ashe) population.get(random.nextInt(elitePoolSize)).player;
			AsheGenome dadGenome = (AsheGenome) dad.getGenome();
			AsheGenome momGenome = (AsheGenome) mom.getGenome();
			AsheGenome childGenome = (AsheGenome) momGenome.crossOver(dadGenome);
			childGenome.mutate(mutationRate, mutationStrength);
			Ashe child = new Ashe(id++, childGenome);
			population.add(new Agent(child));
		}
		Collections.reverse(population);
	}

	PlayerBase[] opponents;
	double avgSurvivorFitness;
	double std;
	double mutationRate;
	double mutationStrength;
	double[] maxStats = { 2000, 40000, 250, 40000, 1000 };

	static final int populationSize = 20;
	static final int maxGenCnt = 2;
	static final int maxDeckCnt = 5;
	static final int champDeckCnt = 1500;
	static final int SBAmt = 50;
	static final int buyInAmt = 20000;
	static final double survivalRate = 0.5;
	static final double initialMutationRate = 0.1;
	static final double finalMutationRate = 0.05;
	static final double initialMutationStrength = 0.25;
	static final double finalMutationStrength = 0.1;
	static final String logPath = "AsheEvolutionLog.txt";
	static int id = 0;
}
