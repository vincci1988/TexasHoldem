package ahri;

import java.util.Vector;

import holdem.ActionBase;
import holdem.TableInfo;
import opponent_model.Intel;
import opponent_model.Statistician;

abstract class Controller implements Statistician {
	Controller(Ahri me) {
		this.player = me;
	}
	
	abstract ActionBase recommend(Vector<ActionBase> actions, TableInfo info, String holeCards, Intel intel)
			throws Exception;
	
	void init(Vector<ActionBase> actions, TableInfo info, Intel intel) {
		this.actions = actions;
		priors = intel.prior();
		present = intel.currentState();
		moves = new double[actions.size()][];
		for (int i = 0; i < actions.size(); i++)
			moves[i] = intel.evaluate(actions.get(i), info);
	}
	
	Ahri player;
	protected Vector<ActionBase> actions;
	protected double[][] priors;
	protected double[] present;
	protected double[][] moves;
}
