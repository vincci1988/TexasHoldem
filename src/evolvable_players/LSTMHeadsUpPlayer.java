package evolvable_players;

import java.io.IOException;

import LSTM.Module;
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
		lstm = new Module(inputSize);
	}
	
	public LSTMHeadsUpPlayer(int id, LSTMHeadsUpPlayerGenome childGenome) throws Exception {
		super(id);
		lstm = new Module(childGenome.getGenes());
	}
	
	public GenomeBase getGenome() {
		return new LSTMHeadsUpPlayerGenome(lstm.getGenome());
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		/**
		 * input: preflop, flop, turn, river, selfCommitment,
		 * opponentCommitment, handStrength
		 */
		double[] input = new double[inputSize];
		for (int i = 0; i < input.length; i++)
			input[i] = 0.0;
		if (info.board.length() == 0)
			input[0] = 1.0;
		else if (info.board.length() == 6)
			input[1] = 1.0;
		else if (info.board.length() == 8)
			input[2] = 1.0;
		else
			input[3] = 1.0;
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
		input[4] = previousBet / 2.0 + myBet;
		input[4] /= (input[4] + myStack);
		input[5] = previousBet / 2.0 + opponentBet;
		input[5] /= (input[5] + opponentStack);
		input[6] = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		double handStrength = lstm.activate(input);
		double baseStrength = 0.5;
		if (handStrength < baseStrength)
			return info.currentBet == getMyBet() ? new Check(this) : new Fold(this);
		int targetBet = (int) Math.round((getMyBet() + getMyStack())
				* 2 * (handStrength - 0.5));
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
		lstm.output = 0;
	}

	@Override
	public String getName() {
		return "LSTM Heads-up Player (ID = " + id + ")";
	}

	private Module lstm;

	static public final int inputSize = 7;
}
