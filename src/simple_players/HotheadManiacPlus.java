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

public class HotheadManiacPlus extends PlayerBase implements ASHE.Statistician {

	public HotheadManiacPlus(int id) {
		super(id);
		rand = new Random();
		position = -1;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (position == -1)
			position = 1;
		double handStrength = evaluator.getHandStength(peek(), info.board);
		int potSizeBet = info.potSize + info.currentBet - getMyBet();
		if (info.currentBet == getMyBet()) {
			if (info.currentBet + potSizeBet / 2 >= getMyBet() + getMyStack()) 
				return handStrength < 0.9 ? new Check(this) : new AllIn(this);
			if (position == 1)
				return info.currentBet + potSizeBet < getMyBet() + getMyStack()
						? new Raise(this, info.currentBet + potSizeBet) : new AllIn(this);
			if (handStrength < rand.nextDouble())
				return new Check(this);
			if (info.currentBet + potSizeBet > getMyBet() + getMyStack())
				return new AllIn(this);
			return new Raise(this, info.currentBet + potSizeBet);
		}
		if (info.currentBet + potSizeBet / 2 >= getMyBet() + getMyStack()) {
			if (handStrength < (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize)
				return new Fold(this);
			return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
		}
		if (handStrength < getPotOdds(info))
			return new Fold(this);
		return info.currentBet + potSizeBet < getMyBet() + getMyStack() ? new Raise(this, info.currentBet + potSizeBet)
				: new AllIn(this);
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
	public void observe(Result resultInfo) {
		// Do nothing

	}

	@Override
	public String getName() {
		return "Hothead Maniac (ID = " + id + ")";
	}

	@Override
	public String toString() {
		return "HotheadManiacPlus";
	}

	Random rand;
	int position;
}
