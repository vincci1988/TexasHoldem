package tests_manual;

import java.util.Scanner;

import holdem.HumanTestTable;

public class HumanTableManualTest implements ManualTest {

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
		System.out.println("Please specify the number of each type of AI players you would like to play against.");
		System.out.println("Note that no more than NINE opponents, regardless of their type(s) are allowed in the test.");
		int cmCnt = 0, csCnt = 0, hmCnt = 0, slCnt = 0, ugCnt = 0;
		System.out.print("[1] Calling Machine: ");
		cmCnt = Integer.parseInt(scanner.nextLine());
		System.out.print("[2] Candid Statistician: ");
		csCnt = Integer.parseInt(scanner.nextLine());
		System.out.print("[3] Hothead Maniac: ");
		hmCnt = Integer.parseInt(scanner.nextLine());
		System.out.print("[4] Scared Limper: ");
		slCnt = Integer.parseInt(scanner.nextLine());
		System.out.print("[5] Unpredictable Gambler: ");
		ugCnt = Integer.parseInt(scanner.nextLine());
		System.out.print("Would you like to cheat (y/n): ");
		boolean cheat = scanner.nextLine().equals("y");
		System.out.println("\n");
		HumanTestTable testTable = new HumanTestTable(SBAmt, ante, name, buyIn);
		testTable.start(cmCnt, csCnt, hmCnt, slCnt, ugCnt, cheat);
	}

}
