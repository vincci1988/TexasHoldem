package simple_players;

import java.util.Random;

import evolvable_players.Statistician;
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

public class Villian extends PlayerBase implements Statistician {

	public Villian(int id) {
		super(id);
		rand = new Random();
		bluffing = false;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {

		float opponentBet = 0, opponentStack = 0;
		if (info.playerInfos.size() != 2)
			throw new Exception("LSTMHeadsUPPlayer.getAction(TableInfo): playerInfo.size != 2");
		for (int i = 0; i < info.playerInfos.size(); i++) {
			if (info.playerInfos.get(i).id != id) {
				opponentBet = info.playerInfos.get(i).bet;
				opponentStack = info.playerInfos.get(i).stack;
			}
		}
		double previousBet = info.potSize - opponentBet - getMyBet();
		double opponentTotal = previousBet / 2 + opponentBet + opponentStack;
		int pot = info.currentBet - getMyBet() + info.potSize;

		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);

		if (info.board.length() == 0)
			return preflop(info, handStrength, opponentBet, opponentTotal, pot);

		return postflop(info, handStrength, opponentBet, opponentTotal, previousBet, pot);
	}

	public void gameStart() {
		bluffing = false;
	}

	public void matchStart() {
		// Defualt implementation: Do nothing;
	}

	private ActionBase preflop(TableInfo info, double handStrength, double opponentBet, double opponentTotal, int pot) {
		// 1. opponent called
		if (info.currentBet == getMyBet()) {
			int ideal = info.currentBet + 3 * pot;
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}
		// 2. opponent has not made her move
		if (getMyBet() == info.BBAmt / 2) {
			int ideal = info.currentBet + 2 * pot;
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}

		// opponent raised
		if (handStrength > 0.66) {
			int ideal = info.currentBet + 2 * pot;
			return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
		}
		
		if (opponentBet / opponentTotal <= 0.2){
			if (info.currentBet < 500 * info.BBAmt) {
				int ideal = info.currentBet + pot;
				return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
			}
			return new Call(this);
		}
			
		double callingThreshold = 0.35 + 0.31 * ((opponentBet / opponentTotal) - 0.2) / 0.8;
		if (handStrength < callingThreshold) return new Fold(this);
		return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
	}

	private ActionBase postflop(TableInfo info, double handStrength, double opponentBet, double opponentTotal,
			double prev, int pot) {
		if (bluffing)
			handStrength = 0.9;
		if (info.currentBet == getMyBet()) {
			// 1. opponent checked
			if (info.playerInfos.get(0).id != id) {
				if (!bluffing && handStrength < 0.5)
					bluffing = true;
				int ideal = info.currentBet
						+ (info.board.length() == 10 ? 2 : 1) * (rand.nextDouble() < 0.5 ? pot / 2 : pot);
				return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
			}
			// 2. opponent has not made her move
			else {
				if (handStrength < 0.5) {
					if (!bluffing)
						bluffing = true;
					handStrength = 1 - handStrength;
				}
				if (handStrength < 0.75) {
					double semibluffProb = (handStrength - 0.5) / 0.25;
					if (rand.nextDouble() < semibluffProb) {
						int ideal = info.currentBet + pot / 2;
						return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
					}
					return new Check(this);
				}
				double highRaiseProb = (handStrength - 0.75) / 0.25;
				if (rand.nextDouble() < highRaiseProb) {
					int ideal = info.currentBet + (info.board.length() == 10 ? 2 : 1) * pot;
					return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
				}
				int ideal = info.currentBet + (info.board.length() == 10 ? pot : pot / 2);
				return ideal < getMyBet() + getMyStack() ? new Raise(this, ideal) : new AllIn(this);
			}
		}

		// opponent raised
		double potOdds = getPotOdds(info);
		double[] pivots = new double[3];
		pivots[0] = 1 / (1 - potOdds) - 5.0 / 6.0;
		pivots[1] = 0.6 / (1 - potOdds) - 0.1;

		if (bluffing && opponentBet + prev / 2 == opponentTotal)
			return new Fold(this);

		if (handStrength <= pivots[0]) {
			return new Fold(this);
		}

		if (handStrength <= pivots[1]) {
			double callProb = (handStrength - pivots[0]) / (pivots[1] - pivots[0]);
			if (rand.nextDouble() < callProb)
				return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
			return new Fold(this);
		}

		double reRaiseProb = (handStrength - pivots[1]) / (1.0 - pivots[1]);
		if (rand.nextDouble() < reRaiseProb) {
			int idealRaise = info.currentBet + (info.minRaise > pot / 2 ? info.minRaise : pot / 2);
			return idealRaise < getMyBet() + getMyStack() ? new Raise(this, idealRaise) : new AllIn(this);
		}
		return info.currentBet < getMyBet() + getMyStack() ? new Call(this) : new AllIn(this);
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
		return "Villian (ID = " + id + ")";
	}

	private Random rand;
	private boolean bluffing;
}
