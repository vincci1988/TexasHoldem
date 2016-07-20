package players;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.PlayerBase;
import holdem.Raise;
import holdem.Result;
import holdem.Table;
import holdem.TableInfo;

public class CandidStatistician extends PlayerBase implements Statistician {

	public CandidStatistician(int id) {
		super(id);
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		double baseStrength = getBaseStrength(info);
		if (handStrength < baseStrength)
			return info.currentBet == getMyBet() ? new Check(this) : new Fold(this);
		int targetBet = (int) Math.round(
				(getMyBet() + getMyStack()) * Math.pow((handStrength - baseStrength) / (1.0 - baseStrength), conservativeness));
		if (info.currentBet >= targetBet) {
			if (info.currentBet == getMyBet())
				return new Check(this);
			if (info.currentBet < getMyBet() + getMyStack())
				return new Call(this);
			return new AllIn(this);
		}
		if (targetBet >= getMyBet() + getMyStack())
			return new AllIn(this);
		int diff = targetBet - info.currentBet - info.minRaise;
		if (diff >= 0) {
			return new Raise(this, info.currentBet + info.minRaise + (diff / info.BBAmt) * info.BBAmt);
		}
		return getMyBet() == info.currentBet ? new Check(this) : new Call(this);
	}

	private double getBaseStrength(TableInfo info) {
		int opponentCnt = info.playerInfos.size() - 1;
		return baseRateHeadsUp + (opponentCnt - 1) * (baseRateFullTable - baseRateHeadsUp) / (Table.seatCnt - 2);
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
		return "Candid Statistician (ID = " + id + ")";
	}

	//public static final String HSDBPath = "B:\\HSDB";
	private static final double conservativeness = 3.0;
	private static final double baseRateFullTable = 0.65;
	private static final double baseRateHeadsUp = 0.5;
	//private static HandStrengthEvaluator evaluator = new HandStrengthEvaluator(HSDBPath);
}
