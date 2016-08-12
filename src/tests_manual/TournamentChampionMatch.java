package tests_manual;

import java.util.ArrayList;
import java.util.Scanner;

import evolvable_players.CandidStatistician;
import evolvable_players.CandidStatisticianGenome;
import holdem.HumanTestTable;
import holdem.PlayerBase;

public class TournamentChampionMatch implements ManualTest {

	@Override
	public void run() throws Exception {
		System.out.print("You are about to start a manual test for class: HumanTestTable.\nPlease enter your name: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String name = scanner.nextLine();
		System.out.print("What is the stake of the table (SB Amt)? ");
		int SBAmt = Integer.parseInt(scanner.nextLine());
		System.out.print("What is the ante of the table? ");
		int ante = Integer.parseInt(scanner.nextLine());
		System.out.print("How much would you like to buy in? ");
		int buyIn = Integer.parseInt(scanner.nextLine());
		System.out.print("Would you like to cheat (y/n): ");
		boolean cheat = scanner.nextLine().equals("y");
		System.out.println("\n");
		HumanTestTable testTable = new HumanTestTable(SBAmt, ante, name, buyIn);
		ArrayList<PlayerBase> players = new ArrayList<PlayerBase>();
		players.add(new evolvable_players.CandidStatistician(1, new CandidStatisticianGenome(1.554, 0.7157, 0.5420)));
		players.add(new CandidStatistician(2, new CandidStatisticianGenome(1.4378, 0.7402, 0.5731)));
		players.add(new CandidStatistician(3, new CandidStatisticianGenome(1.2670, 0.7389, 0.5722)));
		players.add(new CandidStatistician(4, new CandidStatisticianGenome(1.1304, 0.7984, 0.6008)));
		testTable.start(players, cheat);
	}

}
