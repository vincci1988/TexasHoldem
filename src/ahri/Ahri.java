package ahri;

import java.io.IOException;
import java.util.Vector;

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
import opponent_model.GameForest;
import opponent_model.Intel;
import opponent_model.Statistician;

public class Ahri extends PlayerBase implements Evolvable, Statistician {

	public Ahri(int id) throws Exception {
		super(id);
		forest = new GameForest(id);
		forestFile = null;
		arbitrator = new Arbitrator();
		explorer = new Explorer(this);
		exploiter = new Exploiter(this);
	}

	public Ahri(int id, AhriGenome genome) throws Exception {
		super(id);
		forest = new GameForest(id);
		forestFile = null;
		constructByGenome(genome);
	}

	public Ahri(int id, String genomeFile) throws Exception {
		super(id);
		forest = new GameForest(id);
		forestFile = null;
		constructByGenome(new AhriGenome(genomeFile));
	}

	public Ahri(int id, String genomeFile, String forestFile) throws Exception {
		super(id);
		forest = new GameForest(id, forestFile);
		this.forestFile = forestFile;
		constructByGenome(new AhriGenome(genomeFile));
	}

	private void constructByGenome(AhriGenome genome) throws Exception {
		double[] genes = genome.getGenes();
		arbitrator = new Arbitrator(Util.head(genes, Arbitrator.getGenomeLength()));
		explorer = new Explorer(this);
		exploiter = new Exploiter(this);
	}

	@Override
	public GenomeBase getGenome() {
		double[] genes = arbitrator.getGenome();
		return new AhriGenome(genes);
	}

	public static int getGenomeLength() {
		return Arbitrator.getGenomeLength();
	}

	@Override
	public void matchStart() {
		try {
			if (forestFile == null)
				forest.reset();
			else
				forest = new GameForest(id, forestFile);
			explorer.matchStart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void gameStart() {
		forest.prepare();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		Intel intel = forest.getIntel();
		Vector<ActionBase> actions = getAvailableActions(info, intel.getBetCnt());
		//double[] current = intel.currentState();
		//double[][] priors = intel.prior();
		double[][] actionVectors = new double[actions.get(0) instanceof Fold ? actions.size() - 1 : actions.size()][];
		
		for (int i = 0, j = 0; i < actions.size(); i++) {
			if (!(actions.get(i) instanceof Fold))
				actionVectors[j++] = intel.evaluate(actions.get(i), info);
		}
		boolean exloit = arbitrator.exploitable(actionVectors);
		return exloit ? exploiter.recommend(actions, info, peek(), intel)
				: explorer.recommend(actions, info, peek(), intel);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
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

	@Override
	public String getName() {
		return "Ahri (ID = " + id + ")";
	}

	public void saveForest(String path) throws IOException {
		forest.save(path);
		System.out.println("Game forest saved (" + forest.getTotalNodeCnt() + " nodes).");
	}

	private Vector<ActionBase> getAvailableActions(TableInfo info, int betCnt) {
		int potSizeBet = (info.potSize + info.currentBet - getMyBet());
		Vector<ActionBase> actions = new Vector<ActionBase>();
		if (info.currentBet > getMyBet())
			actions.add(new Fold(this));
		if (info.currentBet < getMyBet() + getMyStack()) {
			if (info.currentBet == getMyBet())
				actions.add(new Check(this));
			else
				actions.add(new Call(this));
		}
		if (betCnt < 4 || info.board.length() == 10) {
			if (info.currentBet + potSizeBet / 2 < getMyBet() + getMyStack())
				actions.add(new Raise(this, info.currentBet + potSizeBet / 2));
			if (info.currentBet + potSizeBet < getMyBet() + getMyStack())
				actions.add(new Raise(this, info.currentBet + potSizeBet));
		}
		if (info.board.length() == 10 || info.currentBet + potSizeBet >= getMyBet() + getMyStack())
			actions.add(new AllIn(this));
		return actions;
	}

	GameForest forest;
	Arbitrator arbitrator;
	Explorer explorer;
	Exploiter exploiter;

	private String forestFile;
}
