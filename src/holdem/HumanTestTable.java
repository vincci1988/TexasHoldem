package holdem;

import java.util.ArrayList;

import players.*;

public class HumanTestTable extends GameTable {

	public HumanTestTable(int SBAmt, int ante, String testerName, int testerStack) throws Exception {
		super(SBAmt, ante);
		human = new HumanTester(0, testerName);
		this.testerStack = testerStack;
		mount(human);
	}
	
	public void start(ArrayList<PlayerBase> AIPlayers, boolean cheat) throws Exception {
		if (AIPlayers.size() < 1 || AIPlayers.size() > 9)
			System.out.println("DEALER: Too few or too many players!");
		else {
			welcome();
			for (int i = 0; i < AIPlayers.size(); i++)
				mount(AIPlayers.get(i));
			play(cheat);
		}
	}

	// Calling Machine, Candid Statistician, Hothead Maniac, Scared Limper,
	// Unpredictable Gambler
	public void start(int cmCnt, int csCnt, int hmCnt, int slCnt, int ugCnt, boolean cheat) throws Exception {
		playerCnt = cmCnt + csCnt + hmCnt + slCnt + ugCnt + 1;
		if (playerCnt < 2 || playerCnt > 10)
			System.out.println("DEALER: Too few or too many players!");
		else {
			welcome();
			int i = 0;
			for (; i < cmCnt;)
				mount(new CallingMachine(++i));
			for (; i < cmCnt + csCnt;)
				mount(new CandidStatistician(++i));
			for (; i < cmCnt + csCnt + hmCnt;)
				mount(new HotheadManiac(++i));
			for (; i < cmCnt + csCnt + hmCnt + slCnt;)
				mount(new ScaredLimper(++i));
			for (; i < cmCnt + csCnt + hmCnt + slCnt + ugCnt;)
				mount(new UnpredictableGambler(++i));
			play(cheat);
		}
	}
	
	private void play(boolean cheat) throws Exception {
		int gameCnt = 0;
		for (; human.getMyStack() > 0 && getPlayerCnt() > 1;) {
			System.out.println("\n<BEGIN: GAME>");
			System.out.println("DEALER: GAME " + (++gameCnt));
			if (cheat)
				openGame();
			else
				game();
			System.out.println(stackReport());
			System.out.println("<END: GAME>\n");
		}
		System.out.println("DEALER: TEST TERMINATED!");
		if (human.getMyStack() == 0)
			System.out.println("DEALER: " + human.getName() + " is knocked out after " + gameCnt + " game(s)!!");
		else
			System.out.println("DEALER: " + human.getName() + " is the WINNER!!");
	}

	private boolean openGame() throws Exception {
		playerCnt = getPlayerCnt();
		if (playerCnt < 2)
			return false;
		deal();
		for(int i = 0; i < seatCnt; i++) {
			if (seats[i].active) 
				System.out.println("DEALER (whisper): " + seats[i].player.getName() + ", " + seats[i].getHoleCards() + ".");
		}
		if (preflop() || flop() || turn() || river())
			winBeforeShowdown();
		else
			showdown();
		cleanUp();
		return true;
	}

	private void welcome() {
		System.out.println("DEALER: Welcome, " + human.getName() + "!");
		System.out.print("DEALER: The table stake is $" + SBAmt + "/$" + 2 * SBAmt);
		if (ante > 0)
			System.out.print(" with $" + ante + " ante");
		System.out.println(". Good luck!\n");
	}

	public void mount(PlayerBase player) {
		player.deposit(testerStack);
		player.buyIn(this, testerStack);
	}

	private HumanTester human;
	private int testerStack;

}
