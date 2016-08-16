package evolvable_players;

import java.io.IOException;

import LSTM.*;
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

public class LSTMNoLimitTester extends PlayerBase implements Evolvable, Statistician {

	public LSTMNoLimitTester(int id) {
		super(id);
		lstms = new Cell[cellCnt];
		for (int i = 0; i < cellCnt; i++)
			lstms[i] = new Cell(inputSize);
	}

	public LSTMNoLimitTester(int id, LSTMNoLimitTesterGenome childGenome) throws Exception {
		super(id);
		lstms = new Cell[cellCnt];
		int cellGenomeLength = (inputSize + 3) * 4 + 1;
		double[] cellGenome = new double[cellGenomeLength];
		for (int i = 0; i < cellCnt; i++) {
			for (int j = 0; j < cellGenomeLength; j++) {
				cellGenome[j] = childGenome.getGenes()[j + i * cellGenomeLength];
			}
			lstms[i] = new Cell(cellGenome);
		}
	}

	@Override
	public GenomeBase getGenome() {
		int cellGenomeLength = (inputSize + 3) * 4 + 1;
		double[] genome = new double[cellCnt * cellGenomeLength];
		for (int i = 0; i < cellCnt; i++) {
			double[] cellGenome = lstms[i].getGenome();
			for (int j = 0; j < cellGenomeLength; j++) {
				genome[j + i * cellGenomeLength] = cellGenome[j];
			}
		}
		return new LSTMNoLimitTesterGenome(genome);
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
		input[0] = 2 * myBet / myTotal - 1.0;
		double opponentTotal = previousBet / 2.0 + opponentBet + opponentStack;
		input[1] = 2 * opponentBet / opponentTotal - 1.0;
		input[2] = 2 * evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1) - 1.0;
		input[3] = 4 * (getPotOdds(info) - 0.25);
		double opportunity = lstms[0].activate(input);
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
		for (int i = 0; i < cellCnt; i++)
			lstms[i].reset();
	}

	@Override
	public String getName() {
		return "LSTM Challenger (ID = " + id + ")";
	}

	Cell[] lstms;

	public static final int inputSize = 4;
	public static final int cellCnt = 1;
}
