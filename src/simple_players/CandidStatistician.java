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

public class CandidStatistician extends PlayerBase implements Statistician {

	public CandidStatistician(int id) {
		super(id);
		rand = new Random();
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		int potSizeBet = info.potSize + info.currentBet - getMyBet();
		if (info.currentBet == getMyBet()) {
			if (handStrength < 0.625)
				return new Check(this);
			int idealBet = info.currentBet
					+ (handStrength < 0.75 ? potSizeBet / 2 : (handStrength < 0.875 ? potSizeBet * 3 / 4 : potSizeBet));
			if (idealBet > getMyBet() + getMyStack())
				return new AllIn(this);
			return new Raise(this, info.BBAmt * (idealBet / info.BBAmt));
		}
		double foldprob = (info.potSize + info.currentBet - getMyBet()) / 2.0 / info.potSize;
		double potOdds = getPotOdds(info);
		if (handStrength < potOdds)
			return new Fold(this);
		if (handStrength < foldprob) {
			if (rand.nextDouble() < (handStrength - potOdds) / (foldprob - potOdds))
				return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
			return new Fold(this);
		}
		if (rand.nextDouble() < (handStrength - foldprob) / (1.0 - foldprob))
			return info.currentBet + potSizeBet < getMyBet() + getMyStack()
					? new Raise(this, info.BBAmt * ((info.currentBet + potSizeBet) / info.BBAmt)) : new AllIn(this);
		return new Call(this);
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
	
	@Override
	public String toString() {
		return "CandidStatistician";
	}

	Random rand;
}
