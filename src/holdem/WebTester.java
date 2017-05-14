package holdem;

import exp_web.WebHUNLAPI;

public class WebTester extends PlayerBase {
	public WebTester(int id, String name, WebHUNLAPI web) {
		super(id);
		this.name = name;
		this.web = web;
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		char decision = web.getActtion();
		if (decision == 'k') {
			return new Check(this);
		}
		if (decision == 'c') {
			if (this.getMyBet() == info.currentBet) return new Check(this);
			return this.getMyBet() + this.getMyStack() > info.currentBet ? new Call(this) : new AllIn(this);
		}
		if (decision == 'b') {
			int amt = web.getBet();
			return this.getMyBet() + this.getMyStack() > amt ? new Raise(this, amt) : new AllIn(this);
		}
		return new Fold(this);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (actionInfo.playerID != this.id) {
			if (actionInfo instanceof FoldInfo) 
				web.fold();
			if (actionInfo instanceof CheckInfo) 
				web.check();
			if (actionInfo instanceof CallInfo)
				web.call();
			if (actionInfo instanceof RaiseInfo)
				web.raise(actionInfo.amt);
			if (actionInfo instanceof AllInInfo)
				web.allin();
			web.oppmoved();
		}
	}

	@Override
	public void observe(Result resultInfo) {
		web.reset();
	}

	@Override
	public String getName() {
		return name + " (ID = " + id +")";
	}
	
	private String name;
	private WebHUNLAPI web;
}
