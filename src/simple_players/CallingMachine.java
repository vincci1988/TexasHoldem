package simple_players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.PlayerBase;
import holdem.Result;
import holdem.TableInfo;

public class CallingMachine extends PlayerBase {

	public CallingMachine(int id) {
		super(id);
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		if (info.currentBet == getMyBet()) return new Check(this);
		else if (info.currentBet < getMyBet() + getMyStack()) return new Call(this);
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
		return "Calling Machine (ID = " + id + ")";
	}

}
