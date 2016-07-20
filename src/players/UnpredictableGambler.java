package players;

import java.util.ArrayList;
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

public class UnpredictableGambler extends PlayerBase {

	public UnpredictableGambler(int id) {
		super(id);
		random = new Random();
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		ArrayList<Integer> actions = new ArrayList<Integer>();
		actions.add(4);
		if (info.currentBet == getMyBet())
			actions.add(1);
		else actions.add(0);
		if (info.currentBet > getMyBet() && info.currentBet < getMyBet() + getMyStack())
			actions.add(2);
		int minRaiseTo = info.currentBet + info.minRaise;
		int maxRaiseTo = getMyBet() + getMyStack();
		if (minRaiseTo < maxRaiseTo)
			actions.add(3);
		int action = actions.get(random.nextInt(actions.size()));
		if (action == 0)
			return new Fold(this);
		if (action == 1)
			return new Check(this);
		if (action == 2)
			return new Call(this);
		if (action == 4)
			return new AllIn(this);
		int range = maxRaiseTo - minRaiseTo;
		return new Raise(this, minRaiseTo + (int)(Math.pow(random.nextDouble(), conservativeness) * range));
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
		return "Unpredictable Gambler (ID = " + id + ")";
	}

	private Random random;
	private double conservativeness = 3.0;
}
