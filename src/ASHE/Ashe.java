package ASHE;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

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

public class Ashe extends PlayerBase {

	public Ashe(int id) {
		super(id);
		forest = null;
		forestFile = null;
		rand = new Random();
	}

	public Ashe(int id, String forestFile) {
		super(id);
		forest = null;
		this.forestFile = forestFile;
		rand = new Random();
	}

	@Override
	public void matchStart() {
		try {
			forest = forestFile == null ? new GameForest(id) : new GameForest(id, forestFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void gameStart() {
		forest.prepare();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		Intel intel = forest.getIntel();
		Vector<ActionBase> actions = getAvailableActions(info, intel.getBetCnt());
		if (info.board.length() == 0)
			return preflop(actions, info, intel);
		if (info.board.length() < 10)
			return flopAndTurn(actions, info, intel);
		return river(actions, info, intel);
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

	public void saveForest(String path) throws IOException {
		forest.save(path);
		System.out.println("Game forest saved at " + path + " (" + forest.getTotalNodeCnt() + " nodes).");
	}

	@Override
	public String getName() {
		return "Ashe (ID = " + id + ")";
	}

	private ActionBase river(Vector<ActionBase> actions, TableInfo info, Intel intel) throws Exception {
		double handStrength = GameForest.evaluator.getHandStength(peek(), info.board);
		int best = 0;
		double bestEquity = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < actions.size(); i++) {
			double wr = estimateWinRate(info, intel, handStrength, actions.get(i));
			double equity = evaluate(wr, actions.get(i), info, intel);
			if (equity > bestEquity) {
				bestEquity = equity;
				best = i;
			}
		}
		return actions.get(best);
	}

	private ActionBase flopAndTurn(Vector<ActionBase> actions, TableInfo info, Intel intel) throws Exception {
		double handStrength = GameForest.evaluator.getHandStength(peek(), info.board);
		// ON BUTTION
		if (intel.button()) {
			if (info.currentBet == getMyBet()) {
				if (handStrength < 0.70) {
					int best = 0;
					double bestEquity = Double.NEGATIVE_INFINITY;
					for (int i = 0; i < actions.size(); i++) {
						if (actions.get(i) instanceof Raise) {
							double equity = getFoldEquity(handStrength, (Raise) actions.get(i), info, intel);
							if (equity > bestEquity) {
								best = i;
								bestEquity = equity;
							}
						}
					}
					if (bestEquity > 0)
						return actions.get(best);
					return actions.get(0);
				}
				if (rand.nextDouble() > (handStrength - 0.70) / 0.30)
					return (actions.size() > 2) ? actions.get(2) : actions.get(1);
				return actions.get(1);
			}
			if (intel.getBetCnt() == 1) {
				if (handStrength < 0.85)
					return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0)
							: actions.get(1);
				if (rand.nextDouble() < 0.5)
					return actions.size() > 2 ? actions.get(2) : actions.get(1);
				return actions.get(1);
			}
			if (intel.getBetCnt() == 2) {
				return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0)
						: actions.get(1);
			}
			return actions.get(1);
		}
		// NOT ON BUTTON
		if (info.currentBet == getMyBet()) {
			if (handStrength < 0.70) {
				int best = 0;
				double bestEquity = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < actions.size(); i++) {
					if (actions.get(i) instanceof Raise) {
						double equity = getFoldEquity(handStrength, (Raise) actions.get(i), info, intel);
						if (equity > bestEquity) {
							best = i;
							bestEquity = equity;
						}
					}
				}
				if (bestEquity > 0)
					return actions.get(best);
				return actions.get(0);
			}
			if (handStrength < 0.85) {
				if (rand.nextDouble() < (handStrength - 0.70) / 0.15)
					return (actions.size() > 2) ? actions.get(2) : actions.get(1);
				return actions.get(1);
			}
			if (rand.nextDouble() < 0.5)
				return actions.get(1);
			return actions.get(0);
		}
		if (intel.getBetCnt() == 1) {
			if (handStrength < 0.85)
				return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0)
						: actions.get(1);
			if (rand.nextDouble() < 0.5)
				return actions.size() > 2 ? actions.get(2) : actions.get(1);
			return actions.get(1);
		}
		if (intel.getBetCnt() == 2) {
			return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0) : actions.get(1);
		}
		return actions.get(1);
	}

	private ActionBase preflop(Vector<ActionBase> actions, TableInfo info, Intel intel) throws Exception {
		double handStrength = GameForest.evaluator.getHandStength(peek(), info.board);
		// ON BUTTION
		if (intel.button()) {
			if (intel.getBetCnt() == 1) {
				if (handStrength < 0.40) {
					if (getFoldEquity(handStrength, (Raise) actions.get(3), info, intel) > 0)
						return actions.get(3);
					return actions.get(0);
				}
				if (handStrength > 0.66)
					return actions.get(3); // pot size bet
				if (rand.nextDouble() < handStrength)
					return actions.get(3);
				return actions.get(2); // half-pot size bet
			}
			if (intel.getBetCnt() == 3) {
				if (handStrength < 0.40)
					return actions.get(0); // fold
				if (handStrength > 0.80)
					return actions.size() > 2 ? actions.get(2) : actions.get(1);
				return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0)
						: actions.get(1);
			}
			return actions.get(1); // all-in or call;
		}
		// NOT ON BUTTON
		// IF CALLED
		if (info.currentBet == getMyBet()) {
			if (handStrength < 0.50) {
				if (getFoldEquity(handStrength, (Raise) actions.get(2), info, intel) > 0)
					return actions.get(2);
				return actions.get(0);
			}
			if (rand.nextDouble() < handStrength)
				return actions.get(2);
			return actions.get(1);
		}
		// 2ND BET
		if (intel.getBetCnt() == 2) {
			if (handStrength < 0.40)
				return actions.get(0);
			if (handStrength > 0.66)
				return actions.get(1 + rand.nextInt(actions.size() - 1));
			return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0) : actions.get(1);
		}
		return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds(info)) ? actions.get(0) : actions.get(1);
	}
	
	private double evaluate(double winRate, ActionBase action, TableInfo info, Intel intel) {
		double fp = getFoldProb(action, info, intel);
		if (action instanceof Raise)
			return fp * (info.potSize + info.currentBet - action.getBet()) / 2 + (1 - fp) * (2 * winRate - 1)
					* ((info.potSize - info.currentBet - action.getBet()) / 2 + ((Raise) action).getAmt());
		if (action instanceof AllIn)
			return fp * (info.potSize + info.currentBet - action.getBet()) / 2
					+ (1 - fp) * (2 * winRate - 1) * AsheParams.stk;
		if (action instanceof Call)
			return (2 * winRate - 1) * (info.potSize + info.currentBet - action.getBet()) / 2;
		if (action instanceof Fold)
			return -(info.potSize + action.getBet() - info.currentBet) / 2;
		if (action instanceof Check) {
			if (intel.button())
				return (2 * winRate - 1) * info.potSize / 2;
			else {
				double expectedRaise = 0;
				double raiseProb = 0;
				NodeBase checkNode = intel.next(action, info);
				if (checkNode != null) {
					for (int i = 0; i < checkNode.children.size(); i++) {
						if (checkNode.children.get(i).conditionCode > 3) {
							raiseProb += checkNode.children.get(i).stats.frequency;
							expectedRaise += checkNode.children.get(i).stats.frequency * Tools
									.getRaiseAmtToPot(checkNode.children.get(i).conditionCode, info.potSize);
						}
					}
					raiseProb /= checkNode.stats.frequency;
					expectedRaise /= checkNode.stats.frequency;
				}
				return (1 - raiseProb) * (2 * winRate - 1) * info.potSize / 2
						+ raiseProb * (winRate > expectedRaise / (2 * expectedRaise + 1.0)
								? (2 * winRate - 1) * info.potSize * (0.5 + expectedRaise) : -info.potSize / 2);
			}
		}
		return 0;
	}
	
	private double getFoldEquity(double handStrength, Raise raise, TableInfo info, Intel intel) {
		double fp = getFoldProb(raise, info, intel);
		double winRate = Math.pow(handStrength, 1.0 + fp);
		return fp * (info.potSize + info.currentBet - raise.getBet()) / 2 + (1 - fp)
				* ((2 * winRate - 1) * ((info.potSize - info.currentBet - raise.getBet()) / 2 + raise.getAmt()));
	}
	
	private double getFoldProb(ActionBase raise, TableInfo tableInfo, Intel intel) {
		NodeBase raiseNode = intel.next(raise, tableInfo);
		if (raiseNode == null)
			return 0;
		NodeBase fd = null;
		for (int i = 0; i < raiseNode.children.size(); i++) {
			if (raiseNode.children.get(i).conditionCode == 1) {
				fd = raiseNode.children.get(i);
				break;
			}
		}
		return intel.getStateFreq(fd);
	}

	private boolean shouldFold(double handStrength, double frequency, double potOdds) {
		return Math.pow(handStrength, 2 - frequency) < potOdds;
	}

	private double estimateWinRate(TableInfo info, Intel intel, double handStrength, ActionBase action) {
		if (action instanceof Fold)
			return 0.0;
		if (action instanceof Raise) {
			double potOdds = 1.0 * (((Raise) action).getAmt() - info.currentBet)
					/ (info.potSize + 2 * ((Raise) action).getAmt() - info.currentBet - getMyBet());
			if (handStrength < potOdds)
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - potOdds);
		}
		if (action instanceof AllIn && getMyStack() + getMyBet() > info.currentBet) {
			double potOdds = (getMyStack() + getMyBet() - info.currentBet) / AsheParams.stk / 2;
			if (handStrength < potOdds)
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - potOdds);
		}
		return estimateWinRate(intel, handStrength);
	}
	
	private double estimateWinRate(Intel intel, double handStrength) {
		NodeBase node = intel.record.firstElement();
		while (node.parent != null)
			node = node.parent;
		double showdownProb = 1.0 * (1.0 + node.stats.showdown + 0.5 * node.stats.myFold)
				/ (1.0 + node.stats.frequency);
		return Math.pow(handStrength, 1 / showdownProb);
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
	String forestFile;
	Random rand;
}
