package evolvable_players;

import java.io.IOException;

import LSTMPlus.*;
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

	public LSTMNoLimitTester(int id) throws Exception {
		super(id);
		lstm = new LSTMLayer(inputSize, cellCnt);
		cNet = new FFNetwork(cellCnt, hiddenCnt, outputSize);
	}

	public LSTMNoLimitTester(int id, LSTMNoLimitTesterGenome genome) throws Exception {
		super(id);
		constructByGenome(genome);
	}
	
	public LSTMNoLimitTester(int id, String genomeFile) throws Exception {
		super(id);
		LSTMNoLimitTesterGenome genome = new LSTMNoLimitTesterGenome(genomeFile);
		constructByGenome(genome);
	}
	
	private void constructByGenome(LSTMNoLimitTesterGenome genome) throws Exception {
		if (genome.getGenes().length != getGenomeLength())
			throw new Exception(
					"LSTMNoLimitTester.LSTMNoLimitTester(int,LSTMNoLimitTesterGenome): Invalid genome length.");
		lstm = new LSTMLayer(inputSize, cellCnt,
				Util.head(genome.getGenes(), LSTMLayer.getGenomeLength(inputSize, cellCnt)));
		cNet = new FFNetwork(cellCnt, hiddenCnt, outputSize,
				Util.tail(genome.getGenes(), FFNetwork.getGenomeLength(cellCnt, hiddenCnt, outputSize)));
	}

	@Override
	public GenomeBase getGenome() {
		return new LSTMNoLimitTesterGenome(Util.concat(lstm.getGenome(), cNet.getGenome()));
	}

	public static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(inputSize, cellCnt)
				+ FFNetwork.getGenomeLength(cellCnt, hiddenCnt, outputSize);
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
		input[1] = 2 * (myBet + previousBet / 2) / myTotal - 1.0;
		input[2] = 2 * (opponentBet + previousBet / 2) / opponentTotal - 1.0;
		input[3] = 2 * evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1) - 1.0;
		input[4] = 4 * (getPotOdds(info) - 0.25);
		double opportunity = cNet.activate(lstm.activate(input))[0];

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

	public LSTMLayer lstm;
	public FFNetwork cNet;

	public static final int inputSize = 5;
	public static final int cellCnt = 50;
	public static final int hiddenCnt = 7;
	public static final int outputSize = 1;
}
