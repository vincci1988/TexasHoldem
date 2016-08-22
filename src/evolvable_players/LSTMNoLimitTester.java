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
		inputCells = new Cell[inputCellCnt];
		for (int i = 0; i < inputCellCnt; i++)
			inputCells[i] = new Cell(gameNodeInputSize);
		hiddenCells = new Cell[hiddenCellCnt];
		for (int i = 0; i < hiddenCellCnt; i++)
			hiddenCells[i] = new Cell(hiddenInputSize);
		oNet = new StdNetwork(inputCellCnt, oNetHiddenCnt, oNetOutputCnt);
		iNet = new StdNetwork(gameNodeInputSize, iNetHiddenCnt, iNetOutputCnt);
		myPrevCommitment = 0.0;
		opponentPrevCommitment = 0.0;
	}

	public LSTMNoLimitTester(int id, LSTMNoLimitTesterGenome childGenome) throws Exception {
		super(id);
		inputCells = new Cell[inputCellCnt];
		hiddenCells = new Cell[hiddenCellCnt];
		int inputCellGenomeLength = (gameNodeInputSize + 3) * 4 + 1;
		double[] inputCellGenome = new double[inputCellGenomeLength];
		for (int i = 0; i < inputCellCnt; i++) {
			for (int j = 0; j < inputCellGenomeLength; j++) {
				inputCellGenome[j] = childGenome.getGenes()[j + i * inputCellGenomeLength];
			}
			inputCells[i] = new Cell(inputCellGenome);
		}
		int hiddenCellGenomeLength = (hiddenInputSize + 3) * 4 + 1;
		double[] hiddenCellGenome = new double[hiddenCellGenomeLength];
		for (int i = 0; i < hiddenCellCnt; i++) {
			for (int j = 0; j < hiddenCellGenomeLength; j++) {
				hiddenCellGenome[j] = childGenome.getGenes()[j + i * hiddenCellGenomeLength
						+ inputCellCnt * (inputCellGenomeLength)];
			}
			hiddenCells[i] = new Cell(hiddenCellGenome);
		}
		int oNetStart = inputCellCnt * inputCellGenomeLength + hiddenCellCnt * hiddenCellGenomeLength;
		int oNetLength = inputCellCnt * 2 + (inputCellCnt + 1) * oNetHiddenCnt + (oNetHiddenCnt + 1) * oNetOutputCnt;
		oNet = new StdNetwork(inputCellCnt, oNetHiddenCnt, oNetOutputCnt,
				Misc.subArray(childGenome.getGenes(), oNetStart, oNetLength));
		iNet = new StdNetwork(gameNodeInputSize, iNetHiddenCnt, iNetOutputCnt,
				Misc.tail(childGenome.getGenes(), oNetStart + oNetLength));
		myPrevCommitment = 0.0;
		opponentPrevCommitment = 0.0;
	}

	@Override
	public GenomeBase getGenome() {
		int inputCellGenomeLength = (gameNodeInputSize + 3) * 4 + 1;
		double[] inputLayerGenome = new double[inputCellCnt * inputCellGenomeLength];
		for (int i = 0; i < inputCellCnt; i++) {
			double[] cellGenome = inputCells[i].getGenome();
			for (int j = 0; j < inputCellGenomeLength; j++) {
				inputLayerGenome[j + i * inputCellGenomeLength] = cellGenome[j];
			}
		}
		int hiddenCellGenomeLength = (hiddenInputSize + 3) * 4 + 1;
		double[] hiddenLayerGenome = new double[hiddenCellCnt * hiddenCellGenomeLength];
		for (int i = 0; i < hiddenCellCnt; i++) {
			double[] cellGenome = hiddenCells[i].getGenome();
			for (int j = 0; j < hiddenCellGenomeLength; j++) {
				hiddenLayerGenome[j + i * hiddenCellGenomeLength] = cellGenome[j];
			}
		}
		double[] genome = Misc.concat(inputLayerGenome, hiddenLayerGenome);
		genome = Misc.concat(genome, oNet.getGenome());
		genome = Misc.concat(genome, iNet.getGenome());
		return new LSTMNoLimitTesterGenome(genome);
	}
	
	public void matchStart() {
		reset();
	}
	
	public void reset() {
		for (int i = 0; i < inputCells.length; i++)
			inputCells[i].reset();
		for (int i = 0; i < hiddenCells.length; i++)
			hiddenCells[i].reset();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		double[] input = new double[gameNodeInputSize];
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
		input[1] = 2 * (myBet + previousBet / 2.0) / myTotal - 1.0;
		input[2] = 2 * (opponentBet + previousBet / 2.0) / opponentTotal - 1.0;
		input[3] = 2 * evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1) - 1.0;
		input[4] = 4 * (getPotOdds(info) - 0.25);
		double opportunity = estimateOpportunity(input);
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
		for (int i = 0; i < gameNodeCnt; i++)
			inputCells[i].reset();
		myPrevCommitment = 0.0;
		opponentPrevCommitment = 0.0;
	}

	@Override
	public String getName() {
		return "LSTM Tester (ID = " + id + ")";
	}

	private double getStage(int boardLength) {
		return 2 * (boardLength / 10.0) - 1.0;
	}

	private double estimateOpportunity(double[] input) throws Exception {
		double[] inputCellOutputs = new double[inputCellCnt];
		for (int i = 0; i < inputCellCnt; i++) {
			double[] X = iNet.activate(input);
			inputCellOutputs[i] = inputCells[i].activate(X);
		}
		double[] hiddenCellOutputs = new double[hiddenCellCnt];
		for (int i = 0; i < hiddenCellCnt; i++) {
			double[] X = Misc.tail(inputCellOutputs, gameNodeCnt);
			hiddenCellOutputs[i] = hiddenCells[i].activate(X);
		}
		for (int i = 0; i < hiddenCellCnt; i++)
			inputCellOutputs[i + gameNodeCnt] = hiddenCellOutputs[i];
		return oNet.activate(inputCellOutputs)[0];
	}

	Cell[] inputCells;
	Cell[] hiddenCells;
	StdNetwork oNet;
	StdNetwork iNet;
	double myPrevCommitment;
	double opponentPrevCommitment;

	public static final int gameNodeCnt = 75;
	public static final int gameNodeInputSize = 5;
	public static final int hiddenInputSize = 25;
	public static final int inputCellCnt = 100;
	public static final int hiddenCellCnt = 25;
	public static final int oNetHiddenCnt = 150;
	public static final int oNetOutputCnt = 1;
	public static final int iNetHiddenCnt = 15;
	public static final int iNetOutputCnt = 5;
}
