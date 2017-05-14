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

public class WildGambler extends PlayerBase implements Statistician {

	public WildGambler(int id) {
		super(id);
		rand = new Random();
		cnt = span;
		action = 0;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		if (cnt == span) { 
			action = rand.nextInt(4);
			cnt = 0;
		}
		cnt++;
		if (action == 0)
			return getActionCM(info);
		if (action == 1)
			return getActionHM(info);
		if (action == 2)
			return getActionSL(info);
		return getActionCS(info);
	}
	
	ActionBase getActionCM(TableInfo info) {
		if (info.currentBet == getMyBet()) return new Check(this);
		else if (info.currentBet < getMyBet() + getMyStack()) return new Call(this);
		return new AllIn(this);
	}
	
	ActionBase getActionHM(TableInfo info) {
		int bet = info.currentBet + (info.potSize + info.currentBet - getMyBet()); //pot-size
		if (bet < getMyBet() + getMyStack()) return new Raise(this, bet);
		return new AllIn(this);
	}
	
	ActionBase getActionSL(TableInfo info) throws Exception {
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
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
		if (handStrength > mustdefend)
			return new Call(this);
		return new Fold(this);
	}
	
	ActionBase getActionCS(TableInfo info) throws Exception {
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
	public void matchStart() {
		cnt = span;
		action = 0;
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

	private Random rand;
	final int span = 50;
	int cnt;
	int action;
}
