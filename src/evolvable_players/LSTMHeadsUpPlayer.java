package evolvable_players;

import java.io.IOException;

import LSTM.Cell;
import evolvable_players.Evolvable;
import evolvable_players.GenomeBase;
import evolvable_players.LSTMHeadsUpPlayerGenome;
import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Raise;
import holdem.Result;
import holdem.TableInfo;

public class LSTMHeadsUpPlayer extends PlayerBase implements Statistician, Evolvable {

	public LSTMHeadsUpPlayer(int id) {
		super(id);
		lstm = new Cell(inputSize);
	}
	
	public LSTMHeadsUpPlayer(int id, LSTMHeadsUpPlayerGenome childGenome) throws Exception {
		super(id);
		lstm = new Cell(childGenome.getGenes());
	}
	
	public GenomeBase getGenome() {
		return new LSTMHeadsUpPlayerGenome(lstm.getGenome());
	}
	
	public void matchStart() {
		reset();
	}
	
	public void reset() {
		lstm.reset();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		double[] input = new double[inputSize];
		double myBet = 0, myStack = 0;
		double opponentBet = 0, opponentStack = 0;
		if (info.playerInfos.size() != 2)
			throw new Exception("LSTMHeadsUPPlayer.getAction(TableInfo): playerInfo.size != 2");
		for (int i = 0; i < info.playerInfos.size(); i++) {
			if (info.playerInfos.get(i).id == id) {
				myBet = info.playerInfos.get(i).bet;
				myStack = info.playerInfos.get(i).stack;
			} else {
				opponentBet = info.playerInfos.get(i).bet;
				opponentStack = info.playerInfos.get(i).stack;
			}
		}
		double previousBet = info.potSize - opponentBet - myBet;
		double myTotal = previousBet / 2.0 + myBet + myStack;
		double opponentTotal = previousBet / 2.0 + opponentBet + opponentStack;
		input[0] = getStage(info.board.length());
		input[1] = 2 * myBet / myTotal - 1.0;
		input[2] = 2 * opponentBet / opponentTotal - 1.0;
		input[3] = 2 * evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1) - 1.0;
		input[4] = 4 * (getPotOdds(info) - 0.25);
		double opportunity = lstm.activate(input);
		if (opportunity < 0)
			return info.currentBet == getMyBet() ? new Check(this) : new Fold(this);
		int targetBet = (int) Math.round((getMyBet() + getMyStack()) * opportunity);
		if (info.currentBet >= targetBet) {
			if (info.currentBet == getMyBet())
				return new Check(this);
			if (info.currentBet < getMyBet() + getMyStack())
				return new Call(this);
			return new AllIn(this);
		}
		if (targetBet >= getMyBet() + getMyStack())
			return new AllIn(this);
		int diff = targetBet - info.currentBet - info.minRaise;
		if (diff >= 0) {
			return new Raise(this, info.currentBet + info.minRaise + (diff / info.BBAmt) * info.BBAmt);
		}
		return getMyBet() == info.currentBet ? new Check(this) : new Call(this);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void observe(Result resultInfo) {
		lstm.reset();
	}

	@Override
	public String getName() {
		return "LSTM Heads-up Player (ID = " + id + ")";
	}
	
	private double getStage(int boardLength) {
		return 2 * (boardLength / 10.0) - 1.0;
	}

	private Cell lstm;

	public static final int inputSize = 5;
}
