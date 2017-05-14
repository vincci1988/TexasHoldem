package simple_players;

import java.util.Random;

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

public class CandidStatisticianPlus extends PlayerBase implements Statistician {

	public CandidStatisticianPlus(int id) {
		super(id);
		rand = new Random();
		position = -1; // unknown
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (position == -1)
			position = 1;
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
	public void gameStart() {
		position = -1;
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (position == -1)
			position = 0;
	}

	@Override
	public void observe(Result resultInfo) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Candid Statistician Plus (ID = " + id + ")";
	}

	@Override
	public String toString() {
		return "CandidStatisticianPlus";
	}

	ActionBase preflop(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = evaluator.getRankStrength(peek(), info.board);
		if (button()) {
			if (getMyBet() == info.BBAmt / 2)
				return new Raise(this, info.BBAmt * 5 / 2);
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

	ActionBase flop(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = evaluator.getRankStrength(peek(), info.board);
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

	ActionBase turn(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = evaluator.getRankStrength(peek(), info.board);
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

	ActionBase river(TableInfo info) throws Exception {
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double rankStrength = evaluator.getRankStrength(peek(), info.board);
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

	boolean button() {
		return position == 1;
	}

	private ActionBase callOrAllIn(TableInfo info) {
		return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
	}

	private ActionBase raiseOrAllIn(int size, TableInfo info) {
		return size < getMyBet() + getMyStack() ? new Raise(this, size) : new AllIn(this);
	}

	Random rand;
	int position;
}
