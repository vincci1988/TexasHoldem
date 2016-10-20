package evolvable_players;

import evolvable_players.CandidStatisticianGenome;
import evolvable_players.Evolvable;
import evolvable_players.GenomeBase;
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

public class CarefulStatistician extends PlayerBase implements Statistician, Evolvable {

	public CarefulStatistician(int id) {
		super(id);
		genome = new CandidStatisticianGenome(5.0, 0.5794769876671096, 0.7321911965473042);
	}

	public CarefulStatistician(int id, int version) {
		super(id);
		genome = new CandidStatisticianGenome(5.0, 0.5794769876671096, 0.7321911965473042);
	}

	public CarefulStatistician(int id, CandidStatisticianGenome candidStatisticianGenome) {
		super(id);
		this.genome = candidStatisticianGenome;
	}

	public CarefulStatistician(int id, int version, CandidStatisticianGenome candidStatisticianGenome) {
		super(id);
		this.genome = candidStatisticianGenome;
	}

	public double getConservativeness() {
		return genome.conservativeness;
	}

	public double getBaseRateFullTable() {
		return genome.baseRateFullTable;
	}

	public double getBaseRateHeadsUp() {
		return genome.baseRateHeadsUp;
	}

	@Override
	public GenomeBase getGenome() {
		return genome;
	}

	@Override
	public ActionBase getAction(TableInfo info) throws Exception {
		float myBet = 0;
		float opponentBet = 0, opponentStack = 0;
		if (info.playerInfos.size() != 2)
			throw new Exception("LSTMHeadsUPPlayer.getAction(TableInfo): playerInfo.size != 2");
		for (int i = 0; i < info.playerInfos.size(); i++) {
			if (info.playerInfos.get(i).id == id) {
				myBet = info.playerInfos.get(i).bet;
			} else {
				opponentBet = info.playerInfos.get(i).bet;
				opponentStack = info.playerInfos.get(i).stack;
			}
		}
		float previousBet = info.potSize - opponentBet - myBet;
		float opponentTotal = previousBet / 2 + opponentBet + opponentStack;
		double OC = (opponentBet + previousBet / 2) / opponentTotal;
		if (info.board.length() == 0) {
			if (opponentBet > 5 * info.BBAmt) OC = 0.65;
			else OC *= 0.65;
		}
		else OC *= 0.90;
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		double baseStrength = getPotOdds(info);
		if (OC > baseStrength)
			baseStrength = OC;
		//System.out.println(baseStrength + " " + OC);
		if (info.board.length() == 0 && OC == 0.65) return handStrength > OC ? new AllIn(this) : new Fold(this);
		else if (OC == 0.9) return handStrength > OC ? new AllIn(this) : new Fold(this);
		if (handStrength < baseStrength)
			return info.currentBet == getMyBet() ? new Check(this) : new Fold(this);
		int targetBet = (int) Math.round((getMyBet() + getMyStack())
				* Math.pow((handStrength - baseStrength) / (1.0 - baseStrength), genome.conservativeness));
		if (info.board.length() != 10) {
			if (targetBet > info.potSize + info.currentBet) targetBet = info.potSize + info.currentBet;
		}
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
		return "Careful Statistician (ID = " + id + ")";
	}

	public CandidStatisticianGenome genome;
}
