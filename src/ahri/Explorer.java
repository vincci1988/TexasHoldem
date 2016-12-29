package ahri;

import java.util.Vector;

import evolvable_players.LSTMNoLimitTester;
import holdem.ActionBase;
import holdem.Fold;
import holdem.TableInfo;
import opponent_model.Intel;

public class Explorer extends Controller {

	Explorer(Ahri me) throws Exception {
		super(me);
		tester = new LSTMNoLimitTester(me.getID(), "explorerGenome.txt");
	}
	
	public void matchStart() {
		tester.matchStart();
	}
	
	public void gameStart() {
		tester.gameStart();
	}

	@Override
	public ActionBase recommend(Vector<ActionBase> actions, TableInfo info, String holeCards, Intel intel)
			throws Exception {
		init(actions, info, intel);
		int best = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < actions.size(); i++) {
			double score = actions.get(i) instanceof Fold ? Double.NEGATIVE_INFINITY: -moves[i][0];;
			if (score > maxScore) {
				maxScore = score;
				best = i;
			}
		}
		return actions.get(best);
	}
	
	LSTMNoLimitTester tester;
}
