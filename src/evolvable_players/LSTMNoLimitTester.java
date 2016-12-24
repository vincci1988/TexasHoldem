package evolvable_players;

import java.io.IOException;

import LSTMPlus.*;
import advanced_players.OpponentStats;
import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.ActionStats;
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
		gameLayer = new LSTMLayer[gameLayerCnt];
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i] = new LSTMLayer(inputSize, gameLayerCellCnt);
		matchLayer = new LSTMLayer[matchLayerCnt];
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i] = new LSTMLayer(inputSize, matchLayerCellCnt);
		cNet = new FFNetwork(ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
				hiddenCnt, outputSize);
		ostats = new OpponentStats();
		astats = new ActionStats();
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
		int gameLayerGenomeLength = LSTMLayer.getGenomeLength(inputSize, gameLayerCellCnt);
		gameLayer = new LSTMLayer[gameLayerCnt];
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i] = new LSTMLayer(inputSize, gameLayerCellCnt,
					Util.subArray(genome.getGenes(), gameLayerGenomeLength * i, gameLayerGenomeLength));
		int matchLayerGenomeLength = LSTMLayer.getGenomeLength(inputSize, matchLayerCellCnt);
		matchLayer = new LSTMLayer[matchLayerCnt];
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i] = new LSTMLayer(inputSize, matchLayerCellCnt, Util.subArray(genome.getGenes(),
					gameLayerGenomeLength * gameLayerCnt + matchLayerGenomeLength * i, matchLayerGenomeLength));
		cNet = new FFNetwork(ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
				hiddenCnt, outputSize,
				Util.tail(genome.getGenes(),
						FFNetwork.getGenomeLength(
								ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
								hiddenCnt, outputSize)));
		ostats = new OpponentStats();
		astats = new ActionStats();
	}

	@Override
	public GenomeBase getGenome() {
		double[] genome = null;
		for (int i = 0; i < gameLayerCnt; i++)
			genome = Util.concat(genome, gameLayer[i].getGenome());
		for (int i = 0; i < matchLayerCnt; i++)
			genome = Util.concat(genome, matchLayer[i].getGenome());
		genome = Util.concat(genome, cNet.getGenome());
		return new LSTMNoLimitTesterGenome(genome);
	}

	public static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(inputSize, gameLayerCellCnt) * gameLayerCnt
				+ LSTMLayer.getGenomeLength(inputSize, matchLayerCellCnt) * matchLayerCnt
				+ FFNetwork.getGenomeLength(
						ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt, hiddenCnt,
						outputSize);
	}

	public void matchStart() {
		ostats.reset();
		astats.reset();
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i].reset();
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i].reset();
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
		double opportunity = cNet.activate(Util.concat(getGameLayerOutput(input), getMatchLayerOutput(input)))[0];
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
			//int potSize = info.potSize + info.currentBet - getMyBet();
			//int raiseTo = info.currentBet + (2 * diff / potSize) * potSize / 2;
			int raiseTo = info.currentBet + (diff / info.BBAmt) * info.BBAmt;
			//if (raiseTo > info.currentBet + 2 * potSize)
			//	raiseTo = info.currentBet + 2 * potSize;
			if (raiseTo < info.currentBet + info.minRaise)
				return getMyBet() == info.currentBet ? new Check(this) : new Call(this);
			return new Raise(this, raiseTo);
		}
		return getMyBet() == info.currentBet ? new Check(this) : new Call(this);

	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (actionInfo.playerID != this.id) {
			ostats.actionUpdate(actionInfo);
		}
		astats.update(this.id, actionInfo);
	}

	@Override
	public void observe(Result resultInfo) {
		ostats.gameUpdate(resultInfo);
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i].reset();
	}

	@Override
	public String getName() {
		return "LSTM No-Limit Player (ID = " + id + ")";
	}

	private double getStage(int boardLength) {
		return 2 * (boardLength / 10.0) - 1.0;
	}

	private double[] getGameLayerOutput(double[] x) throws Exception {
		double[] outputs = null;
		for (int i = 0; i < gameLayerCnt; i++)
			outputs = Util.concat(outputs, gameLayer[i].activate(x));
		return outputs;
	}

	private double[] getMatchLayerOutput(double[] x) throws Exception {
		double[] outputs = null;
		for (int i = 0; i < matchLayerCnt; i++)
			outputs = Util.concat(outputs, matchLayer[i].activate(x));
		return outputs;
	}

	public LSTMLayer[] gameLayer;
	public LSTMLayer[] matchLayer;
	public FFNetwork cNet;
	public OpponentStats ostats;
	public ActionStats astats;

	public static final int inputSize = 5;
	public static final int gameLayerCnt = 10;
	public static final int gameLayerCellCnt = 5;
	public static final int matchLayerCnt = 1;
	public static final int matchLayerCellCnt = 10;
	public static final int ostatsLength = 0;
	public static final int hiddenCnt = 7;
	public static final int outputSize = 1;
}
