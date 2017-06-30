package advanced_players;

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

public class Teemo extends PlayerBase implements HandRangeAnalyzer {

	public Teemo(int id) {
		super(id);
		position = -1;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (position == -1)
			position = 1;

		double r = 0.5; // half-pot raise
		evaluator.update(peek(), info.board);

		if (info.board.length() == 0 && button() && info.currentBet == info.BBAmt) {
			double rankStrength = evaluator.getRankStrength();
			if (rankStrength < 0.25)
				return new Fold(this);
			if (rankStrength > 1.0 - (1 - r * r / Math.pow(1.0 + r, 2)) / 2) {
				evaluator.adjustRange(1.0 / 2 / (1.0 + r));
				return raise(info, r);
			}
			return new Call(this);
		}

		if (info.currentBet == getMyBet()) {
			double rankStrength = evaluator.getRankStrength();
			if (rankStrength < 1.0 - (1 - r * r / Math.pow(1.0 + r, 2)) / 2)
				return new Check(this);
			evaluator.adjustRange(1.0 / 2 / (1.0 + r));
			return raise(info, 0.5);
		}

		double s = info.potSize;
		double d = info.currentBet - getMyBet();
		double cCond = (s + d) / 2 / s;
		double rankStrength = evaluator.getRankStrength();
		
		if (rankStrength < cCond)
			return new Fold(this);
		
		double rrCond = 1.0 - cCond * (1 - r * r / Math.pow(1.0 + r, 2)) / 2;
		
		if (rankStrength > rrCond) {
			evaluator.adjustRange(1.0 / 2 / (1.0 + r));
			return raise(info, r);
		}
				
		return new Call(this);
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
	public void gameStart() {
		position = -1;
		evaluator.gameStart();
	}

	@Override
	public String getName() {
		return "Teemo (ID = " + id + ")";
	}

	public String toString() {
		return "Teemo";
	}

	ActionBase call(TableInfo info) {
		return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
	}

	ActionBase raise(TableInfo info, double r) {
		int potSizeBet = info.currentBet + info.potSize - getMyBet();
		int bet = info.currentBet + (int) (r * potSizeBet < info.minRaise ? info.minRaise : r * potSizeBet);
		return bet < getMyBet() + getMyStack() ? new Raise(this, bet) : new AllIn(this);
	}

	boolean button() {
		return position == 1;
	}

	private int position;
}
