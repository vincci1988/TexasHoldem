package ashe;

import java.io.IOException;
import java.util.Vector;

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

public class Ashe extends PlayerBase implements Evolvable, Statistician {

	public Ashe(int id) throws Exception {
		super(id);
		forest = new GameForest(id);
		adviser = new NNAdviser(this);
		forestFile = null;
	}

	public Ashe(int id, AsheGenome genome) throws Exception {
		super(id);
		constructByGenome(genome);
	}

	public Ashe(int id, String genomeFile) throws Exception {
		super(id);
		AsheGenome genome = new AsheGenome(genomeFile);
		constructByGenome(genome);
	}

	public Ashe(int id, String genomeFile, String forestFile) throws Exception {
		super(id);
		AsheGenome genome = new AsheGenome(genomeFile);
		forest = new GameForest(id, forestFile);
		adviser = new NNAdviser(this, genome.getGenes());
		this.forestFile = forestFile;
	}

	private void constructByGenome(AsheGenome genome) throws Exception {
		forest = new GameForest(id);
		adviser = new NNAdviser(this, genome.getGenes());
		forestFile = null;
	}

	@Override
	public GenomeBase getGenome() {
		return ((NNAdviser) adviser).getGenome();
	}

	public static int getGenomeLength() {
		return NNAdviser.getGenomeLength();
	}

	@Override
	public void matchStart() {
		try {
			if (forestFile == null)
				forest.reset();
			else
				forest = new GameForest(id, forestFile);
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
		return adviser.recommend(info, peek(), intel);
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
		return "Ashe (ID = " + id + ")";
	}

	public void saveForest(String path) throws IOException {
		forest.save(path);
		System.out.println("Game forest saved (" + forest.getTotalNodeCnt() + " nodes).");
	}

	Vector<ActionBase> getAvailableActions(TableInfo info, int betCnt) {
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
	AdviserBase adviser;
	private String forestFile;
}
