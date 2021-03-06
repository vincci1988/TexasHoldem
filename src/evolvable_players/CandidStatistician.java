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
import holdem.GameTable;
import holdem.TableInfo;

public class CandidStatistician extends PlayerBase implements Statistician, Evolvable {

	public CandidStatistician(int id) {
		super(id);
		genome = new CandidStatisticianGenome(7.0, 0.5794769876671096, 0.7321911965473042);
		this.version = 2;
	}

	public CandidStatistician(int id, int version) {
		super(id);
		genome = new CandidStatisticianGenome(3.2068309283146066, 0.5794769876671096, 0.7321911965473042);
		this.version = version;
	}

	public CandidStatistician(int id, CandidStatisticianGenome candidStatisticianGenome) {
		super(id);
		this.version = 2;
		this.genome = candidStatisticianGenome;
	}

	public CandidStatistician(int id, int version, CandidStatisticianGenome candidStatisticianGenome) {
		super(id);
		this.version = version;
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
		double handStrength = evaluator.getHandStength(peek(), info.board, info.playerInfos.size() - 1);
		double baseStrength = version == 1 ? genome.baseRateHeadsUp : 0.5;
		if (handStrength < (info.board.length() == 0 ? getPotOdds(info) : 0.5))
			return info.currentBet == getMyBet() ? new Check(this) : new Fold(this);
		int targetBet = (int) Math.round((getMyBet() + getMyStack())
				* Math.pow((handStrength - baseStrength) / (1.0 - baseStrength), genome.conservativeness));
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

	@SuppressWarnings("unused")
	private double getBaseStrength(TableInfo info) {
		int opponentCnt = info.playerInfos.size() - 1;
		return genome.baseRateHeadsUp
				+ (opponentCnt - 1) * (genome.baseRateFullTable - genome.baseRateHeadsUp) / (GameTable.seatCnt - 2);
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

	public CandidStatisticianGenome genome;
	public final int version;
}
