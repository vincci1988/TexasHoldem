package simple_players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Result;
import holdem.TableInfo;

public class ScaredLimperPlus extends PlayerBase implements ASHE.Statistician {

	public ScaredLimperPlus(int id) {
		super(id);
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		double handStrength = evaluator.getHandStength(peek(), info.board);
		if (info.currentBet == getMyBet())
			return new Check(this);
		double mustdefend = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		if (info.board.length() == 0) {
			if (info.currentBet == info.BBAmt)
				return new Call(this);
			if (handStrength > Math.min(mustdefend, 0.80))
				return new Call(this);
			return new Fold(this);
		}
		if (handStrength > (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize)
			return new Call(this);
		return new Fold(this);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		// Do nothing
	}

	@Override
	public void observe(Result resultInfo) {
		// Do nothing
	}

	@Override
	public String getName() {
		return "Scared Limper Plus (ID = " + id + ")";
	}
	
	@Override
	public String toString() {
		return "ScaredLimperPlus";
	}

}
