package simple_players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Result;
import holdem.TableInfo;

public class ScaredLimper extends PlayerBase {

	public ScaredLimper(int id) {
		super(id);
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		if (info.board.length() > 0 && info.currentBet == 0)
			return new Check(this);
		if (info.board.length() == 0 && info.currentBet == info.BBAmt)
			return getMyBet() == info.BBAmt ? new Check(this) : new Call(this);
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
		return "Scared Limper (ID = " + id + ")";
	}

}
