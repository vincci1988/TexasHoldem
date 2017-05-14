package simple_players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.PlayerBase;
import holdem.Raise;
import holdem.Result;
import holdem.TableInfo;

public class HotheadManiac extends PlayerBase {

	public HotheadManiac(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		int bet = info.currentBet + (info.potSize + info.currentBet - getMyBet()); //pot-size
		if (bet < getMyBet() + getMyStack()) return new Raise(this, bet);
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
		return "Hothead Maniac (ID = " + id + ")";
	}

}
