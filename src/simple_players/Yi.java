package simple_players;

import java.io.IOException;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Result;
import holdem.TableInfo;

public class Yi extends PlayerBase implements Statistician {

	public Yi(int id) {
		super(id);
		position = -1;
		range = 1.0;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws IOException, Exception {
		if (position == -1)
			position = 1;
		double rankStrength = evaluator.getRankStrength(peek(), info.board);
		if (position == 1 && info.potSize == info.BBAmt * 3 / 2) {
			return rankStrength > range * 0.25 ? new Call(this) : new Fold(this);
		}
		if (info.currentBet == getMyBet())
			return new Check(this);
		double delta = info.currentBet - getMyBet();
		double s = info.potSize;
		double defendRange = (s - delta) / (s + delta);
		range *= (1 - defendRange);
		return rankStrength > 1.0 - range
				? (info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this)) : new Fold(this);
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
		range = 1.0;
	}

	@Override
	public String getName() {
		return "I-Do-Not-Need-To-Raise (" + id + ")";
	}

	@Override
	public String toString() {
		return "I-Do-Not-Need-To-Raise";
	}

	int position;
	double range;
}
