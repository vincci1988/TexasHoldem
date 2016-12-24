package advanced_players;

import java.io.IOException;
import java.util.Random;

import evolvable_players.Statistician;
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

public class Shaco extends PlayerBase implements Statistician {

	public Shaco(int id) {
		super(id);
		ostats = new OpponentStats();
		board = "UNSPECIFIED";
		previousBet = 0;
		rand = new Random();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		if (!board.equals(info.board)) {
			board = info.board;
			previousBet = board.length() > 0 ? 0 : (onButton(info) ? info.BBAmt : info.BBAmt / 2);
		}
		ActionBase action = info.board.length() == 0 ? preflop(info, handStrength)
				: flop_turn_river(info, handStrength);
		previousBet = info.currentBet;

		return action;
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (actionInfo.playerID != this.id) {
			ostats.actionUpdate(actionInfo);
		}
	}

	@Override
	public void observe(Result resultInfo) throws Exception {
		ostats.gameUpdate(resultInfo);
	}

	@Override
	public void gameStart() {
		previousBet = 0;
		board = "UNSPECIFIED";
	}

	@Override
	public void matchStart() {
		ostats.reset();
	}

	@Override
	public String getName() {
		return "Shaco (ID = " + id + ")";
	}

	private ActionBase preflop(TableInfo info, double handStrength) throws IOException {
		// 1. opponent called
		// 2. opponent has not made her move
		if (info.currentBet == getMyBet() || getMyBet() == info.BBAmt / 2) {
			int ideal = (int) (2.5 * info.BBAmt);
			if (info.currentBet == getMyBet()) {
				if (handStrength < 0.375)
					return new Check(this);
				if (handStrength < 0.5)
					return rand.nextDouble() < (handStrength - 0.375) / 0.125
							? (ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this))
							: new Check(this);
			}
			if (handStrength < 0.375)
				return new Call(this);
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}

		// 3. opponent raised
		Random rand = new Random();
		if (handStrength > 0.80) {
			int ideal = 2 * info.currentBet + info.potSize - getMyBet();
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}

		double oc = (double) (info.currentBet - previousBet) / (info.potSize + previousBet - info.currentBet)
				/ ostats.getAggression(info.board);
		handStrength = adjustHandStrength(handStrength, oc, ostats.getHandRange());
		if (handStrength > getPotOdds(info))
			return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
		if (rand.nextDouble() < ostats.getFoldRate(info.board) / oc) {
			int ideal = 2 * info.currentBet + info.potSize - getMyBet();
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}
		return new Fold(this);

	}

	private ActionBase flop_turn_river(TableInfo info, double handStrength) throws IOException {
		double fr = ostats.getFoldRate(info.board);
		if (info.currentBet == getMyBet())
			return FirstMoveOrOpponentChecked(info, handStrength, fr);
		return opponentRaised(info, handStrength, fr);
	}

	private ActionBase FirstMoveOrOpponentChecked(TableInfo info, double handStrength, double foldRate)
			throws IOException {
		handStrength = adjustHandStrength(handStrength, onButton(info) ? 0 : 1.0, ostats.getHandRange());
		if (foldRate == 0)
			return handStrength > 0.5 ? new AllIn(this) : new Check(this);
		if (handStrength == 0.5)
			return new Check(this);
		double s = info.potSize;
		if (getMyStack() <= info.minRaise) {
			double y = getMyStack();
			double reward = foldRate * y + (1 - foldRate * y / s) * (handStrength * s + (2 * handStrength - 1) * y);
			if (reward > 0)
				return new AllIn(this);
			return new Check(this);
		}
		double y = ((1 - handStrength) / (2 * handStrength - 1) + 1 / foldRate) * s / 2;
		double reward = 0;
		ActionBase action = null;
		if (getMyStack() <= info.potSize) {
			if (y < info.minRaise) {
				y = info.minRaise;
				action = new Raise(this, info.minRaise);
			} else if (y >= info.potSize) {
				y = getMyStack();
				action = new AllIn(this);
			} else {
				action = new Raise(this, (int) y);
			}
			reward = foldRate * y + (1 - foldRate * y / s) * (handStrength * s + (2 * handStrength - 1) * y);
		} else {
			if (y < info.minRaise) {
				y = info.minRaise;
				action = new Raise(this, info.minRaise);
			} else if (y >= info.potSize) {
				y = info.potSize;
				action = new Raise(this, info.potSize);
			} else {
				action = new Raise(this, (int) y);
			}
			reward = foldRate * y + (1 - foldRate * y / s) * (handStrength * s + (2 * handStrength - 1) * y);
			double y2 = handStrength > 0.5 ? getMyBet() : s;
			double reward2 = s * foldRate + (1 - foldRate) * (handStrength * s + (2 * handStrength - 1) * y2);
			if (reward2 > reward) {
				reward = reward2;
				action = (handStrength > 0.5) ? new AllIn(this) : new Raise(this, info.potSize);
			}
		}
		if (reward > 0)
			return action;
		return new Check(this);
	}

	private ActionBase opponentRaised(TableInfo info, double handStrength, double foldRate) throws IOException {
		double oc = (double) (info.currentBet - previousBet) / (info.potSize + previousBet - info.currentBet)
				/ ostats.getAggression(info.board);
		handStrength = adjustHandStrength(handStrength, oc, ostats.getHandRange());
		double potOdds = getPotOdds(info);
		if (foldRate == 0 || opponentAllIn(info)) {
			if (handStrength > 0.5)
				return new AllIn(this);
			if (handStrength > potOdds)
				return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
			return new Fold(this);
		}

		if (handStrength == 0.5)
			return info.currentBet < getMyBet() + getMyStack() ? new Fold(this) : new AllIn(this);
		double x = info.currentBet - getMyBet();
		double s = info.potSize;
		double callReward = handStrength * s - (1 - handStrength) * (x > getMyStack() ? getMyStack() : x);
		ActionBase bestPassive = (callReward > 0) ? (x > getMyStack() ? new AllIn(this) : new Call(this))
				: new Fold(this);
		if (getMyStack() <= x)
			return bestPassive;
		double passiveReward = callReward > 0 ? callReward : 0;
		double aggressiveReward = 0.0;
		ActionBase bestAggressive = null;
		if (getMyStack() < x + info.minRaise) {
			aggressiveReward = s * foldRate * (getMyStack() - x) / (s + x)
					+ (1 + foldRate * (getMyStack() - x) / (s + x))
							* (handStrength * s + (2 * handStrength - 1) * (getMyStack() - x) + (handStrength - 1) * x);
			return aggressiveReward > passiveReward ? new AllIn(this) : bestPassive;
		}
		double y = ((1 - handStrength) / (2 * handStrength - 1) + 1 / foldRate) * (s + x) / 2;
		if (getMyStack() <= s + x) {
			if (y > s + x) {
				y = getMyStack() - x;
				bestAggressive = new AllIn(this);
			} else if (y < x + info.minRaise) {
				y = info.minRaise;
				bestAggressive = new Raise(this, info.currentBet + info.minRaise);
			} else {
				bestAggressive = new Raise(this, (int) (info.currentBet + y));
			}
			aggressiveReward = s * foldRate * y / (s + x) + (1 + foldRate * y / (s + x))
					* (handStrength * s + (2 * handStrength - 1) * y + (handStrength - 1) * x);
		} else {
			if (y > s + x) {
				y = s + x;
				bestAggressive = new Raise(this, (int) (info.currentBet + s + x));
			} else if (y < x + info.minRaise) {
				y = info.minRaise;
				bestAggressive = new Raise(this, info.currentBet + info.minRaise);
			} else {
				bestAggressive = new Raise(this, (int) (info.currentBet + y));
			}
			aggressiveReward = s * foldRate * y / (s + x) + (1 + foldRate * y / (s + x))
					* (handStrength * s + (2 * handStrength - 1) * y + (handStrength - 1) * x);
			double y2 = handStrength > 0.5 ? getMyBet() - x : s + x;
			double aggressiveReward2 = s * foldRate
					+ (1 - foldRate) * (handStrength * s + (2 * handStrength - 1) * y2 + (handStrength - 1) * x);
			if (aggressiveReward2 > aggressiveReward) {
				aggressiveReward = aggressiveReward2;
				bestAggressive = (handStrength > 0.5) ? new AllIn(this)
						: new Raise(this, (int) (info.currentBet + s + x));
			}
		}
		return aggressiveReward > passiveReward ? bestAggressive : bestPassive;
	}

	private double adjustHandStrength(double rawHandStrength, double opponentAggression, double opponentHandRange)
			throws IOException {
		double concern = (opponentAggression < 1.0 ? 1.0 : 1.368 / (1 + Math.exp(-opponentAggression)))
				/ opponentHandRange;
		//System.out.println(opponentHandRange);
		return Math.pow(rawHandStrength, concern);
	}

	private boolean opponentAllIn(TableInfo info) {
		int opponentIndex = 0;
		while (info.playerInfos.get(opponentIndex).id == id)
			opponentIndex++;
		return info.playerInfos.get(opponentIndex).stack == 0;
	}

	private boolean onButton(TableInfo info) {
		if (info.board.length() == 0)
			return info.playerInfos.get(0).id == id;
		return !(info.playerInfos.get(0).id == id);
	}

	private OpponentStats ostats;
	private int previousBet;
	private String board;
	private Random rand;
}
