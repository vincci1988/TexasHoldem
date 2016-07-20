package holdem;

public class RaiseInfo extends ActionInfoBase {

	RaiseInfo(ActionBase action, Board board) {
		super(action, board);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " RAISE ($"
				+ amt + ") <END PLAYER ACTION>";
	}
}
