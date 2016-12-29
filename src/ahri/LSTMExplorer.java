package ahri;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import LSTMPlus.*;
import holdem.ActionBase;
import holdem.PlayerBase;
import holdem.TableInfo;
import opponent_model.Intel;
import opponent_model.Params;
import opponent_model.Statistician;

class LSTMExplorer extends Controller implements Statistician {

	LSTMExplorer(Ahri player) throws Exception {
		super(player);
		this.player = player;
		gameLayer = new LSTMLayer[gameLayerCnt];
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i] = new LSTMLayer(inputSize, gameLayerCellCnt);
		matchLayer = new LSTMLayer[matchLayerCnt];
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i] = new LSTMLayer(inputSize, matchLayerCellCnt);
		cNet = new FFNetwork(ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
				hiddenCnt, outputSize);
	}

	LSTMExplorer(Ahri player, double[] genome) throws Exception {
		super(player);
		this.player = player;
		constructByGenome(genome);
	}

	LSTMExplorer(Ahri player, String genomeFile) throws Exception {
		super(player);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int genomeLength = LSTMExplorer.getGenomeLength();
		double[] genome = new double[genomeLength];
		for (int i = 0; i < genomeLength; i++)
			genome[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
		constructByGenome(genome);
	}

	private void constructByGenome(double[] genome) throws Exception {
		if (genome.length != getGenomeLength())
			throw new Exception(
					"LSTMNoLimitTester.LSTMNoLimitTester(int,LSTMNoLimitTesterGenome): Invalid genome length.");
		int gameLayerGenomeLength = LSTMLayer.getGenomeLength(inputSize, gameLayerCellCnt);
		gameLayer = new LSTMLayer[gameLayerCnt];
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i] = new LSTMLayer(inputSize, gameLayerCellCnt,
					Util.subArray(genome, gameLayerGenomeLength * i, gameLayerGenomeLength));
		int matchLayerGenomeLength = LSTMLayer.getGenomeLength(inputSize, matchLayerCellCnt);
		matchLayer = new LSTMLayer[matchLayerCnt];
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i] = new LSTMLayer(inputSize, matchLayerCellCnt, Util.subArray(genome,
					gameLayerGenomeLength * gameLayerCnt + matchLayerGenomeLength * i, matchLayerGenomeLength));
		cNet = new FFNetwork(ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
				hiddenCnt, outputSize,
				Util.tail(genome,
						FFNetwork.getGenomeLength(
								ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt,
								hiddenCnt, outputSize)));
	}

	double[] getGenome() {
		double[] genome = null;
		for (int i = 0; i < gameLayerCnt; i++)
			genome = Util.concat(genome, gameLayer[i].getGenome());
		for (int i = 0; i < matchLayerCnt; i++)
			genome = Util.concat(genome, matchLayer[i].getGenome());
		genome = Util.concat(genome, cNet.getGenome());
		return genome;
	}

	static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(inputSize, gameLayerCellCnt) * gameLayerCnt
				+ LSTMLayer.getGenomeLength(inputSize, matchLayerCellCnt) * matchLayerCnt
				+ FFNetwork.getGenomeLength(
						ostatsLength + gameLayerCellCnt * gameLayerCnt + matchLayerCellCnt * matchLayerCnt, hiddenCnt,
						outputSize);
	}

	void matchStart() {
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i].reset();
		for (int i = 0; i < matchLayerCnt; i++)
			matchLayer[i].reset();
	}

	void refresh() {
		for (int i = 0; i < gameLayerCnt; i++)
			gameLayer[i].reset();
	}

	ActionBase recommend(Vector<ActionBase> actions, TableInfo info, String holeCards, Intel intel) throws Exception {
		double[] input = new double[inputSize];
		double opponentTotal = (info.potSize + info.currentBet - player.getMyBet()) / 2;
		double myTotal = (info.potSize - (info.currentBet - player.getMyBet())) / 2;
		input[0] = getStage(info.board.length());
		input[1] = 2 * myTotal / Params.stk - 1.0;
		input[2] = 2 * opponentTotal / Params.stk - 1.0;
		input[3] = 2 * evaluator.getHandStength(holeCards, info.board, info.playerInfos.size() - 1) - 1.0;
		input[4] = 4 * (getPotOdds(info) - 0.25);
		double confidence = cNet.activate(Util.concat(getGameLayerOutput(input), getMatchLayerOutput(input)))[0];
		double threshold = -1;
		for (int i = 0; i < actions.size() - 1; i++)
			if (confidence <= (threshold += 2 * Math.pow(0.5, i + 1)))
				return actions.get(i);
		return actions.get(actions.size() - 1);
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

	private double getPotOdds(TableInfo info) {
		double amtToCall = info.currentBet - player.getMyBet();
		if (amtToCall > player.getMyStack())
			amtToCall = player.getMyStack();
		return amtToCall / (amtToCall + info.potSize);
	}

	LSTMLayer[] gameLayer;
	LSTMLayer[] matchLayer;
	FFNetwork cNet;
	PlayerBase player;

	public static final int inputSize = 5;
	public static final int gameLayerCnt = 10;
	public static final int gameLayerCellCnt = 5;
	public static final int matchLayerCnt = 1;
	public static final int matchLayerCellCnt = 10;
	public static final int ostatsLength = 0;
	public static final int hiddenCnt = 7;
	public static final int outputSize = 1;
}
