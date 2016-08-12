package tests_manual;

import java.util.Scanner;

import evolvable_players.CandidStatistician;
import holdem.PlayerBase;
import holdem.TournamentTable;

public class TournamentTableManualTest implements ManualTest {

	@Override
	public void run() throws Exception {
		System.out.println("You are about to start a manual test for class: TournamentTable.");
		System.out.println("Please specify the following attributes of the table.");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.print("SB Amount: ");
		int SBAmt = Integer.parseInt(scanner.nextLine());
		System.out.print("Ante: ");
		int ante = Integer.parseInt(scanner.nextLine());
		System.out.print("Blinds Raising Frequency (games): ");
		int blindRaiseFrequency = Integer.parseInt(scanner.nextLine());
		System.out.print("Player Count: ");
		int size = Integer.parseInt(scanner.nextLine());
		System.out.print("Buy In Amount: ");
		int buyInAmt = Integer.parseInt(scanner.nextLine());
		System.out.print("Verbose(y/n)? ");
		boolean verbose = scanner.nextLine().equals("y");
		TournamentTable tournament = new TournamentTable(SBAmt, ante, blindRaiseFrequency, size);
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < size; i++) {
				PlayerBase player = (new CandidStatistician(i));
				player.deposit(buyInAmt);
				player.buyIn(tournament, buyInAmt);
			}
			if (verbose)
				tournament.startVerbose();
			else
				tournament.start();
			System.out.println("Test Completed!");
		}
	}

}
