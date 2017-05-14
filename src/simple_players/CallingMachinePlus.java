package simple_players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Result;
import holdem.TableInfo;

public class CallingMachinePlus extends PlayerBase implements Statistician {

	public CallingMachinePlus(int id) {
		super(id);
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (info.currentBet == getMyBet()) 
			return new Check(this);
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		if (handStrength < getPotOdds(info))
			return new Fold(this);
		if (info.currentBet < getMyBet() + getMyStack()) return new Call(this);
		return new AllIn(this);
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
		return "Calling Machine Plus (ID = " + id + ")";
	}
	
	@Override
	public String toString() {
		return "CallingMachinePlus";
	}

}
