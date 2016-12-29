package ashe;

import java.util.Vector;

import holdem.ActionBase;
import holdem.AllIn;
import holdem.Raise;
import holdem.TableInfo;
import opponent_model.Intel;
import opponent_model.Statistician;

abstract class AdviserBase implements Statistician {

	AdviserBase(Ashe player) {
		actions = null;
		priors = null;
		present = null;
		moves = null;
		this.player = player;
	}

	ActionBase recommend(TableInfo info, String holeCards, Intel intel) throws Exception {
		int betCnt = intel.getBetCnt();
		actions = player.getAvailableActions(info, betCnt);
		priors = extract(intel.prior());
		present = extract(intel.currentState());
		moves = new double[actions.size()][];
		for (int i = 0; i < actions.size(); i++)
			moves[i] = extract(intel.evaluate(actions.get(i), info));
		double handStrength = evaluator.getHandStength(holeCards, info.board, 1);

		int best = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < actions.size(); i++) {
			if (betCnt > 1 && info.board.length() < 10 && info.currentBet < player.getMyBet() + player.getMyStack()) {
				if (actions.get(i) instanceof AllIn || actions.get(i) instanceof Raise)
					handStrength = Math.pow(handStrength, 2);
			}
			double score = evaluate(info, handStrength, i);
			if (score > maxScore) {
				maxScore = score;
				best = i;
			}
		}
		return actions.get(best);
	}

	abstract double evaluate(TableInfo info, double handStrength, int actionIndex) throws Exception;

	abstract double[] extract(double[] intelVector);

	abstract double[][] extract(double[][] intelVectors);

	protected Vector<ActionBase> actions;
	protected double[][] priors;
	protected double[] present;
	protected double[][] moves;
	protected Ashe player;
}
