package simple_dynamic_players;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import evolvable_players.Evolvable;
import evolvable_players.GenomeBase;
import evolvable_players.LSTMNoLimitTesterGenome;
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

public class Ahri extends PlayerBase implements Evolvable {

	public Ahri(int id) throws Exception {
		super(id);
		forest = new GameForest(id);
		rand = new Random();
	}

	public Ahri(int id, LSTMNoLimitTesterGenome genome) throws Exception {
		super(id);
		constructByGenome(genome);
	}

	public Ahri(int id, String genomeFile) throws Exception {
		super(id);
		LSTMNoLimitTesterGenome genome = new LSTMNoLimitTesterGenome(genomeFile);
		constructByGenome(genome);
	}

	private void constructByGenome(LSTMNoLimitTesterGenome genome) throws Exception {
		forest = new GameForest(id);
		rand = new Random();
	}

	@Override
	public GenomeBase getGenome() {
		return null;
	}

	public static int getGenomeLength() {
		return 0;
	}

	@Override
	public void matchStart() {
		forest.reset();
	}

	@Override
	public void gameStart() {
		forest.prepare();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {

		Vector<ActionBase> actions = getAvailableActions(info);
		Intel intel = forest.getIntel();
		intel.toString();
		
		if (OppStats.getStage(info.board) == 3) {
			return actions.get(rand.nextInt(actions.size()));
		}

		if (actions.get(0) instanceof Fold)
			return actions.get(1 + rand.nextInt(actions.size() - 2 > 0 ? actions.size() - 2 : 1));
		return actions.get(rand.nextInt(actions.size() - 1));
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
		System.out.println(forest.getTotalNodeCnt() + " nodes");
	}

	private Vector<ActionBase> getAvailableActions(TableInfo info) {
		int potSizeBet = (info.potSize + info.currentBet - getMyBet());
		Vector<ActionBase> actions = new Vector<ActionBase>();
		if (info.currentBet > getMyBet())
			actions.add(new Fold(this));
		if (info.currentBet < getMyBet() + getMyStack()) {
			if (info.currentBet == getMyBet())
				actions.add(new Check(this));
			else
				actions.add(new Call(this));
			if (info.currentBet + potSizeBet / 2 < getMyBet() + getMyStack())
				actions.add(new Raise(this, info.currentBet + potSizeBet / 2));
			if (info.currentBet + potSizeBet < getMyBet() + getMyStack())
				actions.add(new Raise(this, info.currentBet + potSizeBet));
		}
		actions.add(new AllIn(this));
		return actions;
	}

	Random rand;
	GameForest forest;
}
