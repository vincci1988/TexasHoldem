package holdem;

public class TournamentTable extends Table {

	public TournamentTable(int SBAmt, int ante, int blindRaisingFrequency, int size) throws Exception {
		super(SBAmt, ante);
		initialSB = SBAmt;
		initialAnte = ante;
		this.blindRaisingFrequency = blindRaisingFrequency;
		this.size = size;
		gameCnt = 0;
	}

	public PlayerBase start() throws Exception {
		return start(false);
	}

	public PlayerBase startVerbose() throws Exception {
		PlayerBase winner = start(true);
		System.out.println("<BEGIN: TOURNAMENT REPORT>");
		System.out.println(winner.getName() + " wins the table after " + gameCnt + " games!");
		System.out.println("<END: TOURNAMENT REPORT>");
		return winner;
	}

	private PlayerBase start(boolean verbose) throws Exception {
		if (getPlayerCnt() != size) {
			System.out.println("TournamentTable.start: Too few or too many player(s)");
			return null;
		}
		SBAmt = initialSB;
		BBAmt = 2 * SBAmt;
		ante = initialAnte;
		gameCnt = 0;
		while (getPlayerCnt() > 1) {
			if (verbose) {
				System.out.println("<BEGIN: GAME>");
				System.out.println("<BEGIN: GAME INFO>\nGame[" + (++gameCnt) + "]: BB = $" + BBAmt + ", ante = $" + ante
						+ "\n<END: GAME INFO>");
			}
			game();
			if (verbose) {
				System.out.println(stackReport());
				System.out.println("<END: GAME>\n");
			}
			if (gameCnt % blindRaisingFrequency == 0) {
				SBAmt *= 2;
				BBAmt *= 2;
				ante *= 2;
			}
		}
		PlayerBase winner = null;
		for (int i = 0; i < seatCnt; i++) {
			if (seats[i].player != null) {
				winner = seats[i].player;
				break;
			}
		}
		return winner;
	}

	final int initialSB;
	final int initialAnte;
	final int blindRaisingFrequency;
	int size;
	int gameCnt;
}
