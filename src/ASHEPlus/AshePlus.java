package ASHEPlus;

import java.io.IOException;
import java.util.Random;

import LSTMPlus.Util;
import evolvable_players.Evolvable;
import evolvable_players.GenomeBase;
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

public class AshePlus extends PlayerBase implements Evolvable {

	public AshePlus(int id) throws Exception {
		super(id);
		forest = null;
		forestFile = null;
		rand = new Random();
		position = -1;
		constructByGenome(new AshePlusGenome(Params.GenomeFile));
	}
	
	public AshePlus(int id, String genomeFile,  double mr, double ms) throws IOException, Exception {
		super(id);
		forest = null;
		forestFile = null;
		rand = new Random();
		position = -1;
		AshePlusGenome genome = new AshePlusGenome(genomeFile);
		genome.mutate(mr, ms);
		constructByGenome(genome);
	}

	public AshePlus(int id, AshePlusGenome genome) throws Exception {
		super(id);
		forest = null;
		forestFile = null;
		constructByGenome(genome);
	}

	public AshePlus(int id, String genomeFile) throws Exception {
		super(id);
		forest = null;
		forestFile = null;
		constructByGenome(new AshePlusGenome(genomeFile));
	}

	public AshePlus(int id, String genomeFile, String forestFile) throws Exception {
		super(id);
		forest = null;
		this.forestFile = forestFile;
		constructByGenome(new AshePlusGenome(genomeFile));
	}

	private void constructByGenome(AshePlusGenome genome) throws Exception {
		double[] genes = genome.getGenes();
		rand = new Random();
		position = -1;
		WRE = new WinRateEstimator_LSTM(this,
				Util.head(genes, WinRateEstimator_LSTM.getGenomeLength()));
		FRE = new FoldRateEstimator_LSTM(this, 
				Util.tail(genes, FoldRateEstimator_LSTM.getGenomeLength()));
	}

	@Override
	public GenomeBase getGenome() {
		double[] genome = null;
		genome = Util.concat(genome, ((WinRateEstimator_LSTM) WRE).getGenome());
		genome = Util.concat(genome, ((FoldRateEstimator_LSTM) FRE).getGenome());
		return new AshePlusGenome(genome);
	}

	public static int getGenomeLength() {
		int length = 0;
		length += WinRateEstimator_LSTM.getGenomeLength();
		length += FoldRateEstimator_LSTM.getGenomeLength();
		return length;
	}

	@Override
	public void matchStart() {
		try {
			forest = (forestFile == null ? new GameForest(id) : new GameForest(id, forestFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void gameStart() {
		forest.prepare();
		position = -1;
	}
	
	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (position == -1)
			position = 1;
		HandRangeAnalyzer.evaluator.update(peek(), info.board);
		Intel intel = forest.getIntel();
		intel.button();
		int boardLength = info.board.length();
		if (boardLength == 0)
			return preflop(info);
		if (boardLength == 6)
			return flop(info);
		if (boardLength == 8)
			return turn(info);
		return river(info);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (position == -1)
			position = 0;
		forest.updateAction(actionInfo);
	}

	@Override
	public void observe(Result resultInfo) {
		try {
			forest.updateResult(resultInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveForest(String path) throws IOException {
		forest.save(path);
	}

	@Override
	public String getName() {
		return "Ashe Plus (ID = " + id + ")";
	}
	
	private ActionBase preflop(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = HandRangeAnalyzer.evaluator.relativeStrength(peek(), info.board);
		if (button()) {
			if (getMyBet() == info.BBAmt / 2)
				return rankStrength < 0.25 ? new Fold(this) : new Raise(this, info.BBAmt * 2);
			if (rankStrength < mustdefend)
				return new Fold(this);
			return callOrAllIn(info);
		}
		double potOdds = getPotOdds(info);
		if (info.currentBet == getMyBet()) {
			if (rankStrength < 0.625)
				return new Check(this);
			return raiseOrAllIn(info.currentBet
					+ (rankStrength < 0.75 ? info.BBAmt : (rankStrength < 0.875 ? info.BBAmt * 3 / 2 : info.BBAmt * 2)),
					info);
		}
		if (rankStrength < potOdds)
			return new Fold(this);
		if (rankStrength < mustdefend)
			return rand.nextDouble() < (rankStrength - potOdds) / (mustdefend - potOdds) ? callOrAllIn(info)
					: new Fold(this);
		int potSizeBet = 3 * info.currentBet;
		return rand.nextDouble() < (rankStrength - mustdefend) / (1.0 - mustdefend) ? raiseOrAllIn(potSizeBet, info)
				: callOrAllIn(info);
	}

	private ActionBase flop(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength =HandRangeAnalyzer.evaluator.relativeStrength(peek(), info.board);
		if (button()) {
			if (getMyBet() == info.currentBet)
				return rankStrength > 0.333 ? raiseOrAllIn(info.potSize * 1 / 2, info) : new Check(this);
			return (rankStrength < mustdefend) ? new Fold(this) : callOrAllIn(info);
		}
		if (getMyBet() == info.currentBet)
			return rankStrength > 0.83 && rankStrength < 0.95 ? raiseOrAllIn(info.potSize, info) : new Check(this);
		if (getMyBet() == 0)
			return (rankStrength < mustdefend) ? new Fold(this)
					: (rankStrength < 0.95 ? callOrAllIn(info)
							: raiseOrAllIn(info.potSize + 2 * info.currentBet, info));
		return (rankStrength < mustdefend) ? new Fold(this) : callOrAllIn(info);
	}

	private ActionBase turn(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = HandRangeAnalyzer.evaluator.relativeStrength(peek(), info.board);
		if (button()) {
			if (getMyBet() == info.currentBet)
				return rankStrength > 0.83 ? raiseOrAllIn(info.potSize * 1 / 2, info) : new Check(this);
			return (rankStrength < mustdefend) ? new Fold(this) : callOrAllIn(info);
		}
		if (getMyBet() == info.currentBet)
			return rankStrength > 0.83 && rankStrength < 0.95 ? raiseOrAllIn(info.potSize, info) : new Check(this);
		if (getMyBet() == 0)
			return (rankStrength < mustdefend) ? new Fold(this)
					: (rankStrength < 0.95 ? callOrAllIn(info)
							: raiseOrAllIn(info.potSize + 2 * info.currentBet, info));
		return (rankStrength < mustdefend) ? new Fold(this) : callOrAllIn(info);
	}

	private ActionBase river(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = HandRangeAnalyzer.evaluator.relativeStrength(peek(), info.board);
		if (button()) {
			if (getMyBet() == info.currentBet)
				return (rankStrength > 0.9) ? raiseOrAllIn(info.potSize / 2, info) : new Check(this);
			if (rankStrength < mustdefend)
				return new Fold(this);
			if (rankStrength > 0.95)
				return raiseOrAllIn(info.potSize, info);
			return callOrAllIn(info);
		}
		if (getMyBet() == info.currentBet)
			return rankStrength > 0.9 ? raiseOrAllIn(info.potSize / 2, info) : new Check(this);
		if (getMyBet() == 0)
			return (rankStrength < mustdefend) ? new Fold(this) : callOrAllIn(info);
		if (rankStrength < mustdefend)
			return new Fold(this);
		return rankStrength > 0.975 ? new AllIn(this) : callOrAllIn(info);
	}
	
	private ActionBase callOrAllIn(TableInfo info) {
		return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
	}

	private ActionBase raiseOrAllIn(int size, TableInfo info) {
		return size < getMyBet() + getMyStack() ? new Raise(this, size) : new AllIn(this);
	}
	
	private boolean button() {
		return position == 1;
	}

	GameForest forest;
	String forestFile;
	Random rand;
	EstimatorBase WRE;
	EstimatorBase FRE;
	private int position;
}
